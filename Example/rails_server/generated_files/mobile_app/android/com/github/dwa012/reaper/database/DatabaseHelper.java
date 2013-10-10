package com.github.dwa012.reaper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;

public class DatabaseHelper extends SQLiteOpenHelper {

  // Used to upgrade the BD as needed
  private static final int DATABASE_VERSION = 1;

  // Database Name
  private static final String DATABASE_NAME = "reaper.db";

  // TABLE NAMES
  static final String TABLE_ALBUM = "album";
  static final String TABLE_ARTIST = "artist";
  static final String TABLE_CUSTOMER = "customer";
  static final String TABLE_EMPLOYEE = "employee";
  static final String TABLE_GENRE = "genre";
  static final String TABLE_INVOICE_LINE = "invoiceline";
  static final String TABLE_INVOICE = "invoice";
  static final String TABLE_MEDIA_TYPE = "mediatype";
  static final String TABLE_PLAYLIST = "playlist";
  static final String TABLE_TRACK = "track";

  // Columns for table Album
  static final String KEY_ALBUM_DB_ID = "_id";
  static final String KEY_ALBUM_ALBUM_ID = "AlbumId";
  static final String KEY_ALBUM_ARTIST_ID = "ArtistId";
  static final String KEY_ALBUM_TITLE = "Title";
  static final String KEY_ALBUM_VERSION = "version";

  // Columns for table Artist
  static final String KEY_ARTIST_DB_ID = "_id";
  static final String KEY_ARTIST_ARTIST_ID = "ArtistId";
  static final String KEY_ARTIST_NAME = "Name";
  static final String KEY_ARTIST_VERSION = "version";

  // Columns for table Customer
  static final String KEY_CUSTOMER_DB_ID = "_id";
  static final String KEY_CUSTOMER_ADDRESS = "Address";
  static final String KEY_CUSTOMER_CITY = "City";
  static final String KEY_CUSTOMER_COMPANY = "Company";
  static final String KEY_CUSTOMER_COUNTRY = "Country";
  static final String KEY_CUSTOMER_CUSTOMER_ID = "CustomerId";
  static final String KEY_CUSTOMER_EMAIL = "Email";
  static final String KEY_CUSTOMER_FAX = "Fax";
  static final String KEY_CUSTOMER_FIRST_NAME = "FirstName";
  static final String KEY_CUSTOMER_LAST_NAME = "LastName";
  static final String KEY_CUSTOMER_PHONE = "Phone";
  static final String KEY_CUSTOMER_POSTAL_CODE = "PostalCode";
  static final String KEY_CUSTOMER_STATE = "State";
  static final String KEY_CUSTOMER_SUPPORT_REP_ID = "SupportRepId";
  static final String KEY_CUSTOMER_VERSION = "version";

  // Columns for table Employee
  static final String KEY_EMPLOYEE_DB_ID = "_id";
  static final String KEY_EMPLOYEE_ADDRESS = "Address";
  static final String KEY_EMPLOYEE_BIRTH_DATE = "BirthDate";
  static final String KEY_EMPLOYEE_CITY = "City";
  static final String KEY_EMPLOYEE_COUNTRY = "Country";
  static final String KEY_EMPLOYEE_EMAIL = "Email";
  static final String KEY_EMPLOYEE_EMPLOYEE_ID = "EmployeeId";
  static final String KEY_EMPLOYEE_FAX = "Fax";
  static final String KEY_EMPLOYEE_FIRST_NAME = "FirstName";
  static final String KEY_EMPLOYEE_HIRE_DATE = "HireDate";
  static final String KEY_EMPLOYEE_LAST_NAME = "LastName";
  static final String KEY_EMPLOYEE_PHONE = "Phone";
  static final String KEY_EMPLOYEE_POSTAL_CODE = "PostalCode";
  static final String KEY_EMPLOYEE_REPORTS_TO = "ReportsTo";
  static final String KEY_EMPLOYEE_STATE = "State";
  static final String KEY_EMPLOYEE_TITLE = "Title";
  static final String KEY_EMPLOYEE_VERSION = "version";

