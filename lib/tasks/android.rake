namespace :reaper do
  desc 'Create model Java classes for Android'
  task :android => :environment do
    models = ARGV[1].nil? ? [] : ARGV[1].split(',')
    task ARGV[1].to_sym do
    end unless models.empty?

    model_output_dir = "#{Rails.root}/generated_files/mobile_app/android/com/github/dwa012/reaper/model/"
    util_output_dir = "#{Rails.root}/generated_files/mobile_app/android/com/github/dwa012/reaper/util/"
    db_output_dir = "#{Rails.root}/generated_files/mobile_app/android/com/github/dwa012/reaper/database/"
    async_output_dir = "#{Rails.root}/generated_files/mobile_app/android/com/github/dwa012/reaper/async/"
    api_output_dir = "#{Rails.root}/generated_files/mobile_app/android/com/github/dwa012/reaper/api/"
    wrappers_output_dir = "#{Rails.root}/generated_files/mobile_app/android/com/github/dwa012/reaper/wrapper/"

    # make the directories for the generated files
    FileUtils.makedirs model_output_dir unless File.exists?(model_output_dir)
    FileUtils.makedirs util_output_dir unless File.exists?(util_output_dir)
    FileUtils.makedirs db_output_dir unless File.exists?(db_output_dir)
    FileUtils.makedirs async_output_dir unless File.exists?(async_output_dir)
    FileUtils.makedirs api_output_dir unless File.exists?(api_output_dir)
    FileUtils.makedirs wrappers_output_dir unless File.exists?(wrappers_output_dir)

    # force load of Models so we can iterate over them
    Rails.application.eager_load!

    gen_util_classes(util_output_dir)

    gen_generic_response(wrappers_output_dir)
    async_listener(async_output_dir)
    gen_api_class(models,api_output_dir)
    generate_db_helper(models,db_output_dir)
    #gen_date_deserializer(models,util_output_dir)

    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        generate_model(model, model_output_dir)
        generate_data_source(model,db_output_dir)
        generate_data_source_async(model,async_output_dir)
        generate_model_wrapper(model,wrappers_output_dir)
      end
    end
  end

  def generate_model(model,output_dir)

    primary_key = model.primary_key

    #generate the property statements
    properties = []
    get_attr(model).each do |attribute|
      properties << create_android_properties(get_column_by_name(model,attribute))
    end
    properties << ' private Date version;'

    # generate the methods
    methods = []
    get_attr(model).each do |attribute|
      type = sql_to_android_type(get_column_by_name(model,attribute))
      (gen = '') << <<-eod
  @JsonProperty("#{attribute}")
  public void set#{attribute.camelize}(#{type} #{attribute.camelize(:lower)}) {
    this.#{attribute.camelize(:lower)} = #{attribute.camelize(:lower)};
  }

  public #{type} get#{attribute.camelize}() {
    return this.#{attribute.camelize(:lower)};
  }
      eod
      methods << gen
    end

    (out = '') << <<-eod
package com.github.dwa012.reaper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class #{model.name.camelize} {

#{properties.join("\n")}

