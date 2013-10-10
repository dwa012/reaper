package com.github.dwa012.reaper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.Artist;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ArtistDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	private String[] allColumns = {
   DatabaseHelper.KEY_ARTIST_DB_ID,
    DatabaseHelper.KEY_ARTIST_ARTIST_ID,
    DatabaseHelper.KEY_ARTIST_NAME,
   DatabaseHelper.KEY_ARTIST_VERSION
  };

	public ArtistDataSource(Context context) {
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
	public void addItem(Artist item, String version) {

		ContentValues values = new ContentValues();
    values.put(DatabaseHelper.KEY_ARTIST_ARTIST_ID, item.getArtistId());
    values.put(DatabaseHelper.KEY_ARTIST_NAME, item.getName());
		values.put(DatabaseHelper.KEY_ARTIST_VERSION, version);

		// Inserting Row
		database.insert(DatabaseHelper.TABLE_ARTIST, null, values);
		//close(); // Closing database connection
	}

	// Getting single item
	public Artist getItem(int primaryKey) {
		Artist item = new Artist();

		Cursor cursor = database.query(DatabaseHelper.TABLE_ARTIST,
				this.allColumns, DatabaseHelper.KEY_ARTIST_ARTIST_ID + "=?",
				new String[] { String.valueOf(primaryKey) }, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
            item = cursorToItem(cursor);
		}

		return item;
	}

	public String getVersion() {
		String result = "1000-01-01T18:00:00Z";

		String selectQuery = "SELECT max(" + DatabaseHelper.KEY_ARTIST_VERSION + ") FROM "
                          + DatabaseHelper.TABLE_ARTIST;
		Cursor cursor = database.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			result = cursor.getString(0);
		}

		return result;
	}

	public List<Artist> getAll() {
		List<Artist> itemList = new ArrayList<Artist>();

    Cursor cursor = database.query(DatabaseHelper.TABLE_ARTIST,
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

  private Artist cursorToItem(Cursor cursor){
    Artist item = new Artist();
    // ignore cursor.getInt(0) since it is not relevant

    item.setArtistId(cursor.getInt(2));
    item.setName(cursor.getString(3));
    try {
        item.setVersion(Common.DATE_FORMATTER.parse(cursor.getString(3)));
    } catch (ParseException e) {
        e.printStackTrace();
    }


    return item;
  }
}
