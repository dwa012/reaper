namespace :reaper do
  desc 'Create model Objective C classes for iOS'

  task :ios => :environment do
    models = ARGV[1].nil? ? [] : ARGV[1].split(',')
    task ARGV[1].to_sym do
    end unless models.empty?


    # set what the output directories are
    model_output_dir = "#{Rails.root}/generated_files/mobile_app/ios/models/"
    db_output_dir = "#{Rails.root}/generated_files/mobile_app/ios/database/"
    remote_output_dir = "#{Rails.root}/generated_files/mobile_app/ios/remote_data/"
    api_output_dir = "#{Rails.root}/generated_files/mobile_app/ios/api/"
    restkit_output_dir = "#{Rails.root}/generated_files/mobile_app/ios/restkit/"

    # make the directories for the generated files
    FileUtils.makedirs model_output_dir unless File.exists?(model_output_dir)
    FileUtils.makedirs db_output_dir unless File.exists?(db_output_dir)
    FileUtils.makedirs remote_output_dir unless File.exists?(remote_output_dir)
    FileUtils.makedirs restkit_output_dir unless File.exists?(restkit_output_dir)
    FileUtils.makedirs api_output_dir unless File.exists?(api_output_dir)

    # force load of Models so we can iterate over them
    Rails.application.eager_load!

    # create the database helper files
    db_helper_h(db_output_dir, models)
    db_helper_m(db_output_dir, models)
    api_controller_h(api_output_dir,models)
    api_controller_m(api_output_dir,models)
    generic_response_class(restkit_output_dir)

    # create the .h and .m files for the models
    ActiveRecord::Base.descendants.each do |m|
      if models.empty? or models.include?(m.name)
        model_h_file(m, model_output_dir)
        model_m_file(m, model_output_dir)
        model_datasource_h(m, db_output_dir)
        model_datasource_m(m, db_output_dir)
        model_remote_h(m,remote_output_dir)
        model_remote_m(m,remote_output_dir)
        model_restkit_wrapper_h(m, restkit_output_dir)
        model_restkit_wrapper_m(m, restkit_output_dir)
      end
    end # end of Model iteration
  end


  def db_helper_h(output_dir, models)

    out = ''
    to_insert = []

    to_insert << '//table names for database'

    # create the names of the tables
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        to_insert << "#define DATABASE_TABLE_#{model.name.underscore.upcase} @\"#{model.name.downcase}\""
      end
    end

    to_insert << ''
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        to_insert << "//column keys for #{model.name}"

        to_insert << "#define DATABASE_KEY_#{model.name.underscore.upcase}_DB_ID @\"_id\""

        get_attr(model).each do |column|
          to_insert << "#define DATABASE_KEY_#{model.name.underscore.upcase}_#{column.underscore.upcase} @\"#{column}\""
        end

        to_insert << "#define DATABASE_KEY_#{model.name.underscore.upcase}_VERSION @\"version\""

        to_insert << ''
      end
    end


    out << <<-eod
#import <sqlite3.h>
#import <RestKit/RestKit.h>

// Database version
#define VERSION 1

// Database constants
#define DATABASE_TABLE_VERSION @"version"
#define DATABASE_TABLE_VERSION_KEY @"version"
#define DATABASE_CREATE_VERSION_TABLE @"CREATE TABLE IF NOT EXISTS version ( _id INTEGER PRIMARY KEY AUTOINCREMENT, version TEXT )"

#{to_insert.join("\n")}


@interface DataBaseHelper : NSObject {
   sqlite3 *_database;
}

// constructor
- (id)init;

// database access methods
- (BOOL)open;
- (BOOL)close;
- (sqlite3*)database;