#{methods.join("\n")}

  public void setVersion(Date version) {
    this.version = version;
  }

  public Date getVersion() {
    return this.version;
  }

  public #{sql_to_android_type(get_column_by_name(model,primary_key))} getPrimaryKey(){
     return #{primary_key.camelize(:lower)};
  }
}
    eod

    File.open("#{output_dir}#{model.name.camelize}.java", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}#{model.name.camelize}.java"
  end

  def generate_db_helper(models,output_dir)

    # generate the table names for the database
    table_names = []
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        table_names << "  static final String TABLE_#{model.name.underscore.upcase} = \"#{model.name.downcase}\";"
      end
    end

    # generate the column names for the table
    table_columns = []
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        table_columns << "  // Columns for table #{model.name}"
        table_columns << "  static final String KEY_#{model.name.underscore.upcase}_DB_ID = \"_id\";"
        get_attr(model).each do |attribute|
          table_columns << "  static final String KEY_#{model.name.underscore.upcase}_#{attribute.underscore.upcase} = \"#{attribute}\";"
        end
        table_columns << "  static final String KEY_#{model.name.underscore.upcase}_VERSION = \"version\";"
        table_columns << ''
      end
    end

    # generate the create table statments
    create_table_statements = []
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        create_table_statements << "  // Create statement for table #{model.name}"
        create_table_statements << "  private String CREATE_#{model.name.underscore.upcase}_TABLE = \"CREATE TABLE \" + TABLE_#{model.name.underscore.upcase} + \"(\""
        create_table_statements << "      + KEY_#{model.name.underscore.upcase}_DB_ID + \" INTEGER PRIMARY KEY autoincrement,\""
        attrs = get_attr(model)
        attrs.each do |attribute|
          type = sql_to_sqlite_type(get_column_by_name(model,attribute))
          create_table_statements << "      + KEY_#{model.name.underscore.upcase}_#{attribute.underscore.upcase} + \" #{type} #{'UNIQUE' if attribute == model.primary_key },\""
        end
        create_table_statements << "      + KEY_#{model.name.underscore.upcase}_VERSION + \" TEXT\""
        create_table_statements << '      + ")";'
        create_table_statements << ''
      end
    end

    # generate the create exec statements
    create_exec = []
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        create_exec << "    db.execSQL(CREATE_#{model.name.underscore.upcase}_TABLE);"
      end
    end

    # generate the drop exec statements
    drop_statements = []
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        drop_statements << "    db.execSQL(\"DROP TABLE IF EXISTS \" + TABLE_#{model.name.underscore.upcase});"
      end
    end


    (out = '') << <<-eod
package com.github.dwa012.reaper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;

public class DatabaseHelper extends SQLiteOpenHelper {

  // Used to upgrade the BD as needed
  private static final int DATABASE_VERSION = 1;

  // Database Name
  private static final String DATABASE_NAME = "reaper.db";

  // TABLE NAMES
#{table_names.join("\n")}

#{table_columns.join("\n")}

#{create_table_statements.join("\n")}

  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  // Creating Tables
  @Override
  public void onCreate(SQLiteDatabase db) {
#{create_exec.join("\n")}
  }

  // Upgrading database
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
#{drop_statements.join("\n")}

