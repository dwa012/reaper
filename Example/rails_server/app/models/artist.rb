class Artist < ActiveRecord::Base
  set_table_name 'Artist'
  set_primary_key :ArtistId
  attr_accessible :ArtistId, :Name
end
