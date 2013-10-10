namespace :reaper do
  desc 'Create model Objective C classes for iOS'

  task :go => :environment do
    models = ARGV[1].nil? ? [] : ARGV[1].split(',')
    task ARGV[1].to_sym do
    end unless models.empty?


    # set what the output directories are
    server_pkg_output_dir = "#{Rails.root}/generated_files/golang/src/reaper/server/"
    controllers_pkg_output_dir = "#{Rails.root}/generated_files/golang/src/reaper/controllers/"
    db_pkg_output_dir = "#{Rails.root}/generated_files/golang/src/reaper/db/"
    src_dir = "#{Rails.root}/generated_files/golang/src/"
    pkg_dir = "#{Rails.root}/generated_files/golang/pkg/"
    bin_dir = "#{Rails.root}/generated_files/golang/bin/"

    # make the directories for the generated files
    FileUtils.makedirs server_pkg_output_dir unless File.exists?(server_pkg_output_dir)
    FileUtils.makedirs controllers_pkg_output_dir unless File.exists?(controllers_pkg_output_dir)
    FileUtils.makedirs db_pkg_output_dir unless File.exists?(db_pkg_output_dir)
    FileUtils.makedirs src_dir unless File.exists?(src_dir)
    FileUtils.makedirs pkg_dir unless File.exists?(pkg_dir)
    FileUtils.makedirs bin_dir unless File.exists?(bin_dir)

    # force load of Models so we can iterate over them
    Rails.application.eager_load!

    # create the database helper files
    go_server(server_pkg_output_dir, models)
    go_db(db_pkg_output_dir)
    go_controller_common(controllers_pkg_output_dir)

    # create the .h and .m files for the models
    ActiveRecord::Base.descendants.each do |m|
      if models.empty? or models.include?(m.name)
        go_model_controller(m, controllers_pkg_output_dir)
      end
    end # end of Model iteration
  end

  def go_server(output_dir,models)
    methods = []

    # create the names of the tables
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        methods << "\tgoweb.MapRest(\"/api/#{model.name.underscore.downcase}\", controllers.New#{model.name.camelize}Controller())"
      end # end if
    end # end models loop

    (out = '') << <<-eod
package main

import (
  "reaper/controllers"
  "github.com/dwa012/goweb/goweb"
)


func main() {
#{methods.join("\n")}
	goweb.ListenAndServe(":8080")
}
    eod

    File.open("#{output_dir}server.go", 'w') { |file| file.write out }
    puts "Wrote: " << "#{output_dir}server.go"
  end

  def go_model_controller(model,output_dir)
    import = ''
    items = []

    # check for time item
    model.accessible_attributes.sort[1..-1].each do |column|
      sql_type = get_type_from_column(model, column)
      if sql_type == 'datetime'
        import =  'import  "time"'
      end
    end

    model.accessible_attributes.sort[1..-1].each do |column|
      sql_type = get_type_from_column(model, column)

      if sql_type == 'integer'
        items << "\t#{column.camelize}\tint\t`sql:\"#{column}\" json:\"#{column}\"`"
      elsif sql_type == 'string' || sql_type == 'text'
        items << "\t#{column.camelize}\tstring\t`sql:\"#{column}\" json:\"#{column}\"`"
      elsif sql_type == 'datetime'
        items << "\t#{column.camelize}\ttime.Time\t`sql:\"#{column}\" json:\"#{column}\"`"
      elsif sql_type == 'boolean'
        items << "\t#{column.camelize}\tbool\t`sql:\"#{column}\" json:\"#{column}\"`"
      elsif sql_type == 'float' or sql_type == 'double'
        items << "\t#{column.camelize}\tfloat32\t`sql:\"#{column}\" json:\"#{column}\"`"
      end
    end

    (out = '') << <<-eod
package controllers

import (
  "fmt"
  "reaper/db"
  "encoding/json"
  "github.com/kisielk/sqlstruct"
	"github.com/dwa012/goweb/goweb"
)
#{import}


type #{model.name.camelize} struct {
#{items.join("\n")}
}

/*
   RESTful API Controller type
*/
type #{model.name.camelize}Controller struct {

}

func New#{model.name.camelize}Controller() *#{model.name.camelize}Controller {
	temp := new(#{model.name.camelize}Controller)
	return temp
}

func (cr *#{model.name.camelize}Controller) Create(cx *goweb.Context) {
  Unsupported(cx)
}

func (cr *#{model.name.camelize}Controller) Delete(id string, cx *goweb.Context) {
  Unsupported(cx)
}

func (cr *#{model.name.camelize}Controller) DeleteMany(cx *goweb.Context) {
  Unsupported(cx)
}

