package com.collinforrester.appstore.client;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	
	/*
	 * App Table Stuff
	 */
	public static final String TAG = "DbHelper";
	public static final String DB_NAME = "myappstore.db";
	public static final int DB_VERSION = 1;
	public static final String APP_TABLE = "tbl_applications";
	public static final String APP_ID = BaseColumns._ID;
	public static final String LAST_UPDATED = "last_updated";
	public static final String STORE_NAME = "store_name";
	public static final String URL = "url";
	public static final String CATEGORY_TEXT = "category_text";
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String TAGS = "tags";
	public static final String SUBMITTED_DATE = "submitted_date";
	public static final String VERSION_MAJOR = "version_major";
	public static final String VERSION_MINOR = "version_minor";
	public static final String AUTHOR = "author";
	public static final String FILENAME = "filename";
	public static final String PACKAGE_NAME = "package_name";
	
	/*
	 * Review Table Stuff
	 */
	public static final String REVIEW_ID = BaseColumns._ID;
	public static final String REVIEW_TABLE = "tbl_reviews";
	public static final String APP_ID_FOR_REVIEW = "app_id";
	public static final String RATING = "rating";
	public static final String USER_ID = "id";
	public static final String COMMENTS = "comments";
	public static final String STORE_ID = "store_id";
	public static final String REVIEW_TITLE = "title";
	public static final String REVIEW_PHONE_ID = "phone_id";
	
	/*
	 * User Preference Storage Table 
	 */
	public static final String UP_TABLE = "tbl_user_preferences";
	public static final String UP_USERNAME = "username";
	public static final String UP_PASSWORD = "password";
	public static final String UP_EMAIL = "email";
	public static final String UP_IMEI = "imei";
	public static final String UP_STORECODE = "storecode";
	
	/*
	 * My Apps Table (how to tell what he's downloaded)
	 */
	public static final String MA_TABLE = "tbl_my_apps";
	public static final String MA_APP_ID = "app_id";
	
	Context context;
	
	//constructor
	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}
	
	//called only once, on DB creation
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		/*
		 * Apps table
		 */
		String sql = "create table " + APP_TABLE + " (" + APP_ID +
		" int primary key, " + TAGS + " text, " + CATEGORY_TEXT + " text, " + SUBMITTED_DATE + " int, " +
		LAST_UPDATED + " int, " + URL + " text, " + STORE_NAME + " text, " + TITLE + " text, " +
		DESCRIPTION + " text, " + VERSION_MAJOR + " text, " + VERSION_MINOR + " text, " + AUTHOR + " text" +
				", " + FILENAME + " text, " + PACKAGE_NAME + " text)";
		//Log.d(TAG, "onCreated  started sql: " + sql);
		db.execSQL(sql);
		//Log.d(TAG, "onCreated  finished sql: " + sql);
		
		/*
		 * Review table
		 */
		sql = "create table " + REVIEW_TABLE + " (" + REVIEW_ID +
		" int primary key, " + APP_ID_FOR_REVIEW + " int, " + 
		RATING + " int, " + USER_ID + " text, " + COMMENTS + " text, " + 
		SUBMITTED_DATE + " int, " + STORE_ID + " int, " + REVIEW_TITLE + " text, " + REVIEW_PHONE_ID + " text)";
		//Log.d(TAG, "onCreated  started sql: " + sql);
		db.execSQL(sql);
		//Log.d(TAG, "onCreated  finished sql: " + sql);
		
		/*
		 * User preference table
		 */
		sql = "create table " + UP_TABLE + " (" + UP_USERNAME +
		" text, " + UP_PASSWORD + " text, " + 
		UP_IMEI + " text, " + UP_EMAIL + " text, " + UP_STORECODE + " text)";
		db.execSQL(sql);
		
		/*
		 * My Apps table
		 */
		sql = "create table " + MA_TABLE + " (" + MA_APP_ID + " int primary key, " + VERSION_MAJOR +
		" int, " + VERSION_MINOR + " int)";
		db.execSQL(sql);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		/*
		 * Apps table
		 */
		String sql = "create table if not exists " + APP_TABLE + " (" + APP_ID +
		" int primary key, " + TAGS + " text, " + CATEGORY_TEXT + " text, " + SUBMITTED_DATE + " int, " +
		LAST_UPDATED + " int, " + URL + " text, " + STORE_NAME + " text, " + TITLE + " text, " +
		DESCRIPTION + " text, " + VERSION_MAJOR + " text, " + VERSION_MINOR + " text, " + AUTHOR + " text" +
		", " + FILENAME + " text, " + PACKAGE_NAME + " text)";
		//Log.d(TAG, "onCreated  started sql: " + sql);
		db.execSQL(sql);
		//Log.d(TAG, "onCreated  finished sql: " + sql);
		
		/*
		 * Review table
		 */
		sql = "create table if not exists " + REVIEW_TABLE + " (" + REVIEW_ID +
		" int primary key, " + APP_ID_FOR_REVIEW + " int, " + 
		RATING + " int, " + USER_ID + " text, " + COMMENTS + " text, " + 
		SUBMITTED_DATE + " int, " + STORE_ID + " int, " + REVIEW_TITLE + " text, " + REVIEW_PHONE_ID + " text)";
		//Log.d(TAG, "onCreated  started sql: " + sql);
		db.execSQL(sql);
		//Log.d(TAG, "onCreated  finished sql: " + sql);
		
		/*
		 * User preference table
		 */
		sql = "create table if not exists " + UP_TABLE + " (" + UP_USERNAME +
		" text, " + UP_PASSWORD + " text, " + 
		UP_IMEI + " text, " + UP_EMAIL + " text, " + UP_STORECODE + " text)";
		//Log.d(TAG, "onCreated  started sql: " + sql);
		db.execSQL(sql);
		//Log.d(TAG, "onCreated  finished sql: " + sql);

		/*
		 * My Apps table
		 */
		sql = "create table if not exists " + MA_TABLE + " (" + MA_APP_ID + " int primary key, " + VERSION_MAJOR +
		" int, " + VERSION_MINOR + " int)";
		db.execSQL(sql);
		
		super.onOpen(db);
	}
	
	private Cursor getUserPreferencesFromDb() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor rval = db.query(UP_TABLE, null, null, null, null, null, null);
		db.close();
		return rval;
	}
	
	public Cursor getDownloadedUserAppsFromDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		Log.i(TAG, "getDownloadedUserAppsFromDB");
		Cursor rval = db.query(MA_TABLE, null, null, null, null, null, null);
		Log.d(TAG, "cursor has " + rval.getColumnCount() + " columns, " + rval.getCount() + " rows");
		db.close();
		return rval;
	}
	
	public Cursor getApplicationInformationFromDb(int appID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor r = db.rawQuery("SELECT * FROM " + APP_TABLE + " WHERE " + APP_ID + " = ?",
				new String [] { String.valueOf(appID) } );
		Cursor rval = db.query(APP_TABLE, null, APP_ID + " = ?", new String [] { APP_ID }, 
				null, null, null);
		if(r.getCount() < 1)
			Log.e(TAG, "no rows in application table");
		db.close();
		return r;
	}
	
	public User getUserFromDb() {
		Cursor cursor = getUserPreferencesFromDb();
		/*
		 * 0: UP_USERNAME
		 * 1: UP_PASSWORD
		 * 2: UP_IMEI
		 * 3: UP_EMAIL
		 * 4: UP_STORECODE
		 */
		Log.e("getUserPreferences()", "Columns: " + cursor.getColumnCount() + ", Rows: " + cursor.getCount());

		User u = new User();
		String username = "";
		String password = "";
		String IMEI = "";
		String email = "";
		String storecode = "";
		while(cursor.moveToNext()) {
			username = cursor.getString(0) + "";
			password = cursor.getString(1) + "";
			IMEI = cursor.getString(2) + "";
			email = cursor.getString(3) + "";
			storecode = cursor.getString(4) + "";
			break;
		}
		if(username.equals("") || username == null)
			username = email;
		u.setUsername(username);
		u.setPassword(password);
		u.setIMEI(IMEI);
		u.setEmail(email);
		
		return u;
	}
	
	public Cursor getAllApplications() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor rval = db.query(APP_TABLE, null, null, null, null, null, null);
		db.close();
		return  rval;
	}
	
	public Cursor getReivewsForApplication(int appID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor rval =db.query(REVIEW_TABLE, null, null, new String [] { APP_ID_FOR_REVIEW + " = " + appID }, 
				null, null, SUBMITTED_DATE);
		db.close();
		return rval;
	}
	
	// Called whenever newVersion != oldVersion
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //
		// Typically do ALTER TABLE statements, but...we're just in development,
		// so:
		//Log.d(TAG, "onUpdated 1");
		db.execSQL("drop table if exists " + APP_TABLE); // drops the old database
		db.execSQL("drop table if exists " + REVIEW_TABLE); // drops the old database
		db.execSQL("drop table if exists " + UP_TABLE); // drops the old database
		db.execSQL("drop table if exists " + MA_TABLE);
		//Log.d(TAG, "onUpdated 2");
		onCreate(db); // run onCreate to get new database
	}
}