  // Columns for table Genre
  static final String KEY_GENRE_DB_ID = "_id";
  static final String KEY_GENRE_GENRE_ID = "GenreId";
  static final String KEY_GENRE_NAME = "Name";
  static final String KEY_GENRE_VERSION = "version";

  // Columns for table InvoiceLine
  static final String KEY_INVOICE_LINE_DB_ID = "_id";
  static final String KEY_INVOICE_LINE_INVOICE_ID = "InvoiceId";
  static final String KEY_INVOICE_LINE_INVOICE_LINE_ID = "InvoiceLineId";
  static final String KEY_INVOICE_LINE_QUANTITY = "Quantity";
  static final String KEY_INVOICE_LINE_TRACK_ID = "TrackId";
  static final String KEY_INVOICE_LINE_UNIT_PRICE = "UnitPrice";
  static final String KEY_INVOICE_LINE_VERSION = "version";

  // Columns for table Invoice
  static final String KEY_INVOICE_DB_ID = "_id";
  static final String KEY_INVOICE_BILLING_ADDRESS = "BillingAddress";
  static final String KEY_INVOICE_BILLING_CITY = "BillingCity";
  static final String KEY_INVOICE_BILLING_COUNTRY = "BillingCountry";
  static final String KEY_INVOICE_BILLING_POSTAL_CODE = "BillingPostalCode";
  static final String KEY_INVOICE_BILLING_STATE = "BillingState";
  static final String KEY_INVOICE_CUSTOMER_ID = "CustomerId";
  static final String KEY_INVOICE_INVOICE_DATE = "InvoiceDate";
  static final String KEY_INVOICE_INVOICE_ID = "InvoiceId";
  static final String KEY_INVOICE_TOTAL = "Total";
  static final String KEY_INVOICE_VERSION = "version";

  // Columns for table MediaType
  static final String KEY_MEDIA_TYPE_DB_ID = "_id";
  static final String KEY_MEDIA_TYPE_MEDIA_TYPE_ID = "MediaTypeId";
  static final String KEY_MEDIA_TYPE_NAME = "Name";
  static final String KEY_MEDIA_TYPE_VERSION = "version";

  // Columns for table Playlist
  static final String KEY_PLAYLIST_DB_ID = "_id";
  static final String KEY_PLAYLIST_NAME = "Name";
  static final String KEY_PLAYLIST_PLAYLIST_ID = "PlaylistId";
  static final String KEY_PLAYLIST_VERSION = "version";

  // Columns for table Track
  static final String KEY_TRACK_DB_ID = "_id";
  static final String KEY_TRACK_ALBUM_ID = "AlbumId";
  static final String KEY_TRACK_BYTES = "Bytes";
  static final String KEY_TRACK_COMPOSER = "Composer";
  static final String KEY_TRACK_GENRE_ID = "GenreId";
  static final String KEY_TRACK_MEDIA_TYPE_ID = "MediaTypeId";
  static final String KEY_TRACK_MILLISECONDS = "Milliseconds";
  static final String KEY_TRACK_NAME = "Name";
  static final String KEY_TRACK_TRACK_ID = "TrackId";
  static final String KEY_TRACK_UNIT_PRICE = "UnitPrice";
  static final String KEY_TRACK_VERSION = "version";


  // Create statement for table Album
  private String CREATE_ALBUM_TABLE = "CREATE TABLE " + TABLE_ALBUM + "("
      + KEY_ALBUM_DB_ID + " INTEGER PRIMARY KEY autoincrement,"
      + KEY_ALBUM_ALBUM_ID + " INTEGER UNIQUE,"
      + KEY_ALBUM_ARTIST_ID + " INTEGER ,"
      + KEY_ALBUM_TITLE + " TEXT ,"
      + KEY_ALBUM_VERSION + " TEXT"
      + ")";

  // Create statement for table Artist
  private String CREATE_ARTIST_TABLE = "CREATE TABLE " + TABLE_ARTIST + "("
      + KEY_ARTIST_DB_ID + " INTEGER PRIMARY KEY autoincrement,"
      + KEY_ARTIST_ARTIST_ID + " INTEGER UNIQUE,"
      + KEY_ARTIST_NAME + " TEXT ,"
      + KEY_ARTIST_VERSION + " TEXT"
      + ")";

