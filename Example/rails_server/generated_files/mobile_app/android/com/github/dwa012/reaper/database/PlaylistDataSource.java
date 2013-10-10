package com.github.dwa012.reaper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.Playlist;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	private String[] allColumns = {
   DatabaseHelper.KEY_PLAYLIST_DB_ID,
    DatabaseHelper.KEY_PLAYLIST_NAME,
    DatabaseHelper.KEY_PLAYLIST_PLAYLIST_ID,
   DatabaseHelper.KEY_PLAYLIST_VERSION
  };

	public PlaylistDataSource(Context context) {
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
	public void addItem(Playlist item, String version) {

		ContentValues values = new ContentValues();
    values.put(DatabaseHelper.KEY_PLAYLIST_NAME, item.getName());
    values.put(DatabaseHelper.KEY_PLAYLIST_PLAYLIST_ID, item.getPlaylistId());
		values.put(DatabaseHelper.KEY_PLAYLIST_VERSION, version);

		// Inserting Row
		database.insert(DatabaseHelper.TABLE_PLAYLIST, null, values);
		//close(); // Closing database connection
	}

	// Getting single item
	public Playlist getItem(int primaryKey) {
		Playlist item = new Playlist();

		Cursor cursor = database.query(DatabaseHelper.TABLE_PLAYLIST,
				this.allColumns, DatabaseHelper.KEY_PLAYLIST_PLAYLIST_ID + "=?",
				new String[] { String.valueOf(primaryKey) }, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
            item = cursorToItem(cursor);
		}

		return item;
	}

	public String getVersion() {
		String result = "1000-01-01T18:00:00Z";

		String selectQuery = "SELECT max(" + DatabaseHelper.KEY_PLAYLIST_VERSION + ") FROM "
                          + DatabaseHelper.TABLE_PLAYLIST;
		Cursor cursor = database.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			result = cursor.getString(0);
		}

		return result;
	}

	public List<Playlist> getAll() {
		List<Playlist> itemList = new ArrayList<Playlist>();

    Cursor cursor = database.query(DatabaseHelper.TABLE_PLAYLIST,
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

  private Playlist cursorToItem(Cursor cursor){
    Playlist item = new Playlist();
    // ignore cursor.getInt(0) since it is not relevant

    item.setName(cursor.getString(2));
    item.setPlaylistId(cursor.getInt(3));
    try {
        item.setVersion(Common.DATE_FORMATTER.parse(cursor.getString(3)));
    } catch (ParseException e) {
        e.printStackTrace();
    }


    return item;
  }
}
