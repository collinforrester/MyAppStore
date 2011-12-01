package com.collinforrester.appstore.client;

import java.util.ArrayList;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class ApplicationScreenTabActivity extends TabActivity {
	
	private MainApplication app;
	private static final String TAG = ApplicationScreenTabActivity.class.getSimpleName();
	
	public void onCreate(Bundle icicle) {
	      super.onCreate(icicle);
	      
	      setContentView(R.layout.applicationscreen);
	      
	      /*
	       * Extras passed to this activity via intent to detect which view was pressed
	       */
	      Bundle extras = getIntent().getExtras(); 
	      String applicationID = null;
	      String title = null;
	      String description = null;
	      if(extras !=null) {
	          title = extras.getString("title");
	          description = extras.getString("description");
		      applicationID = extras.getString("applicationID");
		      Log.d(TAG, "you selected application " + applicationID);
	      }
	      
	      Resources res = getResources(); //get resources for Drawables
	      TabHost tabHost = getTabHost(); //the activity TabHost
	      TabHost.TabSpec spec; //a reusable tabspec for each tab
	      Intent intent; //reusable intent for each tab
	      
	      //Create intent to launch activity
	      intent = new Intent().setClass(this, ApplicationSummaryActivity.class);
	      intent.putExtra("title", title);
	      intent.putExtra("description", description);
	      intent.putExtra("applicationID", applicationID);
	     
	      //Init tabspec for each tab and add it to tab host
	      spec = tabHost.newTabSpec("summary").setIndicator("Summary",
	    		  res.getDrawable(R.drawable.icon))
	    		  .setContent(intent);
	      tabHost.addTab(spec);
	      
	      //Doing the same for the other two tabs (Info & Reviews)
	      intent = new Intent().setClass(this, ApplicationInfoActivity.class);
	      intent.putExtra("title", title);
	      intent.putExtra("description", description);
	      intent.putExtra("applicationID", applicationID);
	      spec = tabHost.newTabSpec("info").setIndicator("Info",
	    		  res.getDrawable(R.drawable.icon))
	    		  .setContent(intent);
	      tabHost.addTab(spec);
	      
	      intent = new Intent().setClass(this, ApplicationReviewActivity.class);
	      intent.putExtra("title", title);
	      intent.putExtra("description", description);
	      intent.putExtra("applicationID", applicationID);
	      spec = tabHost.newTabSpec("reviews").setIndicator("Reviews",
	    		  res.getDrawable(R.drawable.ic_tab_reviews))
	    		  .setContent(intent);
	      tabHost.addTab(spec);
	      tabHost.setCurrentTab(0);
	   }
	public void switchTab(int tab) {
		getTabHost().setCurrentTab(tab);
	}
	
}
