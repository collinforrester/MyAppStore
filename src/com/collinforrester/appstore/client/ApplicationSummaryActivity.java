package com.collinforrester.appstore.client;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class ApplicationSummaryActivity extends Activity {
	
	private MainApplication app;
	private static final String TAG = ApplicationSummaryActivity.class.getSimpleName();
	private Application selectedApplication;
	
	@Override
	public void onCreate(Bundle icicle) {
	      super.onCreate(icicle);
	      
	      if(this.app == null)
	    	  this.app = (MainApplication)getApplication();
	      
	      Bundle extras = getIntent().getExtras(); 
	      int appID = -1;
	      if(extras != null) {
	    	  appID = Integer.parseInt(extras.getString("applicationID"));
	      }
	      
	      setContentView(R.layout.summaryscreen);
	      
	      TextView txtTitle = (TextView)findViewById(R.id.txtSummaryScreenTitle);
	      TextView txtAuthor = (TextView)findViewById(R.id.txtSummaryScreenAuthor);
	      TextView txtDesc = (TextView)findViewById(R.id.txtSummaryScreenDescription);
	      TextView txtVersion = (TextView)findViewById(R.id.txtSummaryScreenVersion);
	      TextView txtTags = (TextView)findViewById(R.id.txtSummaryTags);
	      Button btnInstall = (Button)findViewById(R.id.btnInstall);
	      RatingBar ratingBar = (RatingBar)findViewById(R.id.summaryRatingBar);
	      TextView txtNumberOfReviews = (TextView)findViewById(R.id.txtSummaryNumberOfRatings);
	      
	      float rating = -1;
	      if(this.selectedApplication == null && appID > 0) {
	    	  this.selectedApplication = this.app.getSelectedAppInfo(appID);
	    	  rating = this.app.getApplicationRating(this.selectedApplication.getAppID(), 
	    			  this.app.user.getStore().getID());
	    	  
	    	  txtTitle.setText(this.selectedApplication.getTitle());
		      txtAuthor.setText("Developed by " + this.selectedApplication.getAuthor());
		      if(this.selectedApplication.getDescription().length() >= 50) {
		    	  txtDesc.setText(this.selectedApplication.getDescription().substring(0, 50) + "...");
		      } else {
			      txtDesc.setText(this.selectedApplication.getDescription()); 
		      }
		      txtVersion.setText("Version " + this.selectedApplication.getMajor() + 
		    		  "." + this.selectedApplication.getMinor());
		      ratingBar.setRating((rating / 10) * 5);
		      txtNumberOfReviews.setText("(" + this.app.getReviewsFromDatabase(this.app.user.getStore().getID(), appID).size() + 
		    		  ")");
		      txtTags.setText("Tags: " + this.selectedApplication.getTags());
		      
		      final ApplicationScreenTabActivity act = (ApplicationScreenTabActivity) this.getParent();
		      ratingBar.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction() == MotionEvent.ACTION_UP) {
						Log.d(TAG, "onClick");
						act.switchTab(2);
					}
					return true;
				}
			});
		      int localPackageVersion = this.app.getCurrentPackageVersion(appID);
		      if(selectedApplication.getMajor() > localPackageVersion 
		    		  && localPackageVersion != -1) {
		    	  btnInstall.setText("Install upgrade");
		    	  final int aID = appID;
			      btnInstall.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(getApplicationContext(), "Starting upgrade...", Toast.LENGTH_LONG).show();
						downloadApp(aID);
						finish();
					}
				});
		      } else if(localPackageVersion != -1){
		    	  btnInstall.setText("Installed");
		      } else {
			      final int aID = appID;
			      btnInstall.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(getApplicationContext(), "Starting download...", Toast.LENGTH_LONG).show();
						downloadApp(aID);
						finish();
					}
				});
		      }
	      }
	      else
	    	  Toast.makeText(
	    			  getBaseContext(),
	    			  "Unable to load application summary page",
	    			  Toast.LENGTH_LONG).show();
	      
	} 
    public void downloadApp(int appID) {
  	  this.app.downloadApp(appID);
    }

}
