class Invoice < ActiveRecord::Base
  set_table_name 'Invoice'
  belongs_to :customer
  set_primary_key :InvoiceId

  attr_accessible :BillingAddress, :BillingCity, :BillingCountry, :BillingPostalCode, :BillingState,
                  :CustomerId, :InvoiceDate, :InvoiceId, :Total
end