  // Create statement for table Customer
  private String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + TABLE_CUSTOMER + "("
      + KEY_CUSTOMER_DB_ID + " INTEGER PRIMARY KEY autoincrement,"
      + KEY_CUSTOMER_ADDRESS + " TEXT ,"
      + KEY_CUSTOMER_CITY + " TEXT ,"
      + KEY_CUSTOMER_COMPANY + " TEXT ,"
      + KEY_CUSTOMER_COUNTRY + " TEXT ,"
      + KEY_CUSTOMER_CUSTOMER_ID + " INTEGER UNIQUE,"
      + KEY_CUSTOMER_EMAIL + " TEXT ,"
      + KEY_CUSTOMER_FAX + " TEXT ,"
      + KEY_CUSTOMER_FIRST_NAME + " TEXT ,"
      + KEY_CUSTOMER_LAST_NAME + " TEXT ,"
      + KEY_CUSTOMER_PHONE + " TEXT ,"
      + KEY_CUSTOMER_POSTAL_CODE + " TEXT ,"
      + KEY_CUSTOMER_STATE + " TEXT ,"
      + KEY_CUSTOMER_SUPPORT_REP_ID + " INTEGER ,"
      + KEY_CUSTOMER_VERSION + " TEXT"
      + ")";

  // Create statement for table Employee
  private String CREATE_EMPLOYEE_TABLE = "CREATE TABLE " + TABLE_EMPLOYEE + "("
      + KEY_EMPLOYEE_DB_ID + " INTEGER PRIMARY KEY autoincrement,"
      + KEY_EMPLOYEE_ADDRESS + " TEXT ,"
      + KEY_EMPLOYEE_BIRTH_DATE + " TEXT ,"
      + KEY_EMPLOYEE_CITY + " TEXT ,"
      + KEY_EMPLOYEE_COUNTRY + " TEXT ,"
      + KEY_EMPLOYEE_EMAIL + " TEXT ,"
      + KEY_EMPLOYEE_EMPLOYEE_ID + " INTEGER UNIQUE,"
      + KEY_EMPLOYEE_FAX + " TEXT ,"
      + KEY_EMPLOYEE_FIRST_NAME + " TEXT ,"
      + KEY_EMPLOYEE_HIRE_DATE + " TEXT ,"
      + KEY_EMPLOYEE_LAST_NAME + " TEXT ,"
      + KEY_EMPLOYEE_PHONE + " TEXT ,"
      + KEY_EMPLOYEE_POSTAL_CODE + " TEXT ,"
      + KEY_EMPLOYEE_REPORTS_TO + " INTEGER ,"
      + KEY_EMPLOYEE_STATE + " TEXT ,"
      + KEY_EMPLOYEE_TITLE + " TEXT ,"
      + KEY_EMPLOYEE_VERSION + " TEXT"
      + ")";

  // Create statement for table Genre
  private String CREATE_GENRE_TABLE = "CREATE TABLE " + TABLE_GENRE + "("
      + KEY_GENRE_DB_ID + " INTEGER PRIMARY KEY autoincrement,"
      + KEY_GENRE_GENRE_ID + " INTEGER UNIQUE,"
      + KEY_GENRE_NAME + " TEXT ,"
      + KEY_GENRE_VERSION + " TEXT"
      + ")";

  // Create statement for table InvoiceLine
  private String CREATE_INVOICE_LINE_TABLE = "CREATE TABLE " + TABLE_INVOICE_LINE + "("
      + KEY_INVOICE_LINE_DB_ID + " INTEGER PRIMARY KEY autoincrement,"
      + KEY_INVOICE_LINE_INVOICE_ID + " INTEGER ,"
      + KEY_INVOICE_LINE_INVOICE_LINE_ID + " INTEGER UNIQUE,"
      + KEY_INVOICE_LINE_QUANTITY + " INTEGER ,"
      + KEY_INVOICE_LINE_TRACK_ID + " INTEGER ,"
      + KEY_INVOICE_LINE_UNIT_PRICE + " TEXT ,"
      + KEY_INVOICE_LINE_VERSION + " TEXT"
      + ")";

