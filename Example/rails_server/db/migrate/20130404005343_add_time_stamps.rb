class AddTimeStamps < ActiveRecord::Migration
  def self.up # Or `def up` in 3.1
    change_table :album do |t|
      t.timestamps
    end

    change_table :artist do |t|
      t.timestamps
    end

    change_table :customer do |t|
      t.timestamps
    end

    change_table :employee do |t|
      t.timestamps
    end

    change_table :genre do |t|
      t.timestamps
    end

    change_table :invoice do |t|
      t.timestamps
    end

    change_table :InvoiceLine do |t|
      t.timestamps
    end

    change_table :MediaType do |t|
      t.timestamps
    end

    change_table :playlist do |t|
      t.timestamps
    end

    change_table :PlaylistTrack do |t|
      t.timestamps
    end

    change_table :track do |t|
      t.timestamps
    end

  end
  def self.down # Or `def down` in 3.1

  end
end
