package com.collinforrester.appstore.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainAppViewActivity extends Activity {
	
	private static final int INVISIBLE = 4;
	private static final int VISIBLE = 0;
	/*Site list class object*/
	ApplicationList sitesList = null;
	private static final String TAG = MainAppViewActivity.class.getSimpleName();
	
	private MainApplication app;
	
	SharedPreferences prefs;
	EditText txtSearch;
	String searchTerm;
	private boolean serviceRunning;
	LinearLayout layout;
	LinearLayout appListLayout;
	Button appButton;
	RatingBar rBar;
	User currentUser;
	
	private OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		  public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			  
		  }
		};
	public void connectToStore() {
		
	}
	public void startUpdaterService() {
		startService(new Intent(this, UpdaterService.class));
	}
	public void stopUpdaterService() {
		stopService(new Intent(this, UpdaterService.class));
	}
	public boolean isServiceRunning() {
		return this.serviceRunning;
	}
	
	public void setUpUser() {
		DbHelper dbHelper = new DbHelper(getApplicationContext());
		User tempUser = dbHelper.getUserFromDb();
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		tempUser.setIMEI(tm.getDeviceId());
		tempUser = tempUser.getLocalUserDataFromPhone(tempUser, getApplicationContext());
		System.out.println(tempUser.toString());
		this.currentUser = tempUser;
	}
	
	public void setServiceRunning(boolean serviceRunning) {
		this.serviceRunning = serviceRunning;
	}
	
	
	
	public ArrayList<com.collinforrester.appstore.client.Application> getAppsFromDatabase() {
		ArrayList<com.collinforrester.appstore.client.Application> arrList = new ArrayList<com.collinforrester.appstore.client.Application>();
		/*
		 * k: 0, value: 1
		 * k: 1, value: games, finance
		 * k: 2, value: Productivity
		 * k: 3, value: 2011-09-12
		 * k: 4, value: 2011-09-12
		 * k: 5, value: a
		 * k: 6, value: Test Application Store ID 1
		 * k: 7, value: null
		 * k: 8, value: null
		 * k: 9, version_major
		 * k: 10, version_minor
		 * k: 11, author
		 */
		DbHelper dbHelper = new DbHelper(getApplicationContext());
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(dbHelper.APP_TABLE, null, null, null, null, null, null);
		//cursor.moveToFirst();
		Log.i(TAG, "Cursor # of columns: " + cursor.getColumnCount());
		while(cursor.moveToNext()) {
			com.collinforrester.appstore.client.Application currentApp = new com.collinforrester.appstore.client.Application();
			int appID = Integer.parseInt(cursor.getString(0));
			String tags = cursor.getString(1) + "";
			String category = cursor.getString(2) + "";
			String storeName = cursor.getString(6) + "";
			String title = cursor.getString(7) + "";
			String desc = cursor.getString(8) + "";
			int maj = cursor.getInt(9);
			int min = cursor.getInt(10);
			String author = cursor.getString(11);
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
			//for(int k = 0; k < cursor.getColumnCount(); k++) {
			//	System.out.println("k: " + k + ", value: " + cursor.getString(k));
			//}
			Log.i(TAG, "added application to return list, id: " + appID);
			arrList.add(currentApp);
		}
		db.close();
		return arrList;
	}
	
	public void getAndParseAppList() {
		/*Create a new layout to display the view*/
		layout = (LinearLayout)findViewById(R.id.linLayout);
		layout.setOrientation(1);
		appListLayout = (LinearLayout)findViewById(R.id.appListLinLayout);
		
		/*Create a text view array to display the results*/
		TextView[] title;
		TextView[] description;
		TextView[] location;
		
		Button searchButton = (Button)findViewById(R.id.btnSearch);
		Button clearButton = (Button)findViewById(R.id.btnClear);
		searchButton.setOnClickListener(searchAndClearListener);
		clearButton.setOnClickListener(searchAndClearListener);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
		txtSearch = (EditText)findViewById(R.id.txtSearch);

		txtSearch.setOnKeyListener(searchFieldKeyListener);
		txtSearch.addTextChangedListener(searchListener);
		
		
		searchTerm = "";
		
		prefs.registerOnSharedPreferenceChangeListener(listener);
		String currStoreName = this.app.user.getStore().getTitle();
		
		TextView txtStoreName = (TextView)findViewById(R.id.tvStoreCode);
		txtStoreName.setText("Current store: " + currStoreName);
		
		/*Get the results from MyXMLHandler SiteLIst object*/
		sitesList = MyXMLHandler.appList;
		if(this.app.appList == null || this.app.appList.size() < 0) 
			this.app.appList = getAppsFromDatabase();
			
		/*
		 * Need to get the applist from the database
		 */
		
		/*
		title = new TextView[sitesList.getTitle().size()];
		location = new TextView[sitesList.getTitle().size()];
		description = new TextView[sitesList.getTitle().size()];
		*/
		title = new TextView[this.app.appList.size()];
		location = new TextView[this.app.appList.size()];
		description = new TextView[this.app.appList.size()];
		/*set the result text in textview and add it to layout*/
		Log.d(TAG, "this.app.appList.size() == " + this.app.appList.size());
		for(int i = 0; i < this.app.appList.size(); i++) {
			if(this.app.appList.get(i).getVisible()) {
				title[i] = new TextView(this);
				title[i].setText("Title = "+ this.app.appList.get(i).getTitle());
				location[i] = new TextView(this);
				description[i] = new TextView(this);
				description[i].setText("Description = "+this.app.appList.get(i).getDescription());
				appButton = new Button(this);
				String desc = this.app.appList.get(i).getDescription();
				if(desc.length() > 25)
					desc = desc.substring(0, 25) + "...";
				appButton.setText(this.app.appList.get(i).getTitle() + "\n" + desc);
				appButton.setOnClickListener(appButtonListener);
				appButton.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.appiconlayout, null);
				vg.setOnClickListener(appButtonListener);
				
				/*
				 * ====This adds the attributes to each view in a lightweight manner
				 * ====makes them available to the next activity that shows the details about that 
				 * ====particular application
				 */
				vg.setTag(R.string.title, this.app.appList.get(i).getTitle());
				vg.setTag(R.string.description, this.app.appList.get(i).getDescription());
				vg.setTag(R.string.applicationID, this.app.appList.get(i).getAppID() + ""); //autocast to String for now
				//================================================================
				
				//TextView bottomLine = (TextView)vg.findViewById(R.id.secondLine);
				rBar = (RatingBar)vg.findViewById(R.id.ratingbar_Small);
				
				rBar.setRating(this.app.getApplicationRating(this.app.appList.get(i).getAppID(), this.app.user.getStore().getID()));
				TextView topLine = (TextView)vg.findViewById(R.id.topLine);
				//bottomLine.setText(desc);
				topLine.setText(this.app.appList.get(i).getTitle());
				appListLayout.addView(vg);
			}
			
		}
	}
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (MainApplication) getApplication();
		if(this.prefs == null)
			prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Log.i(TAG, "onCreate");
		
		
		//onResume will be called next ---------=========-----------
		
		
		/*setUpUser();
		if(!this.currentUser.isUserConnected()) {
			currentUser.setStoreCode("abc123");
			if(!currentUser.getStoreCode().equals("")) {
				
			} else {
				startService(new Intent(this, UpdaterService.class));
				setContentView(R.layout.main);
				Log.e(DEBUG, "onCreate");
				setUpUser();
				getAndParseAppList();
			}
		} else {
			
		}
		
		Log.e(DEBUG, "cursor set.");
		while (cursor.moveToNext()) {
			appTitle = cursor.getString(6);//.getColumnIndex(DbHelper.TITLE));
			desc = cursor.getString(7);//cursor.getColumnIndex(DbHelper.DESCRIPTION));
			System.out.println("appTitle: " + appTitle + ", desc: " + desc);
			}*/
		//setContentView(layout);
	}
	
	protected void onActivityResult(int requestCode, int result, Intent data) {
		
	}
	
	@Override
	protected void onResume() {
		app = (MainApplication) getApplication();
		if(this.prefs == null)
			prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		Log.i(TAG, "onResume");
		
		if(!prefs.getString("storeCode", "").equals("") && this.app.user.isUserConnected()) {
			
			Log.i(TAG, "this.currentUser.isConnected = " + this.app.user.isUserConnected().toString());
			//load apps into view
			setContentView(R.layout.main);
			
			getAndParseAppList();
			setContentView(layout);
			
			//setContentView(R.layout.main);
		} else if (!this.app.user.isUserConnected()) {
			startActivity(new Intent(getApplicationContext(), DefaultScreenActivity.class));
			finish(); //finish() to end this activity, it will be launched later, correctly.
		}
		
		super.onResume();
	}
	
	private OnClickListener appButtonListener = new OnClickListener() {
	    public void onClick(View v) {
	      // do something when the button is clicked	    
	    	Intent i = new Intent(getApplicationContext(), ApplicationScreenTabActivity.class);
	    	i.putExtra("title", (String)v.getTag(R.string.title));
	    	i.putExtra("description", (String)v.getTag(R.string.description));
	    	i.putExtra("applicationID", (String)v.getTag(R.string.applicationID));
	    	startActivity(i);
	    }
	};
	
	private TextWatcher searchListener = new TextWatcher() { 
        public void afterTextChanged(Editable s) { 
            //XXX
        } 
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { 
                //XXX do something 
        } 
        public void onTextChanged(CharSequence s, int start, int before, int count) { 
        	searchTerm = txtSearch.getText().toString();//updateSearchTerm
        	updateSearchResults();
        } 
	}; 
	
	private OnClickListener searchAndClearListener = new OnClickListener() {
	    public void onClick(View v) {
	      // do something when the button is clicked	    
	    	switch (v.getId()) {
	    	case R.id.btnClear:
	    		txtSearch.setText(""); //clear
	    	case R.id.btnSearch:
	    		searchTerm = txtSearch.getText().toString();//updateSearchTerm
	    		updateSearchResults();
	    	}
	    }
	};
	
	private OnKeyListener searchFieldKeyListener = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.ACTION_UP) {
				System.out.println("hit");
				updateSearchResults();
				return true;
			}
			else {
				return false;
			}
			
		}
	};
	
	private void updateSearchResults() {
		LinearLayout appListLayout = (LinearLayout)findViewById(R.id.appListLinLayout);
		int count = appListLayout.getChildCount();
		for(int i = 0; i < count; i++) {
			ViewGroup currentItem = (ViewGroup)appListLayout.getChildAt(i);
			//TextView bottomLine = (TextView)currentItem.findViewById(R.id.secondLine);
			TextView topLine = (TextView)currentItem.findViewById(R.id.topLine);
			System.out.println(currentItem.getTag(R.string.description).toString() + ", indexOf is " + currentItem.getTag(R.string.description).toString().indexOf(searchTerm));
			System.out.println(topLine.getText().toString() + ", indexOf is " + topLine.getText().toString().indexOf(searchTerm));
			System.out.println("search term = " + searchTerm);
			if((currentItem.getTag(R.string.description).toString().indexOf(searchTerm) < 0) && (topLine.getText().toString().indexOf(searchTerm) < 0)) {
				currentItem.setVisibility(8);
			}
			else { 
				currentItem.setVisibility(0);
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		if(item.getItemId() == R.id.options) {
	        startActivity(new Intent(this, PreferencesActivity.class));
	        return true;
		} else if (item.getItemId() == R.id.enableService) {
			startService(new Intent(this, UpdaterService.class));
	    	return true;
		} else if (item.getItemId() == R.id.disableService) {
			stopService(new Intent(this, UpdaterService.class));
	    	return true;
		} else if(item.getItemId() == R.id.refresh) {
			Intent i = getIntent();
			i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			finish();
			startActivity(i);
			return true;
		}
    	else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	

}
