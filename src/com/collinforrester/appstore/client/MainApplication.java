package com.collinforrester.appstore.client;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class MainApplication extends android.app.Application implements OnSharedPreferenceChangeListener {

	private static final String TAG = MainApplication.class.getSimpleName();
	private SharedPreferences prefs;
	public User user;
	private Boolean updaterRunning;
	private List<PackageInfo> packages;
	private List<String> packageNames;
	public ArrayList<Application> appList;
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.i(TAG, key + " has been changed..");
		if(TextUtils.equals(key, "storeCode")) {
			this.user.setStoreCode(prefs.getString(key, ""));
			this.user.setStore(null);
			if(this.user.isUserConnected()) {
				Toast.makeText(this, "Disconnected from store.", Toast.LENGTH_LONG).show();
			}
			this.user.connectToStore(false);
		}
		if(TextUtils.equals(key, "chkRunUpdater")) {
			if(prefs.getBoolean("chkRunUpdater", true)) {
				if(!this.updaterRunning)
					startService(new Intent(getBaseContext(), UpdaterService.class));
			} else {
				if(this.updaterRunning)
					stopService(new Intent(getBaseContext(), UpdaterService.class));
			}
		}
	}
	
	public void setUpdaterRunning(Boolean r) {
		this.updaterRunning = r;
	}
	public Boolean isUpdaterRunning() {
		return this.updaterRunning;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		this.user = new User();
		this.updaterRunning = false;
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		this.user.setIMEI(tm.getDeviceId());
		Log.i(TAG, "onCreated");
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "onTerminate");
	}
	
	public synchronized User getUser() {
		if(this.user == null) {
			String s = this.prefs.getString("storeCode", "");
			if(!TextUtils.isEmpty(s)) {
				this.user = new User();
				this.user.setStoreCode(s);
			}
		}
		return this.user;
	}
	
	public float getApplicationRating(int appID, int storeID) {
		float appRating = 0;
		ArrayList<Rating> ratings = getReviewsFromDatabase(storeID, appID);
		int sum = 0;
		int count = 0;
		for(int i = 0; i < ratings.size(); i++) {
			if(ratings.get(i).getAppID() == appID) {
				sum += ratings.get(i).getStars();
				count++;
			}
		}
		if(count > 0)
			appRating = sum / count;
		if(count == 1)
			appRating = sum;
		Log.d(TAG, "Application ID#" + appID + " , Rating: " + appRating);
		return appRating;
	}
	
	public ArrayList<Rating> getReviewsFromDatabase(int storeID, int appID) {
		ArrayList<Rating> rval = new ArrayList<Rating>();
		DbHelper dbHelper = new DbHelper(getApplicationContext());
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor;
		if(appID < 0)
			cursor = db.query(dbHelper.REVIEW_TABLE, null, null, null, null, null, null);
		else
			cursor = db.query(dbHelper.REVIEW_TABLE, null, dbHelper.APP_ID_FOR_REVIEW + " = " +appID, null, null, null, dbHelper.SUBMITTED_DATE);
		/*
		 * 0: REVIEW_ID
		 * 1: APP_ID_FOR_REVIEW
		 * 2: RATING
		 * 3: USER_ID
		 * 4: COMMENTS
		 * 5: SUBMITTED_DATE
		 * 6: STORE_ID
		 * 7: TITLE
		 */
		Log.d(TAG, "Columns: " + cursor.getColumnCount() + ", Rows: " + cursor.getCount());
		while(cursor.moveToNext()) {
			Rating r = new Rating();
			r.setReview(cursor.getString(4) + "");
			r.setStars(Integer.parseInt(cursor.getString(2) + ""));
			r.setUserID(cursor.getString(3) + "");
			r.setReviewID(Integer.parseInt(cursor.getString(0) + ""));
			r.setAppID(Integer.parseInt(cursor.getString(1) + ""));
			r.setSubmitDate(cursor.getString(5) + "");
			r.setTitle(cursor.getString(7) + "");
			Log.i(TAG, "Review added("+r.getStars()+"): " + r.getReview() + "with stars = " + r.getStars());
			rval.add(r);
		}
		db.close();
		return rval;
	}
	
	public void downloadApp(int appID) {
		Application app = getSelectedAppInfo(appID);
		String uriString = "http://appstore.collinforrester.com/stores/" + this.user.getStore().getUrl() +
			"/" + app.getFilename();
		Log.d(TAG, "Downloading " + app.getTitle() + " from " + uriString);
		Intent promptInstall = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
		
		promptInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(promptInstall);
	}
	
	public ArrayList<Integer> getInstalledUserApps() {
		ArrayList<Integer> rval = new ArrayList<Integer>();
		DbHelper dbHelper = new DbHelper(this);
		Cursor cursor = dbHelper.getDownloadedUserAppsFromDB();
		Log.i(TAG, "getInstalledUserApps");
		while(cursor.moveToNext()) {
			rval.add(cursor.getInt(cursor.getColumnIndex(dbHelper.MA_APP_ID)));
			Log.i(TAG, "User has " + cursor.getInt(cursor.getColumnIndex(dbHelper.MA_APP_ID)) + 
					" installed.");
		}
		return rval;
	}
	
	public Application getSelectedAppInfo(int appID) {
		Application currentApp = null;
		DbHelper db = new DbHelper(this);
		Cursor cursor = db.getApplicationInformationFromDb(appID);
		if(cursor.moveToFirst()) {
			currentApp = new Application();
			String tags = cursor.getString(1) + "";
			String category = cursor.getString(2) + "";
			String storeName = cursor.getString(6) + "";
			String title = cursor.getString(7) + "";
			String desc = cursor.getString(8) + "";
			int maj = cursor.getInt(9);
			int min = cursor.getInt(10);
			String author = cursor.getString(11);
			String fn = cursor.getString(cursor.getColumnIndex(db.FILENAME)) + "";
			String packageName = cursor.getString(cursor.getColumnIndex(db.PACKAGE_NAME)) + "";
			currentApp.setAppID(appID);
			currentApp.setTags(tags);
			currentApp.setCategory(category);
			currentApp.setStoreID(storeName);
			currentApp.setTitle(title);
			currentApp.setDescription(desc);
			currentApp.setVisible(true);
			currentApp.setMajor(maj);
			currentApp.setMinor(min);
			currentApp.setAuthor(author);
			currentApp.setFilename(fn);
			currentApp.setPackageName(packageName);
			Log.d(TAG, desc);
		}
		return currentApp;
	}
	
	public int getCurrentPackageVersion(int appID) {
		int rval = -1;
		
		Application app = getSelectedAppInfo(appID);
		if(app != null) {
			Log.i(TAG, "Checking to see if package " + app.getPackageName() + " is installed");
			if(this.packageNames == null || this.packageNames.size() != getInstalledPackages().size()) {
				this.packageNames = new ArrayList<String>();
				if(this.packages == null)
					this.packages = getInstalledPackages();
				for(int i = 0; i < this.packages.size(); i++) {
					this.packageNames.add(i, this.packages.get(i).packageName);
					if(this.packages.get(i).packageName == app.getPackageName()) {
						//^^ == local ------------------^^ == what's available
						rval = this.packages.get(i).versionCode;
						Log.d(TAG, "packages(" + i + "): " + this.packages.get(i).packageName + ", current app package" +
								": " + app.getPackageName());
					}
				}
			} else {
				if(this.packageNames.contains(app.getPackageName())) {
					rval = this.packages.get(packageNames.indexOf(app.getPackageName())).versionCode;
				}
			}
			for(int i = 0; i < packageNames.size(); i++) {
				if(packageNames.contains("creature")) {
					Log.d(TAG, packageNames.get(i));
				}
			}
		}
		if(rval < 0 && app != null)
			Log.e(TAG, app.getPackageName() + " not found!");
		return rval;
	}
	public List<PackageInfo> getInstalledPackages() {
		List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
		return packs;
	}

}
