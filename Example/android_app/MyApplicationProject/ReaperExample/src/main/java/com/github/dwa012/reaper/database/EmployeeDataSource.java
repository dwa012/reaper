package com.github.dwa012.reaper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.Employee;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	private String[] allColumns = {
   DatabaseHelper.KEY_EMPLOYEE_DB_ID,
    DatabaseHelper.KEY_EMPLOYEE_ADDRESS,
    DatabaseHelper.KEY_EMPLOYEE_BIRTH_DATE,
    DatabaseHelper.KEY_EMPLOYEE_CITY,
    DatabaseHelper.KEY_EMPLOYEE_COUNTRY,
    DatabaseHelper.KEY_EMPLOYEE_EMAIL,
    DatabaseHelper.KEY_EMPLOYEE_EMPLOYEE_ID,
    DatabaseHelper.KEY_EMPLOYEE_FAX,
    DatabaseHelper.KEY_EMPLOYEE_FIRST_NAME,
    DatabaseHelper.KEY_EMPLOYEE_HIRE_DATE,
    DatabaseHelper.KEY_EMPLOYEE_LAST_NAME,
    DatabaseHelper.KEY_EMPLOYEE_PHONE,
    DatabaseHelper.KEY_EMPLOYEE_POSTAL_CODE,
    DatabaseHelper.KEY_EMPLOYEE_REPORTS_TO,
    DatabaseHelper.KEY_EMPLOYEE_STATE,
    DatabaseHelper.KEY_EMPLOYEE_TITLE,
   DatabaseHelper.KEY_EMPLOYEE_VERSION
  };

	public EmployeeDataSource(Context context) {
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
	public void addItem(Employee item, String version) {

		ContentValues values = new ContentValues();
    values.put(DatabaseHelper.KEY_EMPLOYEE_ADDRESS, item.getAddress());
    values.put(DatabaseHelper.KEY_EMPLOYEE_BIRTH_DATE, Common.DATE_FORMATTER.format(item.getBirthDate()));
    values.put(DatabaseHelper.KEY_EMPLOYEE_CITY, item.getCity());
    values.put(DatabaseHelper.KEY_EMPLOYEE_COUNTRY, item.getCountry());
    values.put(DatabaseHelper.KEY_EMPLOYEE_EMAIL, item.getEmail());
    values.put(DatabaseHelper.KEY_EMPLOYEE_EMPLOYEE_ID, item.getEmployeeId());
    values.put(DatabaseHelper.KEY_EMPLOYEE_FAX, item.getFax());
    values.put(DatabaseHelper.KEY_EMPLOYEE_FIRST_NAME, item.getFirstName());
    values.put(DatabaseHelper.KEY_EMPLOYEE_HIRE_DATE, Common.DATE_FORMATTER.format(item.getHireDate()));
    values.put(DatabaseHelper.KEY_EMPLOYEE_LAST_NAME, item.getLastName());
    values.put(DatabaseHelper.KEY_EMPLOYEE_PHONE, item.getPhone());
    values.put(DatabaseHelper.KEY_EMPLOYEE_POSTAL_CODE, item.getPostalCode());
    values.put(DatabaseHelper.KEY_EMPLOYEE_REPORTS_TO, item.getReportsTo());
    values.put(DatabaseHelper.KEY_EMPLOYEE_STATE, item.getState());
    values.put(DatabaseHelper.KEY_EMPLOYEE_TITLE, item.getTitle());
		values.put(DatabaseHelper.KEY_EMPLOYEE_VERSION, version);

		// Inserting Row
		database.insert(DatabaseHelper.TABLE_EMPLOYEE, null, values);
		//close(); // Closing database connection
	}

	// Getting single item
	public Employee getItem(int primaryKey) {
		Employee item = new Employee();

		Cursor cursor = database.query(DatabaseHelper.TABLE_EMPLOYEE,
				this.allColumns, DatabaseHelper.KEY_EMPLOYEE_EMPLOYEE_ID + "=?",
				new String[] { String.valueOf(primaryKey) }, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
            item = cursorToItem(cursor);
		}

		return item;
	}

	public String getVersion() {
		String result = "1000-01-01T18:00:00Z";

		String selectQuery = "SELECT max(" + DatabaseHelper.KEY_EMPLOYEE_VERSION + ") FROM "
                          + DatabaseHelper.TABLE_EMPLOYEE;
		Cursor cursor = database.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			result = cursor.getString(0);
		}

		return result;
	}

	public List<Employee> getAll() {
		List<Employee> itemList = new ArrayList<Employee>();

    Cursor cursor = database.query(DatabaseHelper.TABLE_EMPLOYEE,
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

  private Employee cursorToItem(Cursor cursor){
    Employee item = new Employee();
    // ignore cursor.getInt(0) since it is not relevant

    item.setAddress(cursor.getString(2));
    try {
        item.setBirthDate(Common.DATE_FORMATTER.parse(cursor.getString(3)));
    } catch (ParseException e) {
        e.printStackTrace();
    }

    item.setCity(cursor.getString(4));
    item.setCountry(cursor.getString(5));
    item.setEmail(cursor.getString(6));
    item.setEmployeeId(cursor.getInt(7));
    item.setFax(cursor.getString(8));
    item.setFirstName(cursor.getString(9));
    try {
        item.setHireDate(Common.DATE_FORMATTER.parse(cursor.getString(10)));
    } catch (ParseException e) {
        e.printStackTrace();
    }

    item.setLastName(cursor.getString(11));
    item.setPhone(cursor.getString(12));
    item.setPostalCode(cursor.getString(13));
    item.setReportsTo(cursor.getInt(14));
    item.setState(cursor.getString(15));
    item.setTitle(cursor.getString(16));
    try {
        item.setVersion(Common.DATE_FORMATTER.parse(cursor.getString(16)));
    } catch (ParseException e) {
        e.printStackTrace();
    }


    return item;
  }
}
