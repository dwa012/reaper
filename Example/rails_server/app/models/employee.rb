class Employee < ActiveRecord::Base
  set_table_name 'Employee'
  set_primary_key :EmployeeId

  attr_accessible :Address, :BirthDate, :City, :Country, :Email, :EmployeeId, :Fax,
                  :FirstName, :HireDate, :LastName, :Phone, :PostalCode, :ReportsTo,
                  :State, :Title
end
