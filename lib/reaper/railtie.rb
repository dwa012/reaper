require 'reaper'
require 'rails'

module Reaper
  class Railtie < Rails::Railtie
    railtie_name :reaper

    rake_tasks do
      load 'tasks/ios.rake'
      load 'tasks/android.rake'
      load 'tasks/api.rake'
      load 'tasks/go.rake'
    end
  end
end