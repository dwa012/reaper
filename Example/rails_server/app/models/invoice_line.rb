class InvoiceLine < ActiveRecord::Base
  set_table_name 'InvoiceLine'
  set_primary_key :InvoiceLineId

  attr_accessible :InvoiceLineId, :Quantity, :TrackId, :UnitPrice
end
