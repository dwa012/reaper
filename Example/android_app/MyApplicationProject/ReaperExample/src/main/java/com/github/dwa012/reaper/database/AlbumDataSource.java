package com.github.dwa012.reaper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.Album;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AlbumDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	private String[] allColumns = {
   DatabaseHelper.KEY_ALBUM_DB_ID,
    DatabaseHelper.KEY_ALBUM_ALBUM_ID,
    DatabaseHelper.KEY_ALBUM_ARTIST_ID,
    DatabaseHelper.KEY_ALBUM_TITLE,
   DatabaseHelper.KEY_ALBUM_VERSION
  };

	public AlbumDataSource(Context context) {
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
	public void addItem(Album item, String version) {

		ContentValues values = new ContentValues();
    values.put(DatabaseHelper.KEY_ALBUM_ALBUM_ID, item.getAlbumId());
    values.put(DatabaseHelper.KEY_ALBUM_ARTIST_ID, item.getArtistId());
    values.put(DatabaseHelper.KEY_ALBUM_TITLE, item.getTitle());
		values.put(DatabaseHelper.KEY_ALBUM_VERSION, version);

		// Inserting Row
		database.insert(DatabaseHelper.TABLE_ALBUM, null, values);
		//close(); // Closing database connection
	}

	// Getting single item
	public Album getItem(int primaryKey) {
		Album item = new Album();

		Cursor cursor = database.query(DatabaseHelper.TABLE_ALBUM,
				this.allColumns, DatabaseHelper.KEY_ALBUM_ALBUM_ID + "=?",
				new String[] { String.valueOf(primaryKey) }, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
            item = cursorToItem(cursor);
		}

		return item;
	}

	public String getVersion() {
		String result = "1000-01-01T18:00:00Z";

		String selectQuery = "SELECT max(" + DatabaseHelper.KEY_ALBUM_VERSION + ") FROM "
                          + DatabaseHelper.TABLE_ALBUM;
		Cursor cursor = database.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			result = cursor.getString(0);
		}

		return result;
	}

	public List<Album> getAll() {
		List<Album> itemList = new ArrayList<Album>();

    Cursor cursor = database.query(DatabaseHelper.TABLE_ALBUM,
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

  private Album cursorToItem(Cursor cursor){
    Album item = new Album();
    // ignore cursor.getInt(0) since it is not relevant

    item.setAlbumId(cursor.getInt(2));
    item.setArtistId(cursor.getInt(3));
    item.setTitle(cursor.getString(4));
    try {
        item.setVersion(Common.DATE_FORMATTER.parse(cursor.getString(4)));
    } catch (ParseException e) {
        e.printStackTrace();
    }


    return item;
  }
}
