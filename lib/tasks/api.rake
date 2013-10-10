namespace :reaper do
  desc 'Create model Objective C classes for iOS'

  task :api => :environment do
    models = ARGV[1].nil? ? [] : ARGV[1].split(',')
    task ARGV[1].to_sym do
    end unless models.empty?


    # set what the output directories are
    api_output_dir = "#{Rails.root}/app/controllers/api/v1/"
    routes_output_dir = "#{Rails.root}/config/routes/"

    # make the directories for the generated files
    FileUtils.makedirs api_output_dir unless File.exists?(api_output_dir)
    FileUtils.makedirs routes_output_dir unless File.exists?(routes_output_dir)

    # force load of Models so we can iterate over them
    Rails.application.eager_load!

    # create the database helper files
    api_controller(api_output_dir, models)
    create_routes(routes_output_dir, models)
  end


  def api_controller(output_dir, models)

    methods = []

    # create the names of the tables
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        (item = '') << <<-eod
  def get_#{model.name.underscore.downcase}
    default_includes = %w(#{get_attr(model).join(' ')})
    base_time = Time.parse('1000-01-01').utc.iso8601

    limit = params[:limit] ? params[:limit] : nil
    offset = params[:offset] ? params[:offset] : 0
    version = (params[:version] and params[:version] != "") ? Time.parse(params[:version]).utc.iso8601 : base_time
    includes = params[:includes] ? params[:includes].split(/,/) & default_includes : default_includes

    current_version = #{model.name}.order('updated_at desc').first.updated_at.utc.iso8601

    where_statement = version != base_time ? "updated_at > '\#{version}'" : nil
    @items = current_version != version ? #{model.name}.where(where_statement).select(includes).offset(offset).limit(limit) : {}

    data = Hash.new
    data['errors'] = []
    data['count'] = @items.count
    data['version'] = current_version
    data['items'] = @items

    render :json => data
  end
eod
        methods << item
        (item = '') << <<-eod
  def post_#{model.name.underscore.downcase}

    @errors = []
    @messages = []

    @#{model.name.underscore.downcase} = #{model.name}.new(params[:#{model.name.underscore.downcase}])

    if @#{model.name.underscore.downcase}.save
      @messages << 'Item added'
    else
      @errors = @#{model.name.underscore.downcase}.errors.full_messages
    end

    data = Hash.new
    data[:errors]   = @errors
    data[:messages] = @messages

    render :json => data
  end
        eod

        methods << item
        #model.accessible_attributes.sort[1..-1].each do |a|
        #  unless a == 'id' or a.empty?
        #
        #  end
        #end # end attribute loop
      end # end if
    end # end models loop

    (out = '') << <<-eod
class Api::V1::ApiController < ApplicationController

  skip_before_filter :verify_authenticity_token

   #{methods.join("\n")}
end
eod



    File.open("#{output_dir}api_controller.rb", 'w') { |file| file.write out }
    puts 'Wrote: ' << "#{output_dir}api_controller.rb"
  end

  def create_routes(output_dir, models)

    methods = []
    ActiveRecord::Base.descendants.each do |model|
      if models.empty? or models.include?(model.name)
        (item = '') << <<-eod
      get '/#{model.name.underscore.downcase}' => 'api#get_#{model.name.underscore.downcase}'
      post '/#{model.name.underscore.downcase}' => 'api#post_#{model.name.underscore.downcase}'
  eod
        methods << item
        #end # end attribute loop
      end # end if
    end # end models loop

    (routes = '') << <<-eod
Reaper::Application.routes.draw do
 namespace :api do
    namespace :v1 do
      #{methods.join("\n")}
    end
  end
end
    eod

    File.open("#{output_dir}api.rb", 'w') { |file| file.write routes }
    puts 'Wrote: ' << "#{output_dir}api.rb"
  end

  private

  def get_attr(model)
    model.columns.collect {|a| a.name}.sort - %w(created_at updated_at)
  end
end