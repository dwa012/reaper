class MediaType < ActiveRecord::Base
  set_table_name 'MediaType'
  set_primary_key :MediaTypeId

  attr_accessible :MediaTypeId, :Name
end
