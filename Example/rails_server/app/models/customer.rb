class Customer < ActiveRecord::Base
  set_table_name 'Customer'
  set_primary_key :CustomerId

  attr_accessible :Address, :City, :Company, :Country, :CustomerId,
                  :Email, :Fax, :FirstName, :LastName, :Phone,
                  :PostalCode, :State, :SupportRepId
end
