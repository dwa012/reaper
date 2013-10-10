package com.github.dwa012.reaper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.github.dwa012.reaper.util.Common;
import com.github.dwa012.reaper.model.Track;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TrackDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	private String[] allColumns = {
   DatabaseHelper.KEY_TRACK_DB_ID,
    DatabaseHelper.KEY_TRACK_ALBUM_ID,
    DatabaseHelper.KEY_TRACK_BYTES,
    DatabaseHelper.KEY_TRACK_COMPOSER,
    DatabaseHelper.KEY_TRACK_GENRE_ID,
    DatabaseHelper.KEY_TRACK_MEDIA_TYPE_ID,
    DatabaseHelper.KEY_TRACK_MILLISECONDS,
    DatabaseHelper.KEY_TRACK_NAME,
    DatabaseHelper.KEY_TRACK_TRACK_ID,
    DatabaseHelper.KEY_TRACK_UNIT_PRICE,
   DatabaseHelper.KEY_TRACK_VERSION
  };

	public TrackDataSource(Context context) {
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
	public void addItem(Track item, String version) {

		ContentValues values = new ContentValues();
    values.put(DatabaseHelper.KEY_TRACK_ALBUM_ID, item.getAlbumId());
    values.put(DatabaseHelper.KEY_TRACK_BYTES, item.getBytes());
    values.put(DatabaseHelper.KEY_TRACK_COMPOSER, item.getComposer());
    values.put(DatabaseHelper.KEY_TRACK_GENRE_ID, item.getGenreId());
    values.put(DatabaseHelper.KEY_TRACK_MEDIA_TYPE_ID, item.getMediaTypeId());
    values.put(DatabaseHelper.KEY_TRACK_MILLISECONDS, item.getMilliseconds());
    values.put(DatabaseHelper.KEY_TRACK_NAME, item.getName());
    values.put(DatabaseHelper.KEY_TRACK_TRACK_ID, item.getTrackId());
    values.put(DatabaseHelper.KEY_TRACK_UNIT_PRICE, item.getUnitPrice());
		values.put(DatabaseHelper.KEY_TRACK_VERSION, version);

		// Inserting Row
		database.insert(DatabaseHelper.TABLE_TRACK, null, values);
		//close(); // Closing database connection
	}

	// Getting single item
	public Track getItem(int primaryKey) {
		Track item = new Track();

		Cursor cursor = database.query(DatabaseHelper.TABLE_TRACK,
				this.allColumns, DatabaseHelper.KEY_TRACK_TRACK_ID + "=?",
				new String[] { String.valueOf(primaryKey) }, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
            item = cursorToItem(cursor);
		}

		return item;
	}

	public String getVersion() {
		String result = "1000-01-01T18:00:00Z";

		String selectQuery = "SELECT max(" + DatabaseHelper.KEY_TRACK_VERSION + ") FROM "
                          + DatabaseHelper.TABLE_TRACK;
		Cursor cursor = database.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			result = cursor.getString(0);
		}

		return result;
	}

	public List<Track> getAll() {
		List<Track> itemList = new ArrayList<Track>();

    Cursor cursor = database.query(DatabaseHelper.TABLE_TRACK,
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

  private Track cursorToItem(Cursor cursor){
    Track item = new Track();
    // ignore cursor.getInt(0) since it is not relevant

    item.setAlbumId(cursor.getInt(2));
    item.setBytes(cursor.getInt(3));
    item.setComposer(cursor.getString(4));
    item.setGenreId(cursor.getInt(5));
    item.setMediaTypeId(cursor.getInt(6));
    item.setMilliseconds(cursor.getInt(7));
    item.setName(cursor.getString(8));
    item.setTrackId(cursor.getInt(9));
    item.setUnitPrice(cursor.getString(10));
    try {
        item.setVersion(Common.DATE_FORMATTER.parse(cursor.getString(10)));
    } catch (ParseException e) {
        e.printStackTrace();
    }


    return item;
  }
}