    // Recreate tables again
    onCreate(db);
  }
}
eod

    File.open("#{output_dir}DatabaseHelper.java", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}DatabaseHelper.java"
  end

  def generate_data_source(model,output_dir)

    primary_key = model.primary_key
    primary_key_type = sql_to_android_type(get_column_by_name(model,primary_key))

    # generate the columns for the database
    columns = []
    columns << "   DatabaseHelper.KEY_#{model.name.underscore.upcase}_DB_ID"
    get_attr(model).each do |attribute|
      #type = sql_to_sqlite_type(get_column_by_name(model,attribute))
      columns << "    DatabaseHelper.KEY_#{model.name.underscore.upcase}_#{attribute.underscore.upcase}"
    end
    columns << "   DatabaseHelper.KEY_#{model.name.underscore.upcase}_VERSION"

    # generate the database input statements
    input_statements = []
    get_attr(model).each do |attribute|
      type = sql_to_android_type(get_column_by_name(model,attribute))
      if type == 'Date'
        input_statements << "    values.put(DatabaseHelper.KEY_#{model.name.underscore.upcase}_#{attribute.underscore.upcase}, Common.DATE_FORMATTER.format(item.get#{attribute.camelize}()));"
      elsif type == 'boolean'
        input_statements << "    values.put(DatabaseHelper.KEY_#{model.name.underscore.upcase}_#{attribute.underscore.upcase}, item.get#{attribute.camelize}() ? 1 : 0);"
      elsif type == 'float' or type == 'double' or type.start_with?('numeric') or type == 'decimal'
        input_statements << "    values.put(DatabaseHelper.KEY_#{model.name.underscore.upcase}_#{attribute.underscore.upcase}, String.valueOf(item.get#{attribute.camelize}()) );"
      else
        input_statements << "    values.put(DatabaseHelper.KEY_#{model.name.underscore.upcase}_#{attribute.underscore.upcase}, item.get#{attribute.camelize}());"
      end
    end

    # generate the database retrieval statements
    output_statements = []
    index = 1;
    get_attr(model).each do |attribute|
      type = sql_to_android_type(get_column_by_name(model,attribute)).downcase
      if type == 'Date'
        output_statements << <<-eod
    try {
        item.set#{attribute.camelize}(Common.DATE_FORMATTER.parse(cursor.getString(#{index + 1})));
    } catch (ParseException e) {
        e.printStackTrace();
    }
        eod
      elsif type == 'boolean'
        #output_statements << "    values.put(DatabaseHelper.KEY_#{model.name.underscore.upcase}_#{attribute.underscore.upcase}, item.get#{attribute.camelize}() ? 1 : 0);"
        output_statements << "    item.set#{attribute.camelize}(cursor.getInt(#{index + 1}) == 1);"
      elsif type == 'float' or type == 'double' or type == 'numeric'  or type == 'decimal'
        output_statements << "    item.set#{attribute.camelize}(Float.parseFloat(cursor.getString(#{index + 1})));"
      elsif type == 'int'
        output_statements << "    item.set#{attribute.camelize}(cursor.getInt(#{index + 1}));"
      else
        output_statements << "    item.set#{attribute.camelize}(cursor.getString(#{index + 1}));"
      end
      index = index + 1;
    end

    output_statements << <<-eod
    try {
        item.setVersion(Common.DATE_FORMATTER.parse(cursor.getString(#{index})));
    } catch (ParseException e) {
        e.printStackTrace();
    }
    eod

    (out = '') << <<-eod
package com.github.dwa012.reaper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.#{model.name.camelize};

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class #{model.name.camelize}DataSource {

	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	private String[] allColumns = {
#{columns.join(",\n")}
  };

	public #{model.name.camelize}DataSource(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		database.close();
		dbHelper.close();
	}

	// Adding new item
	public void addItem(#{model.name.camelize} item, String version) {

		ContentValues values = new ContentValues();
#{input_statements.join("\n")}
		values.put(DatabaseHelper.KEY_#{model.name.underscore.upcase}_VERSION, version);

		// Inserting Row
		database.insert(DatabaseHelper.TABLE_#{model.name.underscore.upcase}, null, values);
		//close(); // Closing database connection
	}

	// Getting single item
	public #{model.name.camelize} getItem(#{primary_key_type} primaryKey) {
		#{model.name.camelize} item = new #{model.name.camelize}();

		Cursor cursor = database.query(DatabaseHelper.TABLE_#{model.name.underscore.upcase},
				this.allColumns, DatabaseHelper.KEY_#{model.name.underscore.upcase}_#{model.primary_key.underscore.upcase} + "=?",
				new String[] { String.valueOf(primaryKey) }, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
            item = cursorToItem(cursor);
		}

		return item;
	}

	public String getVersion() {
		String result = "1000-01-01T18:00:00Z";

		String selectQuery = "SELECT max(" + DatabaseHelper.KEY_#{model.name.underscore.upcase}_VERSION + ") FROM "
                          + DatabaseHelper.TABLE_#{model.name.underscore.upcase};
		Cursor cursor = database.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			result = cursor.getString(0);
		}

		return result;
	}

	public List<#{model.name.camelize}> getAll() {
		List<#{model.name.camelize}> itemList = new ArrayList<#{model.name.camelize}>();

    Cursor cursor = database.query(DatabaseHelper.TABLE_#{model.name.underscore.upcase},
                this.allColumns,
                null,
                null,
                null, null, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				itemList.add(cursorToItem(cursor));
			} while (cursor.moveToNext());
		}

		return itemList;
	}

  private #{model.name.camelize} cursorToItem(Cursor cursor){
    #{model.name.camelize} item = new #{model.name.camelize}();
    // ignore cursor.getInt(0) since it is not relevant

