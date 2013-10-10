package com.github.dwa012.reaper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.Customer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	private String[] allColumns = {
   DatabaseHelper.KEY_CUSTOMER_DB_ID,
    DatabaseHelper.KEY_CUSTOMER_ADDRESS,
    DatabaseHelper.KEY_CUSTOMER_CITY,
    DatabaseHelper.KEY_CUSTOMER_COMPANY,
    DatabaseHelper.KEY_CUSTOMER_COUNTRY,
    DatabaseHelper.KEY_CUSTOMER_CUSTOMER_ID,
    DatabaseHelper.KEY_CUSTOMER_EMAIL,
    DatabaseHelper.KEY_CUSTOMER_FAX,
    DatabaseHelper.KEY_CUSTOMER_FIRST_NAME,
    DatabaseHelper.KEY_CUSTOMER_LAST_NAME,
    DatabaseHelper.KEY_CUSTOMER_PHONE,
    DatabaseHelper.KEY_CUSTOMER_POSTAL_CODE,
    DatabaseHelper.KEY_CUSTOMER_STATE,
    DatabaseHelper.KEY_CUSTOMER_SUPPORT_REP_ID,
   DatabaseHelper.KEY_CUSTOMER_VERSION
  };

	public CustomerDataSource(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		database.close();
		dbHelper.close();
	}

	// Adding new item
	public void addItem(Customer item, String version) {

		ContentValues values = new ContentValues();
    values.put(DatabaseHelper.KEY_CUSTOMER_ADDRESS, item.getAddress());
    values.put(DatabaseHelper.KEY_CUSTOMER_CITY, item.getCity());
    values.put(DatabaseHelper.KEY_CUSTOMER_COMPANY, item.getCompany());
    values.put(DatabaseHelper.KEY_CUSTOMER_COUNTRY, item.getCountry());
    values.put(DatabaseHelper.KEY_CUSTOMER_CUSTOMER_ID, item.getCustomerId());
    values.put(DatabaseHelper.KEY_CUSTOMER_EMAIL, item.getEmail());
    values.put(DatabaseHelper.KEY_CUSTOMER_FAX, item.getFax());
    values.put(DatabaseHelper.KEY_CUSTOMER_FIRST_NAME, item.getFirstName());
    values.put(DatabaseHelper.KEY_CUSTOMER_LAST_NAME, item.getLastName());
    values.put(DatabaseHelper.KEY_CUSTOMER_PHONE, item.getPhone());
    values.put(DatabaseHelper.KEY_CUSTOMER_POSTAL_CODE, item.getPostalCode());
    values.put(DatabaseHelper.KEY_CUSTOMER_STATE, item.getState());
    values.put(DatabaseHelper.KEY_CUSTOMER_SUPPORT_REP_ID, item.getSupportRepId());
		values.put(DatabaseHelper.KEY_CUSTOMER_VERSION, version);

		// Inserting Row
		database.insert(DatabaseHelper.TABLE_CUSTOMER, null, values);
		//close(); // Closing database connection
	}

	// Getting single item
	public Customer getItem(int primaryKey) {
		Customer item = new Customer();

		Cursor cursor = database.query(DatabaseHelper.TABLE_CUSTOMER,
				this.allColumns, DatabaseHelper.KEY_CUSTOMER_CUSTOMER_ID + "=?",
				new String[] { String.valueOf(primaryKey) }, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
            item = cursorToItem(cursor);
		}

		return item;
	}

	public String getVersion() {
		String result = "1000-01-01T18:00:00Z";

		String selectQuery = "SELECT max(" + DatabaseHelper.KEY_CUSTOMER_VERSION + ") FROM "
                          + DatabaseHelper.TABLE_CUSTOMER;
		Cursor cursor = database.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			result = cursor.getString(0);
		}

		return result;
	}

	public List<Customer> getAll() {
		List<Customer> itemList = new ArrayList<Customer>();

    Cursor cursor = database.query(DatabaseHelper.TABLE_CUSTOMER,
                this.allColumns,
                null,
                null,
                null, null, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				itemList.add(cursorToItem(cursor));
			} while (cursor.moveToNext());
		}

		return itemList;
	}

  private Customer cursorToItem(Cursor cursor){
    Customer item = new Customer();
    // ignore cursor.getInt(0) since it is not relevant

    item.setAddress(cursor.getString(2));
    item.setCity(cursor.getString(3));
    item.setCompany(cursor.getString(4));
    item.setCountry(cursor.getString(5));
    item.setCustomerId(cursor.getInt(6));
    item.setEmail(cursor.getString(7));
    item.setFax(cursor.getString(8));
    item.setFirstName(cursor.getString(9));
    item.setLastName(cursor.getString(10));
    item.setPhone(cursor.getString(11));
    item.setPostalCode(cursor.getString(12));
    item.setState(cursor.getString(13));
    item.setSupportRepId(cursor.getInt(14));
    try {
        item.setVersion(Common.DATE_FORMATTER.parse(cursor.getString(14)));
    } catch (ParseException e) {
        e.printStackTrace();
    }


    return item;
  }
}
