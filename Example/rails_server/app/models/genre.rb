class Genre < ActiveRecord::Base
  set_table_name 'Genre'
  set_primary_key :GenreId

  attr_accessible :GenreId, :Name
end