#{output_statements.join("\n")}

    return item;
  }
}
    eod
    File.open("#{output_dir}#{model.name.camelize}DataSource.java", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}#{model.name.camelize}DataSource.java"
  end

  def generate_data_source_async(model, output_dir)

    (out = '') << <<-eod
package com.github.dwa012.reaper.async;

import android.content.Context;
import android.os.AsyncTask;
import com.github.dwa012.reaper.api.Api;
import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.#{model.name.camelize};
import com.github.dwa012.reaper.wrapper.#{model.name.camelize}Wrapper;
import com.github.dwa012.reaper.database.#{model.name.camelize}DataSource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class #{model.name.camelize}Async {

  private Context context;
  private AsyncListener<#{model.name.camelize}> listener;

  private #{model.name.camelize}Async() {
    // left blank
  }

  public #{model.name.camelize}Async(Context context) {
    this.context = context;
  }

  public List<#{model.name.camelize}> getItemsFromDB(){
    #{model.name.camelize}DataSource db = new #{model.name.camelize}DataSource(context);
    db.open();

    List<#{model.name.camelize}> result = db.getAll();

    db.close();

    return result;
  }

  // get the current items from the database
  public void fetchItems(AsyncListener<#{model.name.camelize}> listener){
    new FetchTask(context).execute();
  }

  private class FetchTask extends AsyncTask<Object,Object,List<#{model.name.camelize}>> {

    private Context context;

    public FetchTask(Context context) {
      this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected List<#{model.name.camelize}> doInBackground(Object... unused) {

      //write the captures to the database
      #{model.name.camelize}DataSource db = new #{model.name.camelize}DataSource(context);
      db.open();
      String version = db.getVersion();
      db.close();

      Map<String,String> params = new LinkedHashMap<String,String>();
      params.put("version",version);

      String json = Api.GET(Api.#{model.name.underscore.upcase}_URL,params);
      JsonParser parser = null;
      #{model.name.camelize}Wrapper wrapper = new #{model.name.camelize}Wrapper();

      try {
          parser = Common.getJsonFactory().createJsonParser(json);
          wrapper = Common.getObjectMapper().readValue(parser,#{model.name.camelize}Wrapper.class);
      } catch (JsonParseException e) {
          e.printStackTrace();
      } catch (MalformedURLException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      } finally {
          try {
              parser.close();
          } catch (IOException e) {
              e.printStackTrace();
          } catch (java.lang.NullPointerException e) {

          }
      }

      db = new #{model.name.camelize}DataSource(context);
      db.open();

      for ( #{model.name.camelize} item : wrapper.getResults() ) {
          db.addItem(item,wrapper.getVersion());
      }

      db.close();

      return wrapper.getResults();
    }

    @Override
    protected void onPostExecute(List<#{model.name.camelize}> result) {
      listener.retrievalFinished(result);
    }

  }
}

eod

    File.open("#{output_dir}#{model.name.camelize}Async.java", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}#{model.name.camelize}Async.java"
  end

  def generate_model_wrapper(model,output_dir)
    (out = '') << <<-eod
package com.github.dwa012.reaper.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dwa012.reaper.model.#{model.name.camelize};

