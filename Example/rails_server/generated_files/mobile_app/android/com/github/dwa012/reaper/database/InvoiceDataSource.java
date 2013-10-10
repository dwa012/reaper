package com.github.dwa012.reaper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.Invoice;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	private String[] allColumns = {
   DatabaseHelper.KEY_INVOICE_DB_ID,
    DatabaseHelper.KEY_INVOICE_BILLING_ADDRESS,
    DatabaseHelper.KEY_INVOICE_BILLING_CITY,
    DatabaseHelper.KEY_INVOICE_BILLING_COUNTRY,
    DatabaseHelper.KEY_INVOICE_BILLING_POSTAL_CODE,
    DatabaseHelper.KEY_INVOICE_BILLING_STATE,
    DatabaseHelper.KEY_INVOICE_CUSTOMER_ID,
    DatabaseHelper.KEY_INVOICE_INVOICE_DATE,
    DatabaseHelper.KEY_INVOICE_INVOICE_ID,
    DatabaseHelper.KEY_INVOICE_TOTAL,
   DatabaseHelper.KEY_INVOICE_VERSION
  };

	public InvoiceDataSource(Context context) {
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
	public void addItem(Invoice item, String version) {

		ContentValues values = new ContentValues();
    values.put(DatabaseHelper.KEY_INVOICE_BILLING_ADDRESS, item.getBillingAddress());
    values.put(DatabaseHelper.KEY_INVOICE_BILLING_CITY, item.getBillingCity());
    values.put(DatabaseHelper.KEY_INVOICE_BILLING_COUNTRY, item.getBillingCountry());
    values.put(DatabaseHelper.KEY_INVOICE_BILLING_POSTAL_CODE, item.getBillingPostalCode());
    values.put(DatabaseHelper.KEY_INVOICE_BILLING_STATE, item.getBillingState());
    values.put(DatabaseHelper.KEY_INVOICE_CUSTOMER_ID, item.getCustomerId());
    values.put(DatabaseHelper.KEY_INVOICE_INVOICE_DATE, Common.DATE_FORMATTER.format(item.getInvoiceDate()));
    values.put(DatabaseHelper.KEY_INVOICE_INVOICE_ID, item.getInvoiceId());
    values.put(DatabaseHelper.KEY_INVOICE_TOTAL, item.getTotal());
		values.put(DatabaseHelper.KEY_INVOICE_VERSION, version);

		// Inserting Row
		database.insert(DatabaseHelper.TABLE_INVOICE, null, values);
		//close(); // Closing database connection
	}

	// Getting single item
	public Invoice getItem(int primaryKey) {
		Invoice item = new Invoice();

		Cursor cursor = database.query(DatabaseHelper.TABLE_INVOICE,
				this.allColumns, DatabaseHelper.KEY_INVOICE_INVOICE_ID + "=?",
				new String[] { String.valueOf(primaryKey) }, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
            item = cursorToItem(cursor);
		}

		return item;
	}

	public String getVersion() {
		String result = "1000-01-01T18:00:00Z";

		String selectQuery = "SELECT max(" + DatabaseHelper.KEY_INVOICE_VERSION + ") FROM "
                          + DatabaseHelper.TABLE_INVOICE;
		Cursor cursor = database.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			result = cursor.getString(0);
		}

		return result;
	}

	public List<Invoice> getAll() {
		List<Invoice> itemList = new ArrayList<Invoice>();

    Cursor cursor = database.query(DatabaseHelper.TABLE_INVOICE,
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

  private Invoice cursorToItem(Cursor cursor){
    Invoice item = new Invoice();
    // ignore cursor.getInt(0) since it is not relevant

    item.setBillingAddress(cursor.getString(2));
    item.setBillingCity(cursor.getString(3));
    item.setBillingCountry(cursor.getString(4));
    item.setBillingPostalCode(cursor.getString(5));
    item.setBillingState(cursor.getString(6));
    item.setCustomerId(cursor.getInt(7));
    item.setInvoiceDate(cursor.getString(8));
    item.setInvoiceId(cursor.getInt(9));
    item.setTotal(cursor.getString(10));
    try {
        item.setVersion(Common.DATE_FORMATTER.parse(cursor.getString(10)));
    } catch (ParseException e) {
        e.printStackTrace();
    }


    return item;
  }
}