@end
    eod
    File.open("#{output_dir}DataBaseHelper.h", 'w') { |file| file.write out }
    puts 'Wrote: ' << 'DataBaseHelper.h'
  end

  def db_helper_m(output_dir, models)
    out = ''

    # build the create table string objects
    create_table_string_variables = []
    create_table_string_variables << '// The create table statement variables'
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        create_table_string_variables << "NSString* CREATE_#{model.name.underscore.upcase}_TABLE;"
      end
    end

    # generate the create table statements
    create_table_string_values = []

    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        create_table_string_values << "//column keys for #{model.name}"
        create_table_string_values << "CREATE_#{model.name.underscore.upcase}_TABLE = [[NSString alloc] initWithFormat:@\"CREATE TABLE IF NOT EXISTS \\"


        create_table_string_values << '%@ ( \\'
        create_table_string_values << '%@ %@ PRIMARY KEY AUTOINCREMENT, \\'

        get_attr(model).each do |column|
          create_table_string_values << "%@ %@ #{ 'UNIQUE' if column.to_s == model.primary_key }, \\"
        end

        create_table_string_values << '%@ %@ )",'

        create_table_string_values << "DATABASE_TABLE_#{model.name.underscore.upcase},"
        create_table_string_values << "DATABASE_KEY_#{model.name.underscore.upcase}_DB_ID, @\"INTEGER\","
        #create_table_string_values << "DATABASE_KEY_#{model.name.underscore.upcase}_REMOTE_ID, @\"INTEGER\","


        get_attr(model).each do |name|
          column = get_column_by_name(model,name)
          type = 'TEXT'

          if column.type.to_s == 'integer'
            type = 'INTEGER'
          elsif column.type.to_s == 'string' || column.type.to_s == 'text'
            type = 'TEXT'
          elsif column.type.to_s == 'datetime'
            type = 'TEXT'
          elsif column.type.to_s == 'boolean'
            type = 'BOOLEAN'
          elsif column.type.to_s == 'float' or column.type.to_s == 'double'
            type = 'TEXT'
          end

          create_table_string_values << "DATABASE_KEY_#{model.name.underscore.upcase}_#{column.name.upcase}, @\"#{type}\","
        end

        create_table_string_values << "DATABASE_KEY_#{model.name.underscore.upcase}_VERSION, @\"TEXT\""
        create_table_string_values << '];'
        create_table_string_values << ''
      end
    end

    # create the lines for the sql statement execution
    create_table_statements = []
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        create_table_statements << "if (sqlite3_exec([self database], [CREATE_#{model.name.underscore.upcase}_TABLE UTF8String], NULL, NULL, &errMsg) != SQLITE_OK) {"
        create_table_statements << "  NSLog(@\"Failed to create table - #{model.name.underscore}\");"
        create_table_statements << '}'
      end
    end

    # create the lines to drop the tables
    drop_table_statements = []
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        drop_table_statements << "if (sqlite3_exec([self database], [[[NSString alloc] initWithFormat:@\"DROP TABLE IF EXISTS %@\",DATABASE_TABLE_#{model.name.underscore.upcase}] UTF8String], NULL, NULL, &errMsg) != SQLITE_OK) {"
        drop_table_statements << "  NSLog(@\"Failed to drop table - #{model.name.underscore}\");"
        drop_table_statements << '}'
      end
    end

    out << <<-eod

#import "DataBaseHelper.h"
// The name of the database file
#define DATABASE_NAME @"reaper_database.sqlite3"

@implementation DataBaseHelper

#{create_table_string_variables.join("\n")}


- (id)init {
  if ((self = [super init])) {
    if([self open]){
      [self checkForUpdate];
      [self initializeCreateTableStatements];
      [self createTables];
    }

    [self close];
  }
  return self;
}

- (void)dealloc {
  sqlite3_close(_database);
}

- (BOOL)open {
  return sqlite3_open([[self dataFilePath] UTF8String], &_database) == SQLITE_OK;
}

- (BOOL)close {
  return sqlite3_close(_database) == SQLITE_OK;
}

- (sqlite3*)database {
  return _database;
}

- (void)onUpdateDatabase {
  // put the update database code here
  // drops the current table as a template

  char *errMsg;
  #{drop_table_statements.join("\n\t")}

  //recreate the tables
  [self initializeCreateTableStatements];
}


- (void)createTables {
  if([self open]){
    char *errMsg;
    if (sqlite3_exec([self database], [DATABASE_CREATE_VERSION_TABLE UTF8String], NULL, NULL, &errMsg) != SQLITE_OK) {
      NSLog(@"Failed to create table - version %s", errMsg);
    }
    #{create_table_statements.join("\n\t\t")}
  }

  [self close];
}

-(void)initializeCreateTableStatements {
  #{create_table_string_values.join("\n  ")}
}

-(void)checkForUpdate {
  NSNumber* result = [NSNumber numberWithInt:0];

  sqlite3_stmt *statement;

  NSString *querySQL = [NSString stringWithFormat: @"SELECT max(%@) FROM %@",
                        DATABASE_TABLE_VERSION_KEY,
                        DATABASE_TABLE_VERSION];

  const char *query_stmt = [querySQL UTF8String];

  if (sqlite3_prepare_v2([self database], query_stmt, -1, &statement, NULL) == SQLITE_OK)
  {
    if (sqlite3_step(statement) == SQLITE_ROW)
    {
      result = [NSNumber numberWithInt:sqlite3_column_int(statement, 0)];
    } else {
      NSLog(@"version not found");
    }

    sqlite3_finalize(statement);
  }

  if ([result intValue] < VERSION) {
    [self onUpdateDatabase];


    BOOL result = NO;
    sqlite3_stmt *statement;

    NSString *insertSQL = [NSString stringWithFormat: @"INSERT INTO %@ (%@) VALUES (?)",
                           DATABASE_TABLE_VERSION,
                           DATABASE_TABLE_VERSION_KEY];

    if ( sqlite3_prepare_v2([self database], [insertSQL UTF8String], -1, &statement, NULL) == SQLITE_OK)
    {
      sqlite3_bind_int(statement, 1, VERSION);
    }


    result = (sqlite3_step(statement) == SQLITE_DONE);

    if (result == NO){
      NSLog(@"DB ERROR: %s", sqlite3_errmsg([self database]));
    }

    sqlite3_finalize(statement);

  }
}

