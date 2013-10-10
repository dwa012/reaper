class Album < ActiveRecord::Base
  set_table_name 'Album'
  belongs_to :artist
  set_primary_key :AlbumId

  attr_accessible :AlbumId, :ArtistId, :Title
end
