class Track < ActiveRecord::Base
  set_table_name 'Track'
  set_primary_key :TrackId

  has_and_belongs_to_many :playlist, :join_table => 'PlaylistTrack', :association_foreign_key => 'PlaylistId', :foreign_key => 'TrackId'

  attr_accessible :AlbumId, :Bytes, :Composer, :GenreId, :MediaTypeId, :Milliseconds, :Name, :TrackId, :UnitPrice
end
