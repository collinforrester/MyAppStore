package com.collinforrester.appstore.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DefaultScreenActivity extends Activity {
	
	public Context context = null;
	private static final String TAG = DefaultScreenActivity.class.getSimpleName();
	private MainApplication app;
	private Context thisContext = this;
	
	public void saveStoreInfoInPrefs(String title, String owner, String email, String url) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Editor e = prefs.edit();
		
		e.putString("storeTitle", title);
		e.putString("storeOwner", owner);
		e.putString("storeEmail", email);
		e.putString("storeURL", url);
		
		e.commit();
	}
	
	public void getStoreInfo() {
		// TODO Auto-generated method stub
		Log.i(TAG, "getStoreInfo");
		//Contact webservice, providing storecode, and password (will be ignored if not needed)
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		try {
			
			String storeCode = prefs.getString("storeCode", "").replace(" ", "");
			String password = prefs.getString("password", "");
			
			String _URL = "http://appstore.collinforrester.com/webservice.php?f=stores&storeCode="+
			storeCode + "&password=" + password;
			Log.d(TAG, "URL: " + _URL);
			URL webservice = new URL(_URL);
			URLConnection webstore = webservice.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					webstore.getInputStream()));
			String line = in.readLine();
			//Log.e(TAG, "line: " + line);
			if(line != null) {
				JSONObject jObj = new JSONObject(line);
			
				JSONArray appsArr = jObj.getJSONArray("results");
				//Log.e(TAG, "appList count (start):" + appList.size());
				ContentValues values = new ContentValues();
				for(int x = 0; x < appsArr.length(); x++) {
					JSONObject appObj = appsArr.getJSONObject(x);
					values.clear();
					//Log.e(TAG, "appObj: " + appObj.toString());
					
					JSONObject jo = appObj.getJSONObject("result");
					//Log.e(TAG, "jo: " + jo.toString());
					/**
					 * Expected Result:
					 * {"results":[{"result":{"STORE_ID":"1",
					 * "STORE_CODE":"abc123",
					 * "STORE_NAME":"DevGuy's App Store",
					 * "STORE_OWNER":"Test App Store Owner 1",
					 * "STORE_EMAIL":"collin.forrester@gmail.com",
					 * "PASSWORD_REQUIRED":"N",
					 * "PASSWORD":null,
					 * "STORE_URL":"b"}}]}			
					 */
					String storeName = jo.getString("STORE_NAME");
					String storeOwner = jo.getString("STORE_OWNER");
					String storeEmail = jo.getString("STORE_EMAIL");
					String storeURL = jo.getString("STORE_URL");
					int ID = jo.getInt("STORE_ID");
					
					saveStoreInfoInPrefs(storeName, storeOwner, storeEmail, storeURL);
					Log.d(TAG, "storeName = " + storeName);
					Log.d(TAG, "storeOwner = " + storeOwner);
					Log.d(TAG, "storeEmail = " + storeEmail);
					Log.d(TAG, "storeURL = " + storeURL);
					this.app.user.setStoreCode(storeCode);
					this.app.user.connectToStore(true);
					Store s = new Store();
					s.setID(ID);
					s.setTitle(storeName);
					s.setOwner(storeOwner);
					s.setEmail(storeEmail);
					s.setUrl(storeURL);
					this.app.user.setStore(s);
				}
			} else {
				this.app.user.connectToStore(false);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
			this.app.user.connectToStore(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
			this.app.user.connectToStore(false);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
			this.app.user.connectToStore(false);
		}
		//Now, start the other activity or error out.
		Log.d(TAG, "User is connected (" + this.app.user.isUserConnected() + ")");
		if(this.app.user.isUserConnected()) {
			if(!this.app.isUpdaterRunning()) {
				startService(new Intent(this, UpdaterService.class));
			}
			startActivity(new Intent(this, MainAppViewActivity.class));
			finish();
		}
		
	}
	
	private void showMissingPrefDialog() {
		Log.i(TAG, "showMissingPrefDialog");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("No store code entered! ")
		       .setCancelable(false)
		       .setPositiveButton("Go to preferences", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   startActivity(new Intent(getApplicationContext(), PreferencesActivity.class));
		           }
		       })
		       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	public void showErrorToPrefsDialog(String msg) {
		Log.i(TAG, "showErrorToPrefsDialog");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Uh oh! " + msg)
		       .setCancelable(false)
		       .setPositiveButton("Go to preferences", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   startActivity(new Intent(getApplicationContext(), PreferencesActivity.class));
		           }
		       })
		       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	public Boolean userIsConnected() {
		return this.app.user.isUserConnected();
	}
	public void showConnectingDialog() {
		final ProgressDialog dialog = ProgressDialog.show(this, 
				"Connecting...", 
				"Please wait. Connecting to your store..", true);
				final Handler handler = new Handler() {
				   public void handleMessage(Message msg) {
					   	  dialog.dismiss();
					      if(!userIsConnected())
						  {
								showErrorToPrefsDialog("We're unable to connect you.  Double check your store code and password (if needed)");
						  }
				      }
				   };
				Thread checkUpdate = new Thread() {  
				   public void run() {
					   //get storeinformation..
					   Log.i(TAG, "run");
					   getStoreInfo();
					   handler.sendEmptyMessage(0);
				      }
				   };
				checkUpdate.start();
	}
	
	public void onCreate(Bundle icicle) {
	      super.onCreate(icicle);
	      app = (MainApplication) getApplication();
		  //get prefs object.. 
		  final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	      
	      if(this.app.user.isUserConnected()) {
	    	  showConnectingDialog();
			}
	      
	      setContentView(R.layout.defaultscreen);
		  Button connect = (Button)findViewById(R.id.btnConnect);
			
			connect.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//Connect to store magic
					Log.i(TAG, "connect onClick");
					if(TextUtils.isEmpty(prefs.getString("storeCode", ""))) {
						showMissingPrefDialog();
					} else {
						showConnectingDialog();
					}
				}
			});
	      
	      TextView tv = (TextView)findViewById(R.id.txtDefaultMessage);
	      tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onDestroy();
			}
		});
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
		} 
    	else {
			return super.onOptionsItemSelected(item);
		}
	}
}