  // Create statement for table Invoice
  private String CREATE_INVOICE_TABLE = "CREATE TABLE " + TABLE_INVOICE + "("
      + KEY_INVOICE_DB_ID + " INTEGER PRIMARY KEY autoincrement,"
      + KEY_INVOICE_BILLING_ADDRESS + " TEXT ,"
      + KEY_INVOICE_BILLING_CITY + " TEXT ,"
      + KEY_INVOICE_BILLING_COUNTRY + " TEXT ,"
      + KEY_INVOICE_BILLING_POSTAL_CODE + " TEXT ,"
      + KEY_INVOICE_BILLING_STATE + " TEXT ,"
      + KEY_INVOICE_CUSTOMER_ID + " INTEGER ,"
      + KEY_INVOICE_INVOICE_DATE + " TEXT ,"
      + KEY_INVOICE_INVOICE_ID + " INTEGER UNIQUE,"
      + KEY_INVOICE_TOTAL + " TEXT ,"
      + KEY_INVOICE_VERSION + " TEXT"
      + ")";

  // Create statement for table MediaType
  private String CREATE_MEDIA_TYPE_TABLE = "CREATE TABLE " + TABLE_MEDIA_TYPE + "("
      + KEY_MEDIA_TYPE_DB_ID + " INTEGER PRIMARY KEY autoincrement,"
      + KEY_MEDIA_TYPE_MEDIA_TYPE_ID + " INTEGER UNIQUE,"
      + KEY_MEDIA_TYPE_NAME + " TEXT ,"
      + KEY_MEDIA_TYPE_VERSION + " TEXT"
      + ")";

  // Create statement for table Playlist
  private String CREATE_PLAYLIST_TABLE = "CREATE TABLE " + TABLE_PLAYLIST + "("
      + KEY_PLAYLIST_DB_ID + " INTEGER PRIMARY KEY autoincrement,"
      + KEY_PLAYLIST_NAME + " TEXT ,"
      + KEY_PLAYLIST_PLAYLIST_ID + " INTEGER UNIQUE,"
      + KEY_PLAYLIST_VERSION + " TEXT"
      + ")";

  // Create statement for table Track
  private String CREATE_TRACK_TABLE = "CREATE TABLE " + TABLE_TRACK + "("
      + KEY_TRACK_DB_ID + " INTEGER PRIMARY KEY autoincrement,"
      + KEY_TRACK_ALBUM_ID + " INTEGER ,"
      + KEY_TRACK_BYTES + " INTEGER ,"
      + KEY_TRACK_COMPOSER + " TEXT ,"
      + KEY_TRACK_GENRE_ID + " INTEGER ,"
      + KEY_TRACK_MEDIA_TYPE_ID + " INTEGER ,"
      + KEY_TRACK_MILLISECONDS + " INTEGER ,"
      + KEY_TRACK_NAME + " TEXT ,"
      + KEY_TRACK_TRACK_ID + " INTEGER UNIQUE,"
      + KEY_TRACK_UNIT_PRICE + " TEXT ,"
      + KEY_TRACK_VERSION + " TEXT"
      + ")";


  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  // Creating Tables
  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_ALBUM_TABLE);
    db.execSQL(CREATE_ARTIST_TABLE);
    db.execSQL(CREATE_CUSTOMER_TABLE);
    db.execSQL(CREATE_EMPLOYEE_TABLE);
    db.execSQL(CREATE_GENRE_TABLE);
    db.execSQL(CREATE_INVOICE_LINE_TABLE);
    db.execSQL(CREATE_INVOICE_TABLE);
    db.execSQL(CREATE_MEDIA_TYPE_TABLE);
    db.execSQL(CREATE_PLAYLIST_TABLE);
    db.execSQL(CREATE_TRACK_TABLE);
  }

  // Upgrading database
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUM);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIST);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRE);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVOICE_LINE);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVOICE);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA_TYPE);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACK);

    // Recreate tables again
    onCreate(db);
  }
}