@JsonIgnoreProperties(ignoreUnknown=true)
public class #{model.name.camelize}Wrapper {

	private java.util.ArrayList<String> errors;
	private int count;
	private String version;
	private java.util.ArrayList<#{model.name.camelize}> results;

	public #{model.name.camelize}Wrapper(){
		results = new java.util.ArrayList<#{model.name.camelize}>();
	}

	@JsonProperty("items")
	public java.util.ArrayList<#{model.name.camelize}> getResults() {
		return results;
	}

	@JsonProperty("items")
	public void setResults(java.util.ArrayList<#{model.name.camelize}> results) {
		this.results = results;
	}

	@JsonProperty("version")
	public String getVersion() {
		return version;
	}

	@JsonProperty("version")
	public void setVersion(String version) {
		this.version = version;
	}

	@JsonProperty("errors")
	public final java.util.ArrayList<String> getErrors() {
		return errors;
	}

	@JsonProperty("errors")
	public final void setErrors(java.util.ArrayList<String> errors) {
		this.errors = errors;
	}

	@JsonProperty("count")
	public final int getCount() {
		return count;
	}

	@JsonProperty("count")
	public final void setCount(int count) {
		this.count = count;
	}
}
eod

    File.open("#{output_dir}#{model.name.camelize}Wrapper.java", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}#{model.name.camelize}Wrapper.java"
  end

  def gen_generic_response(output_dir)
    (out = '') << <<-eod
package com.github.dwa012.reaper.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GenericResponse {

    private ArrayList<String> errros;
    private ArrayList<String> messages;

    public GenericResponse() {
        errros = new ArrayList<String>();
        messages = new ArrayList<String>();
    }

    @JsonProperty("errors")
    public ArrayList<String> getErrros() {
        return errros;
    }

    public void setErrros(ArrayList<String> errros) {
        this.errros = errros;
    }

    @JsonProperty("Messages")
    public ArrayList<String> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }
}
    eod

    File.open("#{output_dir}GenericResponse.java", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}GenericResponse.java"
  end

  def gen_date_deserializer(output_dir)
    (out = '') << <<-eod
package com.github.dwa012.reaper.util;

public class JsonDateSerializer extends JsonSerializer<Date> {

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  @Override
  public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
    String formattedDate = dateFormat.format(date);
    gen.writeString(formattedDate);
  }
}
eod

    File.open("#{output_dir}JsonDateSerializer.java", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}JsonDateSerializer.java"
  end

  def gen_api_class(models,output_dir)

    route_paths = []
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        route_paths << "  public static final String #{model.name.underscore.upcase}_URL = BASE_API_URL + \"#{model.name.underscore.downcase}\";"
      end
    end

      (out = '') << <<-eod
package com.github.dwa012.reaper.api;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Joiner;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.util.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class Api {
  public static final String BASE_API_URL = "YOUR_SERVER_API_ADDRESS";
#{route_paths.join("\n")}

  // if result == "{}", there was an error
  public static String GET(String url,Map<String, String> params) {
    String json = "{}";

    String paramString = Joiner.on("&").withKeyValueSeparator("=").useForNull("").join(params);

    HttpGet request = new HttpGet(url + "?" + paramString);
    HttpClient httpClient = new DefaultHttpClient();
    HttpResponse response;

    try {
      response = httpClient.execute(request);
      Response result = new Response(response.getStatusLine().getStatusCode(),
                                     response.getStatusLine().getReasonPhrase(),
                                     EntityUtils.toString(response.getEntity()));

      json = result.getData();

    } catch (ClientProtocolException e1) {
        e1.printStackTrace();
    } catch (IOException e1) {
        e1.printStackTrace();
    }

    return json;
  }

  public static String POST(String url, Map<String, Object> params) {
    String json = "{}";

    String jsonEncodedString = "";

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JsonFactory jsonFactory = Common.getJsonFactory();
    JsonGenerator jsonGenerator = null;
    try {
        jsonGenerator = jsonFactory.createJsonGenerator(outputStream, JsonEncoding.UTF8);
        jsonGenerator.writeStartObject();

        //add the params to the body
        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            jsonGenerator.writeObjectField(pairs.getKey().toString(), pairs.getValue());
        }
        //---------------------

        jsonGenerator.writeEndObject();
        jsonGenerator.close();

        jsonEncodedString = outputStream.toString();
    } catch (IOException e) {
        e.printStackTrace();
    }


    HttpPost request = new HttpPost(url);
    request.setEntity(new ByteArrayEntity(jsonEncodedString.getBytes()));
    request.addHeader("Content-Type", "application/json");

    HttpClient httpClient = new DefaultHttpClient();
    HttpResponse response;

    try {
        response = httpClient.execute(request);
        Response httpResult = new Response(response.getStatusLine().getStatusCode(),
                                           response.getStatusLine().getReasonPhrase(),
                                           EntityUtils.toString(response.getEntity()));

        json = httpResult.getData();
    } catch (ClientProtocolException e1) {
        e1.printStackTrace();
    } catch (IOException e1) {
        e1.printStackTrace();
    }

    return json;
  }

}
eod

    File.open("#{output_dir}Api.java", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}Api.java"
  end

  def async_listener(output_dir)
    (out = '') << <<-eod
