package com.collinforrester.appstore.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	
	static final int DELAY = 60000; //a minute
	private boolean runFlag = false;
	private static final String TAG = UpdaterService.class.getSimpleName();
	private Updater updater;
	MainApplication app;
	DbHelper dbHelper;
	SQLiteDatabase db;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public ArrayList<Application> getApplications(int user, int storeID) {
		Updater updater = new Updater();
		ArrayList<Application> aList = updater.getApps(user, storeID);
		return aList;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.updater = new Updater();
		this.app = (MainApplication)getApplication();
		if(this.dbHelper == null) {
			this.dbHelper = new DbHelper(this);
		}
		Log.i(TAG, "onCreated");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		this.runFlag = true;
		this.app.setUpdaterRunning(this.runFlag);
		this.updater.start();
		Log.i(TAG, "onStarted");
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.runFlag = false;
		this.app.setUpdaterRunning(this.runFlag);
		this.updater.interrupt();
		this.updater = null;
		if(this.db != null) {
			this.db.close();
		}
		this.db = null;
		this.dbHelper = null;
		Log.i(TAG, "onDestroyed");
	}
	
	
	/***
	 * Sends notification for an update to an application if the major version 
	 * numbers don't match in the JSON response
	 */
	
	public void sendNotificationForAppUpdate(int appID) {
		if(this.app.getInstalledUserApps().contains(appID)) {
			int icon = R.drawable.icon; //need a notification icon
			CharSequence text = "An update is available.";
			long when = System.currentTimeMillis();
			Context context = getApplicationContext();
			CharSequence title = "Updates are available.";
			CharSequence cText = "An update is available for " + this.app.getSelectedAppInfo(appID).getTitle();
			Intent i = new Intent(this, DefaultScreenActivity.class);
	    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);
	
	    	// the next two lines initialize the Notification, using the configurations above
	    	Notification notification = new Notification(icon, text, when);
	    	notification.flags |= notification.FLAG_AUTO_CANCEL;
	    	notification.setLatestEventInfo(context, title, cText, contentIntent);
	    	
	    	String ns = Context.NOTIFICATION_SERVICE;
	    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
	    	
	    	mNotificationManager.notify(1, notification);
		} else {
			Log.i(TAG, "Update available, but user does not have app installed");
		}
	}
	public MainApplication getThisApplication() {
		return this.app;
	}
	/*
	 * Thread that performs the actual update
	 */
	private class Updater extends Thread {
		SQLiteDatabase db;
		public Updater() {
			super("UpdaterService-Updater");
		}
		
		@Override
		public void run() {
			UpdaterService updaterService = UpdaterService.this;
			while(updaterService.runFlag) {
				//Log.d(TAG, "Updater running");
				try {
					getApps(1, 1);
					getAllReviewsForStore(1);
					//Log.d(TAG, "Updater Ran..");
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					updaterService.runFlag = false;
				}
			}
		}
		public ArrayList<Rating> getAllReviewsForStore(int storeID) {
			ArrayList<Rating> rval = new ArrayList<Rating>();
			try {
				////Log.e(TAG, "Starting getAllReviewsForApp, appID: " + storeID);
				String _URL = "http://appstore.collinforrester.com/webservice.php?f=ratings&storeID=" + storeID;
				////Log.e(TAG, "URL: " + _URL);
				URL webservice = new URL(_URL);
				URLConnection webstore = webservice.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						webstore.getInputStream()));
				
				if(this.db == null || !this.db.isOpen()) {
					this.db = dbHelper.getReadableDatabase();
				}
				String line = in.readLine();
				//Log.e(TAG, "line: " + line);
				if(line != null) {
					JSONObject jObj = new JSONObject(line);
				
					JSONArray revArr = jObj.getJSONArray("reviews");
					//Log.e(TAG, "appList count (start):" + rval.size());
					ContentValues values = new ContentValues();
					for(int x = 0; x < revArr.length(); x++) {
						JSONObject revObj = revArr.getJSONObject(x);
						values.clear();
						//Log.e(TAG, "revObj: " + revObj.toString());
						
						JSONObject jo = revObj.getJSONObject("review");
						//Log.e(TAG, "jo: " + jo.toString());
						
						int review_id = jo.getInt("REVIEW_ID");
						int app_id = jo.getInt("APP_ID");
						int rating = jo.getInt("RATING");
						String id = jo.getString("ID");
						String comments = jo.getString("COMMENTS");
						String submitted_date = jo.getString("SUBMITTED_DATE");
						values.put(DbHelper.REVIEW_ID, review_id);
						values.put(DbHelper.APP_ID_FOR_REVIEW, app_id);
						values.put(DbHelper.RATING, rating);
						values.put(DbHelper.USER_ID, id);
						values.put(DbHelper.COMMENTS, comments);
						values.put(DbHelper.SUBMITTED_DATE, submitted_date);
						values.put(DbHelper.STORE_ID, jo.getInt("STORE_ID"));
						values.put(DbHelper.TITLE, jo.getString("TITLE"));
						values.put(DbHelper.REVIEW_PHONE_ID, jo.getString("PHONE_ID"));
						this.db.replaceOrThrow(dbHelper.REVIEW_TABLE, null, values);
					}
					this.db.close();
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return rval;
		}
		public ArrayList<Application> getApps(int user, int storeID) {
			ArrayList<Application> appList = new ArrayList<Application>();
			try {
				String _URL = "http://appstore.collinforrester.com/webservice.php?f=apps&ID=" +
						user + "&storeID=" + storeID;
				Log.d(TAG, "URL: " + _URL);
				URL webservice = new URL(_URL);
				URLConnection webstore = webservice.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						webstore.getInputStream()));
				String line = in.readLine();
				if(line != null) {
					JSONObject jObj = new JSONObject(line);
				
					JSONArray appsArr = jObj.getJSONArray("apps");
					ContentValues values = new ContentValues();
					for(int x = 0; x < appsArr.length(); x++) {
						JSONObject appObj = appsArr.getJSONObject(x);
						values.clear();
						//Log.e(TAG, "appObj: " + appObj.toString());
						
						JSONObject jo = appObj.getJSONObject("app");
						//Log.e(TAG, "jo: " + jo.toString());
						
						appList.add(new Application(jo.getInt("APP_ID"), jo.getString("STORE_NAME"), 
								jo.getString("SUBMITTED_DATE"), jo.getString("CATEGORY_TEXT"),
								jo.getString("VISIBLE"), jo.getString("LAST_UPDATED"), 
								jo.getString("URL"), jo.getString("TAGS"), jo.getString("TITLE"),
								jo.getString("DESCRIPTION")));
						StringBuilder builder = new StringBuilder();
						builder.append("APP_ID: " + jo.getInt("APP_ID") + ", ");
						builder.append("STORE_NAME: " + jo.getString("STORE_NAME") + ", ");
						builder.append("SUBMITTED_DATE: " + jo.getString("SUBMITTED_DATE") + ", ");
						builder.append("CATEGORY_TEXT: " + jo.getString("CATEGORY_TEXT") + ", ");
						builder.append("VISIBLE: " + jo.getString("VISIBLE") + ", ");
						builder.append("LAST_UPDATED: " + jo.getString("LAST_UPDATED") + ", ");
						builder.append("URL: " + jo.getString("URL") + ", ");
						builder.append("TAGS: " + jo.getString("TAGS") + ", ");
						builder.append("TITLE: " + jo.getString("TITLE") + ", ");
						builder.append("DESCRIPTION: " + jo.getString("DESCRIPTION") + ". ");
						//Log.e(TAG, "inserting now...");
						/*
						 * Order in DB:
						 * APP_ID ,TAGS +CATEGORY_TEXT +  SUBMITTED_DATE + LAST_UPDATED + URL 
						 * STORE_NAME
						 */
						
						int oldVersion = getThisApplication().getCurrentPackageVersion(jo.getInt("APP_ID"));
						int newVersion = jo.getInt("VERSION_MAJOR");
						Log.d(TAG, "New version = " + newVersion + " && old version = " + oldVersion + 
								" for application ID " + jo.getInt("APP_ID"));
						if( newVersion > oldVersion && oldVersion != -1) {
							sendNotificationForAppUpdate(jo.getInt("APP_ID"));
						}

						if(this.db == null || !this.db.isOpen()) {
							this.db = dbHelper.getReadableDatabase();
						}
						values.put(DbHelper.APP_ID, jo.getInt("APP_ID"));
						values.put(DbHelper.TAGS, jo.getString("TAGS"));
						values.put(DbHelper.CATEGORY_TEXT, jo.getString("CATEGORY_TEXT"));
						values.put(DbHelper.SUBMITTED_DATE, jo.getString("SUBMITTED_DATE"));
						values.put(DbHelper.LAST_UPDATED, jo.getString("LAST_UPDATED"));
						values.put(DbHelper.URL, jo.getString("URL"));
						values.put(DbHelper.STORE_NAME, jo.getString("STORE_NAME"));
						values.put(DbHelper.TITLE, jo.getString("TITLE"));
						values.put(DbHelper.DESCRIPTION, jo.getString("DESCRIPTION"));
						values.put(DbHelper.VERSION_MAJOR, jo.getString("VERSION_MAJOR"));
						values.put(DbHelper.VERSION_MINOR, jo.getString("VERSION_MINOR"));
						values.put(DbHelper.AUTHOR, jo.getString("AUTHOR"));
						values.put(DbHelper.FILENAME, jo.getString("FILENAME"));
						values.put(DbHelper.PACKAGE_NAME, jo.getString("PACKAGE_NAME"));
						this.db.replaceOrThrow(dbHelper.APP_TABLE, null, values);
						//Log.e(TAG, builder.toString());

					}
					this.db.close();
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return appList;
		} 
	}
}
