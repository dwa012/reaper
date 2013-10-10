package com.github.dwa012.reaper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.MediaType;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MediaTypeDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	private String[] allColumns = {
   DatabaseHelper.KEY_MEDIA_TYPE_DB_ID,
    DatabaseHelper.KEY_MEDIA_TYPE_MEDIA_TYPE_ID,
    DatabaseHelper.KEY_MEDIA_TYPE_NAME,
   DatabaseHelper.KEY_MEDIA_TYPE_VERSION
  };

	public MediaTypeDataSource(Context context) {
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
	public void addItem(MediaType item, String version) {

		ContentValues values = new ContentValues();
    values.put(DatabaseHelper.KEY_MEDIA_TYPE_MEDIA_TYPE_ID, item.getMediaTypeId());
    values.put(DatabaseHelper.KEY_MEDIA_TYPE_NAME, item.getName());
		values.put(DatabaseHelper.KEY_MEDIA_TYPE_VERSION, version);

		// Inserting Row
		database.insert(DatabaseHelper.TABLE_MEDIA_TYPE, null, values);
		//close(); // Closing database connection
	}

	// Getting single item
	public MediaType getItem(int primaryKey) {
		MediaType item = new MediaType();

		Cursor cursor = database.query(DatabaseHelper.TABLE_MEDIA_TYPE,
				this.allColumns, DatabaseHelper.KEY_MEDIA_TYPE_MEDIA_TYPE_ID + "=?",
				new String[] { String.valueOf(primaryKey) }, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
            item = cursorToItem(cursor);
		}

		return item;
	}

	public String getVersion() {
		String result = "1000-01-01T18:00:00Z";

		String selectQuery = "SELECT max(" + DatabaseHelper.KEY_MEDIA_TYPE_VERSION + ") FROM "
                          + DatabaseHelper.TABLE_MEDIA_TYPE;
		Cursor cursor = database.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			result = cursor.getString(0);
		}

		return result;
	}

	public List<MediaType> getAll() {
		List<MediaType> itemList = new ArrayList<MediaType>();

    Cursor cursor = database.query(DatabaseHelper.TABLE_MEDIA_TYPE,
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

  private MediaType cursorToItem(Cursor cursor){
    MediaType item = new MediaType();
    // ignore cursor.getInt(0) since it is not relevant

    item.setMediaTypeId(cursor.getInt(2));
    item.setName(cursor.getString(3));
    try {
        item.setVersion(Common.DATE_FORMATTER.parse(cursor.getString(3)));
    } catch (ParseException e) {
        e.printStackTrace();
    }


    return item;
  }
}