package com.github.dwa012.reaper.async;

import java.util.List;

public interface AsyncListener<Element> {
  public void retrievalFinished(List<Element> items);
}
    eod

    File.open("#{output_dir}AsyncListener.java", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}AsyncListener.java"
  end

  def gen_util_classes(output_dir)
    (out = '') << <<-eod
package com.github.dwa012.reaper.util;

public class Response {
  private String data;
  private int statusCode;
  private String response;

  public Response(int statusCode, String response, String data){
      this.statusCode = statusCode;
      this.response = response;
      this.data = data;
  }

  public int getStatusCode(){
      return this.statusCode;
  }

  public String getResponse(){
      return this.response;
  }

  public String getData(){
      return this.data;
  }
}
    eod

    File.open("#{output_dir}Response.java", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}Response.java"

    (out = '') << <<-eod
package com.github.dwa012.reaper.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dwa012.reaper.wrapper.GenericResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class Common {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static JsonFactory jsonFactory = new MappingJsonFactory();

    // Database date format
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public static ObjectMapper getObjectMapper() {
        objectMapper.setDateFormat(Common.DATE_FORMATTER);
        objectMapper.getDeserializationConfig().with(Common.DATE_FORMATTER);
        return objectMapper;
    }

    public static JsonFactory getJsonFactory() {
        return jsonFactory;
    }

    public static GenericResponse parseJsonResponse(String json) {
        GenericResponse result = new GenericResponse();
        try {
            result =  Common.getObjectMapper().readValue(json,GenericResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
    eod

    File.open("#{output_dir}Common.java", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}Common.java"
  end

  ######################
  # HELPER METHODS
  ######################

  def create_android_properties(column)

    # the column was nil, cannot discover anything info about it
    if column == nil
      return
    end

    java_type = sql_to_android_type(column)

    c = "  private #{java_type} #{column.name.camelize(:lower)}; // Generated from DB => attribute: #{column.name.camelize(:lower)}, data-type: #{column.type.to_s}"

    return c

  end

  def sql_to_android_type(column)
    sql_type = column.type.to_s.downcase
    java_type = 'Object '

    if sql_type == 'integer'
      java_type = 'int'
    elsif sql_type == 'string' || sql_type == 'text'
      java_type = 'String'
    elsif sql_type == 'datetime' || sql_type == 'date'
      java_type = 'Date'
    elsif sql_type == 'boolean'
      java_type = 'boolean'
    elsif sql_type == 'float' or sql_type == 'double' or sql_type == 'decimal' or sql_type.start_with?('numeric')
      java_type = 'float '
    end

    return java_type
  end

  def sql_to_sqlite_type(column)
    sql_type = column.type.to_s.downcase
    lite_type = ''

    if sql_type == 'integer'
      lite_type = 'INTEGER'
    elsif sql_type == 'string' || sql_type == 'text'
      lite_type = 'TEXT'
    elsif sql_type == 'datetime' || sql_type == 'date'
      lite_type = 'TEXT'
    elsif sql_type == 'boolean'
      lite_type = 'INTEGER'
    elsif sql_type == 'float' or sql_type == 'double' or sql_type == 'decimal' or sql_type.start_with?('numeric')
      lite_type = 'TEXT'
    end

    return lite_type
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

  def get_attr(model)
    model.columns.collect {|a| a.name}.sort - %w(created_at updated_at)
  end

end