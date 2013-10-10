class UpdateTimeStamps < ActiveRecord::Migration
  def up
   Album.all.each do |a|
     a.updated_at = Time.now
     a.created_at = Time.now
     a.save
   end

   Artist.all.each do |a|
     a.updated_at = Time.now
     a.created_at = Time.now
     a.save
   end

   Customer.all.each do |a|
     a.updated_at = Time.now
     a.created_at = Time.now
     a.save
   end

   Employee.all.each do |a|
     a.updated_at = Time.now
     a.created_at = Time.now
     a.save
   end

   Genre.all.each do |a|
     a.updated_at = Time.now
     a.created_at = Time.now
     a.save
   end

   Invoice.all.each do |a|
     a.updated_at = Time.now
     a.created_at = Time.now
     a.save
   end

   MediaType.all.each do |a|
     a.updated_at = Time.now
     a.created_at = Time.now
     a.save
     end

   Playlist.all.each do |a|
     a.updated_at = Time.now
     a.created_at = Time.now
     a.save
     end

   Track.all.each do |a|
     a.updated_at = Time.now
     a.created_at = Time.now
     a.save
   end

   end

   def down
  end
end