func (cr *#{model.name.camelize}Controller) Read(id string, cx *goweb.Context) {
  db, err := db.Open()
  defer db.Close()

  if err != nil {
    GeneralError(cx,err)
    return
  }

  items := make([]#{model.name.camelize},0)
  errors := make([]error,0)

  var temp #{model.name.camelize}
  rows, err := db.Query(fmt.Sprintf("SELECT %s FROM #{model.table_name} where #{model.primary_key} = %s ", sqlstruct.Columns(temp),id))

  if err != nil {
    GeneralError(cx,err)
  }

	for rows.Next() {
		var temp #{model.name.camelize}
		err = sqlstruct.Scan(&temp, rows)
	  items = append(items,temp)
	}

  var result = map[string] interface{} {
    "items" : items,
    "count" : len(items),
    "errors" : errors,
  }

  b, err := json.Marshal(result)

  fmt.Fprintf(cx.ResponseWriter, string(b))
}

func (cr *#{model.name.camelize}Controller) ReadMany(cx *goweb.Context) {
	db, err := db.Open()
  defer db.Close()

  if err != nil {
    GeneralError(cx,err)
    return
  }

  items := make([]#{model.name.camelize},0)
  errors := make([]error,0)

  var temp #{model.name.camelize}
  rows, err := db.Query(fmt.Sprintf("SELECT %s FROM #{model.table_name}", sqlstruct.Columns(temp)))

  if err != nil {
    GeneralError(cx,err)
    return
  }

	for rows.Next() {
		var temp #{model.name.camelize}
		err = sqlstruct.Scan(&temp, rows)
	  items = append(items,temp)
	}

  var result = map[string] interface{} {
    "items" : items,
    "count" : len(items),
    "errors" : errors,
  }

  b, err := json.Marshal(result)

  fmt.Fprintf(cx.ResponseWriter, string(b))
}

func (cr *#{model.name.camelize}Controller) Update(id string, cx *goweb.Context) {
  Unsupported(cx)
}

func (cr *#{model.name.camelize}Controller) UpdateMany(cx *goweb.Context) {
	Unsupported(cx)
}
    eod

    File.open("#{output_dir}#{model.name.camelize(:lower)}Controller.go", 'w') { |file| file.write out }
    puts "Wrote: " << "#{output_dir}#{model.name.camelize(:lower)}Controller.go"
  end

  def go_controller_common (output_dir)
    (out = '') << <<-eod
package controllers

import (
	"fmt"
  "encoding/json"
	"github.com/dwa012/goweb/goweb"
)

func Unsupported(cx *goweb.Context) {
	var result = map[string]interface{}{
		"message": "Unsupported",
	}

	b, _ := json.Marshal(result)
	fmt.Fprintf(cx.ResponseWriter, string(b))
}

func GeneralError(cx *goweb.Context, err error) {
	var result = map[string]interface{}{
		"error": fmt.Sprintf("%s",err),
	}

	b, _ := json.Marshal(result)
	fmt.Fprintf(cx.ResponseWriter, string(b))
}
    eod

    File.open("#{output_dir}common.go", 'w') { |file| file.write out }
    puts "Wrote: " << "#{output_dir}common.go"
  end

  def go_model(model,output_dir)
    import = ''
    items = []

    # check for time item
    model.accessible_attributes.sort[1..-1].each do |column|
      sql_type = get_type_from_column(model, column)
      if sql_type == 'datetime'
         import =  'import  "time"'
      end
    end

    model.accessible_attributes.sort[1..-1].each do |column|
        sql_type = get_type_from_column(model, column)

        if sql_type == 'integer'
          items << "  #{column.camelize} int `sql:\"#{column}\" json:\"#{column}\"`"
        elsif sql_type == 'string' || sql_type == 'text'
          items << "  #{column.camelize} string `sql:\"#{column}\" json:\"#{column}\"`"
        elsif sql_type == 'datetime'
          items << "  #{column.camelize} time.Time `sql:\"#{column}\" json:\"#{column}\"`"
        elsif sql_type == 'boolean'
          items << "  #{column.camelize} bool `sql:\"#{column}\" json:\"#{column}\"`"
        elsif sql_type == 'float' or sql_type == 'double'
          items << "  #{column.camelize} float `sql:\"#{column}\" json:\"#{column}\"`"
        end
    end

    (out = '') << <<-eod
package models

#{import}

type #{model.name.camelize} struct {
#{items.join("\n")}
}
    eod

    File.open("#{output_dir}#{model.name.camelize(:lower)}.go", 'w') { |file| file.write out }
    puts "Wrote: " << "#{output_dir}#{model.name.camelize(:lower)}.go"
  end


def go_db(output_dir)
    (out = '') << <<-eod
package db

import (
	"database/sql"
	_ "YOUR_DB_DRIVER_PACKAGE"
)

func Open() (*sql.DB, error) {
	db, err := sql.Open("YOUR_DB_DRIVER", "YOUR_DB_CONNECTION_INFORMATION")
	return db,err
}
    eod

    File.open("#{output_dir}db.go", 'w') { |file| file.write out }
    puts "Wrote: " << "#{output_dir}db.go"
end

end