-(NSString *)dataFilePath {
  NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
  NSString *documentsDirectory = [paths objectAtIndex:0];
  return [documentsDirectory stringByAppendingPathComponent:DATABASE_NAME];
}


@end
    eod

    File.open("#{output_dir}DataBaseHelper.m", 'w') { |file| file.write out }
    puts 'Wrote: ' << 'DataBaseHelper.m'

  end

  def model_h_file(model, output_dir)
    # Writing the .h file

    col = get_column_by_name(model, model.primary_key.instance_of?(String) ? model.primary_key : model.primary_key[0])
    primary_key_type = sql_type_to_objc(col)

    #generate the property statements
    properties = []

    get_attr(model).each do |attribute|
      if attribute == 'id'
        properties << "@property (nonatomic, retain) #{primary_key_type} remoteId;"
      else
        properties << create_properties(get_column_by_name(model,attribute))
      end
    end

    # generate the class header
    (out = '') << <<-eod
#import <RestKit/RestKit.h>

@interface #{model.name.camelize} : NSObject

#{properties.join("\n")}
// the version number from the server for this object
@property (nonatomic, retain) NSString* version;

+ (RKObjectMapping *) getObjectMapping;
- (NSDictionary*) toDictionary;
- (#{primary_key_type}) remotePrimaryKey;
@end
    eod
    puts 'Wrote: ' << "#{output_dir}#{model.name.camelize}.h"
    File.open("#{output_dir}#{model.name.camelize}.h", 'w') { |file| file.write out }
  end

  def model_m_file(model, output_dir)
    primary_key = model.primary_key.instance_of?(String) ? model.primary_key : model.primary_key[0]
    primary_key_type = sql_type_to_objc(get_column_by_name(model, primary_key))

    synthesize_statements = []
    mapping_statements = []

    synthesize_statements << "@synthesize #{primary_key == 'id' ? 'remoteId' : primary_key} = _id;"
    mapping_statements << "@\"#{primary_key}\" : @\"#{primary_key == 'id' ? 'remoteId' : primary_key.camelize(:lower)}\""

    get_attr(model).each do |attribute|
      #if attribute == 'id'
      #  synthesize_statements << '@synthesize remoteId = _id;'
      #  mapping_statements << "@\"#{primary_key}\" : @\"remoteId\""
      #else
        synthesize_statements << "@synthesize #{attribute.camelize(:lower)} = _#{attribute.camelize(:lower)};"
        mapping_statements << "@\"#{attribute}\" : @\"#{attribute.camelize(:lower)}\""
      #end
    end

    # generate the model to dictionary statements
    dictionary_statements = []
    get_attr(model).each do |attribute|
      unless attribute == model.primary_key # skip the primary key, no need to send it back to the server
        sql_type = get_column_by_name(model, attribute).type.to_s unless get_column_by_name(model, attribute).nil?

        if sql_type == 'datetime' || sql_type == 'date'
          dictionary_statements << "  if(_#{attribute.camelize(:lower)}) [result setObject:[formatter stringFromDate:_#{attribute.camelize(:lower)}] forKey:@\"#{attribute}\"];"
        elsif sql_type == 'boolean'
          dictionary_statements << "  if(_#{attribute.camelize(:lower)}) [result setObject:[NSNumber numberWithBool:_#{attribute.camelize(:lower)}] forKey:@\"#{attribute}\"];"
        else
          dictionary_statements << "  if(_#{attribute.camelize(:lower)}) [result setObject:_#{attribute.camelize(:lower)} forKey:@\"#{attribute}\"];"
        end
      end
    end

    # generate the class body
    (out = '') << <<-eod
#import "#{model.name.camelize}.h"
@implementation #{model.name.camelize}

    #{synthesize_statements.join("\n")}
@synthesize version = _version;

+ (RKObjectMapping *) getObjectMapping
{
  RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[#{model.name.camelize} class]];
  [mapping addAttributeMappingsFromDictionary:@{
  #{mapping_statements.join(",\n\t")}
  }];
  return mapping;
}

 - (NSDictionary*) toDictionary
{
  NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
  [formatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ss.SSSZ"];

  NSMutableDictionary* result =  [NSMutableDictionary dictionary];
#{dictionary_statements.join("\n")}
  return result;
}


- (#{primary_key_type}) remotePrimaryKey {
  return _#{primary_key.camelize(:lower)};
}
@end
    eod
    File.open("#{output_dir}#{model.name.camelize}.m", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}#{model.name.camelize}.m"
  end

  def model_datasource_h(model, output_dir)
    sql_col = get_column_by_name(model, model.primary_key.instance_of?(String) ? model.primary_key : model.primary_key[0])
    objc_type = sql_type_to_objc(sql_col)

    (out = '') << <<-eod
#import "DataBaseHelper.h"
#import "#{model.name.camelcase}.h"

@interface #{model.name.camelcase}DataSource : NSObject {
   DataBaseHelper* database;
}

// constructor
- (id)init;

// database access methods
- (BOOL)open;
- (BOOL)close;
- (#{model.name.camelize}*)getWithRemotePrimaryKey:(#{objc_type})remotePrimaryKey;
- (NSArray*)getAll;
- (BOOL)addItem:(#{model.name.camelize}*)item itemVersion:(NSString*)version;
- (BOOL)remove:(#{objc_type})remotePrimaryKey;
- (BOOL)update:(#{objc_type})remotePrimaryKey itemToAdd:(#{model.name.camelize}*)item;
- (NSString*)getMaxVersion;

@end
    eod

    File.open("#{output_dir}#{model.name.camelcase}DataSource.h", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}#{model.name.camelcase}DataSource.h"
  end

  def model_datasource_m(model, output_dir)

    index = 1
    insert_item_statements = []
    get_attr(model).each do |column|
      sql_type = get_type_from_column(model, column)
      property = column == 'id' ? 'remoteId' : column.camelcase(:lower)

      if sql_type == 'integer'
        insert_item_statements << "sqlite3_bind_int(statement, #{index}, [[item #{property}] intValue]);"
      elsif sql_type == 'string' or sql_type == 'text'
        insert_item_statements << "sqlite3_bind_text(statement, #{index}, [[item #{property}] UTF8String], -1, SQLITE_TRANSIENT);"
      elsif sql_type == 'datetime'
        insert_item_statements << "sqlite3_bind_text(statement, #{index}, [[formatter stringFromDate:[item #{property}]] UTF8String], -1, SQLITE_TRANSIENT);"
      elsif sql_type == 'boolean'
        insert_item_statements << "sqlite3_bind_int(statement, #{index}, [item #{property}] ? 1 : 0);"
      elsif sql_type == 'float' or sql_type == 'double' or sql_type == 'decimal'
        insert_item_statements << "sqlite3_bind_text(statement, #{index}, [[[item #{property}] stringValue] UTF8String], -1, SQLITE_TRANSIENT);"
      else
        insert_item_statements << "// THIS ATTRIBUTE -- #{property.upcase} -- DOES NOT CONFORM TO STANDARD SQL DATA TYPE"
        insert_item_statements << "sqlite3_bind_null(statement, #{index});"
      end

      index = index + 1
    end
    insert_item_statements << "sqlite3_bind_text(statement, #{index}, [version UTF8String], -1 , SQLITE_TRANSIENT);"

    index = 1
    build_item_statements = []
    get_attr(model).each do |column|
      sql_type = get_type_from_column(model, column)
      property = column == 'id' ? 'RemoteId' : column.camelcase

      if sql_type == 'integer'
        build_item_statements << "[result set#{property}:sqlite3_column_type(statement, #{index}) != SQLITE_NULL ? [NSNumber numberWithInt:sqlite3_column_int(statement, #{index})] : NULL ];"
      elsif sql_type == 'string' || sql_type == 'text'
        build_item_statements << "[result set#{property}:sqlite3_column_type(statement, #{index}) != SQLITE_NULL ? [NSString stringWithUTF8String:(char *)sqlite3_column_text(statement, #{index})] : NULL];"
      elsif sql_type == 'datetime'
        build_item_statements << "[result set#{property}:sqlite3_column_type(statement, #{index}) != SQLITE_NULL ? [formatter dateFromString: [NSString stringWithUTF8String:(char *)sqlite3_column_text(statement, #{index})]] : NULL];"
      elsif sql_type == 'boolean'
        build_item_statements << "[result set#{property}:sqlite3_column_type(statement, #{index}) != SQLITE_NULL ? (sqlite3_column_int(statement, #{index}) > 0) : 0];"
      elsif sql_type == 'float' or sql_type == 'double' or sql_type == 'decimal'
        build_item_statements << "[result set#{property}:sqlite3_column_type(statement, #{index}) != SQLITE_NULL ? [numberFormatter numberFromString:NSString stringWithUTF8String:(char *)sqlite3_column_text(statement, #{index})]] : NULL];"
      else
        build_item_statements << "#warning Type unknown for this attribute - #{property}"
      end

      index = index + 1
    end
    build_item_statements << "[result setVersion:sqlite3_column_type(statement, #{index}) != SQLITE_NULL ? [NSString stringWithUTF8String:(char *)sqlite3_column_text(statement, #{index})] : NULL];"

    insert_keys = []
    get_attr(model).each { |e| insert_keys << "DATABASE_KEY_#{model.name.underscore.upcase}_#{e.underscore.upcase}," }
    insert_keys << "DATABASE_KEY_#{model.name.underscore.upcase}_VERSION"


    sql_col = get_column_by_name(model, model.primary_key.instance_of?(String) ? model.primary_key : model.primary_key[0])
    sql_type = get_type_from_column(model, sql_col)
    objc_type = sql_type_to_objc(sql_col)

    primary_key = model.primary_key.instance_of?(String) ? model.primary_key : model.primary_key[0]

    bind_statement_for_remove = ''
    if sql_type == 'integer'
      bind_statement_for_remove = 'sqlite3_bind_int(statement, 1, [remotePrimaryKey intValue]);'
    elsif sql_type == 'string' or sql_type == 'text'
      bind_statement_for_remove = 'sqlite3_bind_text(statement, 1, [remotePrimaryKey UTF8String], -1, SQLITE_TRANSIENT);'
    elsif sql_type == 'datetime'
      bind_statement_for_remove = 'sqlite3_bind_text(statement, 1, [[formatter stringFromDate:remotePrimaryKey] UTF8String], -1, SQLITE_TRANSIENT);'
    elsif sql_type == 'boolean'
      bind_statement_for_remove = 'sqlite3_bind_int(statement, 1, remotePrimaryKey ? 1 : 0);'
    elsif sql_type == 'float' or sql_type == 'double' or sql_col == 'decimal'
      bind_statement_for_remove = 'sqlite3_bind_text(statement, 1, [[remotePrimaryKey stringValue] UTF8String], -1, SQLITE_TRANSIENT);'
    end

    out = ''
    out << <<-eod
#import "#{model.name.camelize}DataSource.h"

@implementation #{model.name.camelize}DataSource

- (id)init
{
  if ((self = [super init])) {
    database = [[DataBaseHelper alloc] init];
  }
  return self;
}

- (BOOL)open
{
  return [database open];
}

- (BOOL)close
{
	return [database close];
}

- (#{model.name.camelize}*)getWithRemotePrimaryKey:(#{objc_type})remotePrimaryKey
{
  #{model.name.camelize} *result;

  sqlite3_stmt *statement;

  NSString *querySQL = [NSString stringWithFormat: @"SELECT * FROM %@ WHERE %@ = ?",
                                                    DATABASE_TABLE_#{model.name.underscore.upcase},
                                                    DATABASE_KEY_#{model.name.underscore.upcase}_#{primary_key.underscore.upcase}];

  sqlite3 *db = [database database];

  const char *query_stmt = [querySQL UTF8String];

  if (sqlite3_prepare_v2(db, query_stmt, -1, &statement, NULL) == SQLITE_OK)
  {
    #{bind_statement_for_remove}

    if (sqlite3_step(statement) == SQLITE_ROW)
    {
      result = [self queryResultToItem:statement];
    } else {
      NSLog(@"Item not found");
    }

    sqlite3_finalize(statement);
  }

  return result;
}

- (NSArray*)getAll
{
  NSMutableArray *result = [ [NSMutableArray alloc] init];

  sqlite3_stmt *statement;

  NSString *querySQL = [NSString stringWithFormat: @"SELECT * FROM %@", DATABASE_TABLE_#{model.name.underscore.upcase}];

  const char *query_stmt = [querySQL UTF8String];

  if (sqlite3_prepare_v2([database database], query_stmt, -1, &statement, NULL) == SQLITE_OK)
  {
  	while (sqlite3_step(statement) == SQLITE_ROW)
  	{
    	[result addObject:[self queryResultToItem:statement]];
    }
  }
  sqlite3_finalize(statement);

  return result;

}

// private method to convert a result statement to an Item
- (#{model.name.camelize}*) queryResultToItem: (sqlite3_stmt*)statement
{
  #{model.name.camelize} * result = [[#{model.name.camelize} alloc]init];
  NSNumberFormatter * numberFormatter = [[NSNumberFormatter alloc] init];
  [numberFormatter setNumberStyle:NSNumberFormatterDecimalStyle];

  NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
  [formatter setDateFormat:@\"yyyy-MM-dd'T'HH:mm:ss.SSSZ\"];

  #{build_item_statements.join("\n\t")}

  return result;
}

- (BOOL)addItem:(#{model.name.camelize}*)item itemVersion:(NSString*)version
{

  if ([self getWithRemotePrimaryKey:[item remotePrimaryKey]] != nil) {
    [self remove:[item remotePrimaryKey]];
  }

  BOOL result = NO;
	sqlite3_stmt *statement;

  NSString *insertSQL = [NSString stringWithFormat: @\"INSERT INTO %@ \\
                                                    (#{ a = []; get_attr(model).count.times do; a << '%@'; end; a.join(',') }, %@) \\
                                                    VALUES \\
                                                    (#{ a = []; get_attr(model).count.times do; a << '?'; end; a.join(',') }, ?)",
                                                    DATABASE_TABLE_#{model.name.underscore.upcase},
                                                    #{insert_keys.join("\n")}];

	if ( sqlite3_prepare_v2([database database], [insertSQL UTF8String], -1, &statement, NULL) == SQLITE_OK)
  {
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@\"yyyy-MM-dd'T'HH:mm:ss.SSSZ\"];

    #{insert_item_statements.join("\n\t\t")}
  }

	result = (sqlite3_step(statement) == SQLITE_DONE);

  if (result == NO){
    NSLog(@"DB ERROR: %s", sqlite3_errmsg([database database]));
  }

  sqlite3_finalize(statement);

  return result;
}

- (BOOL)remove:(#{objc_type})remotePrimaryKey
{
  BOOL result = YES;
  sqlite3_stmt *statement;

	NSString *removeSQL = [NSString stringWithFormat: @"DELETE FROM %@ WHERE %@ = ?",
                                                    DATABASE_TABLE_#{model.name.underscore.upcase},
                                                    DATABASE_KEY_#{model.name.underscore.upcase}_#{primary_key.underscore.upcase}];


	sqlite3_prepare_v2([database database], [removeSQL UTF8String], -1, &statement, NULL);
  #{bind_statement_for_remove}

	result = sqlite3_step(statement) == SQLITE_DONE;

  sqlite3_finalize(statement);

  return result;
}

- (BOOL)update:(#{objc_type})remotePrimaryKey itemToAdd:(#{model.name.camelize}*)item
{
  BOOL result = NO;

  if ( [self remove:remotePrimaryKey])
  {
    result = [self addItem:item itemVersion:[item version]];
  }

  return result;
}

- (NSString*)getMaxVersion {
  NSString* result = @"0";

  sqlite3_stmt *statement;

  NSString *querySQL = [NSString stringWithFormat: @"SELECT max(%@) FROM %@",
                                                    DATABASE_KEY_#{model.name.underscore.upcase}_VERSION,
                                                    DATABASE_TABLE_#{model.name.underscore.upcase}];

  const char *query_stmt = [querySQL UTF8String];

  if (sqlite3_prepare_v2([database database], query_stmt, -1, &statement, NULL) == SQLITE_OK)
  {
    if (sqlite3_step(statement) == SQLITE_ROW)
    {
      const char* col = (const char *)sqlite3_column_text(statement, 0);

      if (col) {
        result = [NSString stringWithUTF8String:(char *)sqlite3_column_text(statement, 0)];
      }
    } else {
      NSLog(@"Item not found");
    }

    sqlite3_finalize(statement);
  }

  return result;
}

@end
    eod
    File.open("#{output_dir}#{model.name.camelize}DataSource.m", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}#{model.name.camelize}DataSource.m"

  end

  def model_remote_h(model,output_dir)

    (out = '') << <<-eod
#import "#{model.name.camelize}DataSource.h"
#import <Restkit/RestKit.h>

@interface #{model.name.camelize}RemoteDataSource  : NSObject

+ (NSArray*)getAllWithCompletionBlock:(void(^)(NSArray*))completion;

@end
    eod

    File.open("#{output_dir}#{model.name.camelize}RemoteDataSource.h", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}#{model.name.camelize}RemoteDataSource.h"
  end

  def model_remote_m(model,output_dir)
    (out = '') << <<-eod
#import "#{model.name.camelize}RemoteDataSource.h"
#import "APIController.h"
#import "#{model.name.camelize}Wrapper.h"

@implementation #{model.name.camelize}RemoteDataSource

+ (NSArray*)getAllWithCompletionBlock:(void(^)(NSArray*))completion {
  NSArray __block *contents = [[NSArray alloc] init];
  #{model.name.camelize}DataSource *db = [[#{model.name.camelize}DataSource alloc] init];
  APIController *apiController = [[APIController alloc] init];
  if ([db open]) {
    contents = [db getAll];
  }
  [db close];

  // always try to fetch the new items from the server
  RKObjectRequestOperation *operation = [apiController get#{model.name.camelize}List:^{
    #{model.name.camelize}Wrapper *wrapper = [[apiController objects] objectAtIndex:0];

    //open the database and add all returned items
    #{model.name.camelize}DataSource *db = [[#{model.name.camelize}DataSource alloc] init];
    if ([db open]) {
      for (#{model.name.camelize}* c in [wrapper items]){
        [db addItem:c itemVersion:[wrapper version]];
      }
      //update the array backing the list with all the data stored in the db
      if ([wrapper items].count > 0) { // if needed
        contents = [db getAll];
      }
      [db close];
    }//end db operations

    //call the passed in function with the fetched data
    completion(contents);

  }];

  // start the fetch the from the server
  dispatch_sync([APIController sharedQueue], ^{
    [operation start];
  });

  return contents;

}
@end
    eod
    File.open("#{output_dir}#{model.name.camelize}RemoteDataSource.m", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}#{model.name.camelize}RemoteDataSource.m"
  end

  def model_restkit_wrapper_h(model, output_dir)
    (out = '') << <<-eod
#include <RestKit/RestKit.h>
#include "#{model.name.camelize}.h"

@interface #{model.name.camelize}Wrapper  : NSObject

@property (nonatomic, retain) NSNumber* count;
@property (nonatomic, retain) NSString* version;
@property (nonatomic, retain) NSArray* errors;
@property (nonatomic, retain) NSArray* items;

+(RKObjectMapping *)getObjectMapping;
@end

    eod

    File.open("#{output_dir}#{model.name.camelize}Wrapper.h", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}#{model.name.camelize}Wrapper.h"
  end

  def model_restkit_wrapper_m(model, output_dir)
    (out = '') << <<-eod

#include "#{model.name.camelize}Wrapper.h"

@implementation #{model.name.camelize}Wrapper

@synthesize count;
@synthesize version;
@synthesize errors;
@synthesize items;

+(RKObjectMapping *)getObjectMapping {
  RKObjectMapping* mapping = [RKObjectMapping mappingForClass:[#{model.name.camelize}Wrapper class]];
  [mapping addAttributeMappingsFromDictionary:@{
      @"count": @"count",
      @"errors": @"errors",
      @"version": @"version"
  }];

  [mapping addRelationshipMappingWithSourceKeyPath:@"items" mapping:[#{model.name.camelize} getObjectMapping]];

  return mapping;
}
@end
    eod
    File.open("#{output_dir}#{model.name.camelize}Wrapper.m", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}#{model.name.camelize}Wrapper.m"
  end

  def api_controller_h(output_dir,models)

    imports = []
    #generate the model imports
    imports << '//model imports'
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        imports << "#import \"#{model.name.camelize}.h\""
      end
    end

    # generate the method signatures
    methods = []
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        (item = '') << <<-eod
- (RKObjectRequestOperation *)get#{model.name.camelize}List:(void(^)(void))completion  error:(void(^)(void))onError;
        eod
        methods << item
      end # end if
    end # end loop

    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        (item = '') << <<-eod
- (RKObjectRequestOperation *)post#{model.name.camelize}:(#{model.name.camelize}*)item complete:(void(^)(void))completion  error:(void(^)(void))onError;
        eod
        methods << item
      end # end if
    end # end loop

    # generate the header body
    (out = '') << <<-eod
#import <Foundation/Foundation.h>
#import <RestKit/RestKit.h>
#{imports.join("\n")}
@interface APIController : NSObject

@property (strong, nonatomic) NSArray *objects;
@property (strong, nonatomic) AFHTTPClient *client;
@property (strong, nonatomic) RKObjectManager *objectManager;

+ (dispatch_queue_t)sharedQueue;

#{methods.join("\n")}

@end
    eod

    File.open("#{output_dir}APIController.h", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}APIController.h"
  end

  def api_controller_m(output_dir,models)

    # generate GET method bodies
    methods = []
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        (item = '') << <<-eod
- (RKObjectRequestOperation *)get#{model.name.camelize}List:(void(^)(void))completion error:(void(^)(void))onError {

    // get the max version of the items in the database
    #{model.name.camelize}DataSource *db = [[#{model.name.camelize}DataSource alloc] init];
    NSString* currentVersion = @"";
    if ([db open]) {
        currentVersion = [db getMaxVersion];
    }
    [db close];

    NSDictionary *dict = [[NSDictionary alloc] initWithObjects:@[currentVersion]
                                                       forKeys:@[@"version"]];
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:nil];

    RKObjectMapping* requestMapping = [RKObjectMapping requestMapping ];

    RKRequestDescriptor *requestDescriptor = [RKRequestDescriptor requestDescriptorWithMapping:requestMapping
                                                                                  objectClass:[NSDictionary class]
                                                                                  rootKeyPath:nil];
    [_objectManager addRequestDescriptor:requestDescriptor];

    RKResponseDescriptor *responseDescriptor = [RKResponseDescriptor responseDescriptorWithMapping:[#{model.name.camelize}Wrapper getObjectMapping]
                                                                                       pathPattern:@"/api/v1/#{model.name.underscore}"
                                                                                           keyPath:nil
                                                                                       statusCodes:[NSIndexSet indexSetWithIndex:200]];

    NSMutableURLRequest *mutRequest = [[RKObjectManager sharedManager] requestWithObject:nil
                                                                                  method:RKRequestMethodGET
                                                                                    path:[NSString stringWithFormat:@"/api/v1/#{model.name.underscore}"]
                                                                              parameters:dict];

    //[mutRequest setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    //[mutRequest setHTTPBody:jsonData];

    RKObjectRequestOperation *operation = [[RKObjectRequestOperation alloc] initWithRequest:mutRequest
                                                                        responseDescriptors:@[responseDescriptor]];

    [operation setCompletionBlockWithSuccess:^(RKObjectRequestOperation *operation, RKMappingResult *result){
      _objects = [result array];
      completion();
    } failure:^(RKObjectRequestOperation *operation, NSError *error){
      onError();
    }];
    return operation;
}

- (RKObjectRequestOperation *)post#{model.name.camelize}:(#{model.name.camelize}*)item complete:(void(^)(void))completion  error:(void(^)(void))onError{

  NSDictionary *dict = [[NSDictionary alloc] initWithObjects:@[[item toDictionary]]
                                                     forKeys:@[@"#{model.name.underscore}"]];
  NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:nil];

  RKResponseDescriptor *responseDescriptor = [RKResponseDescriptor responseDescriptorWithMapping:[GenericResponse getObjectMapping]
                                                                                     pathPattern:@"/api/v1/#{model.name.underscore}"
                                                                                         keyPath:nil
                                                                                     statusCodes:[NSIndexSet indexSetWithIndex:200]];



  NSMutableURLRequest *mutRequest = [[RKObjectManager sharedManager] requestWithObject:nil
                                                                                method:RKRequestMethodPOST
                                                                                  path:[NSString stringWithFormat:@"/api/v1/#{model.name.underscore}"]
                                                                            parameters:nil];

  [mutRequest setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
  [mutRequest setHTTPBody:jsonData];

  RKObjectRequestOperation *operation = [[RKObjectRequestOperation alloc] initWithRequest:mutRequest
  responseDescriptors:@[responseDescriptor]];


  [operation setCompletionBlockWithSuccess:^(RKObjectRequestOperation *operation, RKMappingResult *result){
    _objects = [result array];
    completion();
  } failure:^(RKObjectRequestOperation *operation, NSError *error){
    onError();
  }];

  return operation;
}
        eod
        methods << item
      end
    end

    imports = []


    #generate the wrapper imports
    imports << '//wrapper imports'
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        imports << "#import \"#{model.name.camelize}Wrapper.h\""
      end
    end
    imports << '#import "GenericResponse.h"'

    imports << "\n\n"

    #generate the data source imports
    imports << '//data source imports'
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        imports << "#import \"#{model.name.camelize}DataSource.h\""
      end
    end

    # the class definition
    (out = '') << <<-eod
#import "APIController.h"

#{imports.join("\n")}

#define API_URL @"YOUR.SERVER.COM"

@implementation APIController

@synthesize objects = _objects;
@synthesize client = _client;
@synthesize objectManager = _objectManager;

- (id)init {
    self = [super init];

    _client = [[AFHTTPClient alloc] initWithBaseURL:[NSURL URLWithString:API_URL]];
    _objectManager = [[RKObjectManager alloc] initWithHTTPClient:_client];

    return self;
}

+ (dispatch_queue_t)sharedQueue {
    static dispatch_once_t pred;
    static dispatch_queue_t sharedDispatchQueue;

    dispatch_once(&pred, ^{
        sharedDispatchQueue = dispatch_queue_create("Queue", NULL);
    });

    return sharedDispatchQueue;
}

#{methods.join("\n\n")}
@end
    eod
    File.open("#{output_dir}APIController.m", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}APIController.m"
  end

  def generic_response_class(output_dir)
    (header = '') << <<-eod
#include <RestKit/RestKit.h>
#import <Foundation/Foundation.h>

@interface GenericResponse : NSObject

@property (nonatomic, retain) NSSet* errors;
@property (nonatomic, retain) NSSet* messages;

+(RKObjectMapping *)getObjectMapping;

@end
eod

    (m = '') << <<-eod
#import "GenericResponse.h"

@implementation GenericResponse
@synthesize errors;
@synthesize messages;

+(RKObjectMapping *)getObjectMapping {
  RKObjectMapping* mapping = [RKObjectMapping mappingForClass:[GenericResponse class]];
  [mapping addAttributeMappingsFromDictionary:@{
   @"errors": @"errors",
   @"messages": @"messages"
   }];

  return mapping;
}
@end
    eod
    File.open("#{output_dir}GenericResponse.h", 'w') { |file| file.write header }
    puts 'Wrote: ' << "#{output_dir}GenericResponse.h"

    File.open("#{output_dir}GenericResponse.m", 'w') { |file| file.write m }
    puts 'Wrote: ' << "#{output_dir}GenericResponse.m"

  end

  def create_properties(column)

    return if column == nil

    sql_type = column.type.to_s
    objc_type = sql_type_to_objc(column)

    (c = '') << "// Generated from DB => attribute: #{column.name.camelize(:lower)}, data-type: #{sql_type}" << "\n"
    c << "@property #{ '(strong, nonatomic)' unless objc_type == 'BOOL'} #{objc_type} #{column.name.camelize(:lower)};"
  end

  def sql_type_to_objc(column)
    sql_type = column.type.to_s
    objc_type = 'NSObject* '

    if sql_type == 'integer'
      objc_type = 'NSNumber*'
    elsif sql_type == 'string' || sql_type == 'text'
      objc_type = 'NSString*'
    elsif sql_type == 'datetime'
      objc_type = 'NSDate*'
    elsif sql_type == 'boolean'
      objc_type = 'BOOL'
    elsif sql_type == 'float' or sql_type == 'double' or sql_type == 'decimal'
      objc_type = 'NSNumber* '
    end

    return objc_type
  end

  def get_column_by_name(model, column_name)
    col = nil
    model.columns.each do |c|
      if c.name.to_s == column_name.to_s
        col = c
        break
      end
    end

    return col
  end

  def get_type_from_column(model, column_name)
    type = nil

    model.columns.each do |c|
      if c.name.to_s == column_name.to_s
        type = c.type.to_s
        break
      end
    end

    return type
  end

  def get_attr(model)
    model.columns.collect {|a| a.name}.sort - %w(created_at updated_at)
  end
end