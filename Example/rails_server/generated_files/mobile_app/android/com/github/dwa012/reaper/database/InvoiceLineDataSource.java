package com.github.dwa012.reaper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.InvoiceLine;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceLineDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	private String[] allColumns = {
   DatabaseHelper.KEY_INVOICE_LINE_DB_ID,
    DatabaseHelper.KEY_INVOICE_LINE_INVOICE_ID,
    DatabaseHelper.KEY_INVOICE_LINE_INVOICE_LINE_ID,
    DatabaseHelper.KEY_INVOICE_LINE_QUANTITY,
    DatabaseHelper.KEY_INVOICE_LINE_TRACK_ID,
    DatabaseHelper.KEY_INVOICE_LINE_UNIT_PRICE,
   DatabaseHelper.KEY_INVOICE_LINE_VERSION
  };

	public InvoiceLineDataSource(Context context) {
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
	public void addItem(InvoiceLine item, String version) {

		ContentValues values = new ContentValues();
    values.put(DatabaseHelper.KEY_INVOICE_LINE_INVOICE_ID, item.getInvoiceId());
    values.put(DatabaseHelper.KEY_INVOICE_LINE_INVOICE_LINE_ID, item.getInvoiceLineId());
    values.put(DatabaseHelper.KEY_INVOICE_LINE_QUANTITY, item.getQuantity());
    values.put(DatabaseHelper.KEY_INVOICE_LINE_TRACK_ID, item.getTrackId());
    values.put(DatabaseHelper.KEY_INVOICE_LINE_UNIT_PRICE, item.getUnitPrice());
		values.put(DatabaseHelper.KEY_INVOICE_LINE_VERSION, version);

		// Inserting Row
		database.insert(DatabaseHelper.TABLE_INVOICE_LINE, null, values);
		//close(); // Closing database connection
	}

	// Getting single item
	public InvoiceLine getItem(int primaryKey) {
		InvoiceLine item = new InvoiceLine();

		Cursor cursor = database.query(DatabaseHelper.TABLE_INVOICE_LINE,
				this.allColumns, DatabaseHelper.KEY_INVOICE_LINE_INVOICE_LINE_ID + "=?",
				new String[] { String.valueOf(primaryKey) }, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
            item = cursorToItem(cursor);
		}

		return item;
	}

	public String getVersion() {
		String result = "1000-01-01T18:00:00Z";

		String selectQuery = "SELECT max(" + DatabaseHelper.KEY_INVOICE_LINE_VERSION + ") FROM "
                          + DatabaseHelper.TABLE_INVOICE_LINE;
		Cursor cursor = database.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			result = cursor.getString(0);
		}

		return result;
	}

	public List<InvoiceLine> getAll() {
		List<InvoiceLine> itemList = new ArrayList<InvoiceLine>();

    Cursor cursor = database.query(DatabaseHelper.TABLE_INVOICE_LINE,
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

  private InvoiceLine cursorToItem(Cursor cursor){
    InvoiceLine item = new InvoiceLine();
    // ignore cursor.getInt(0) since it is not relevant

    item.setInvoiceId(cursor.getInt(2));
    item.setInvoiceLineId(cursor.getInt(3));
    item.setQuantity(cursor.getInt(4));
    item.setTrackId(cursor.getInt(5));
    item.setUnitPrice(cursor.getString(6));
    try {
        item.setVersion(Common.DATE_FORMATTER.parse(cursor.getString(6)));
    } catch (ParseException e) {
        e.printStackTrace();
    }


    return item;
  }
}
