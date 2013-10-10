class Playlist < ActiveRecord::Base
  set_table_name 'Playlist'
  set_primary_key :PlaylistId

  has_and_belongs_to_many :tracks, :join_table => 'PlaylistTrack', :association_foreign_key => 'TrackId', :foreign_key => 'PlaylistId'

  attr_accessible :Name, :PlaylistId
end
