package com.collinforrester.appstore.client;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ApplicationReviewActivity extends Activity {
	
	private MainApplication app;
	private static final String TAG = ApplicationSummaryActivity.class.getSimpleName();
	
	
	@Override
	public void onCreate(Bundle icicle) {
	      super.onCreate(icicle);
	      
	      this.app = (MainApplication)getApplication();
	      
	      LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	      ScrollView layout = (ScrollView)inflater.inflate(R.layout.reviewlistparent, null);
	      LinearLayout parentReviewLayout = (LinearLayout)layout.findViewById(R.id.linLayoutReviewParent);
	      
	      Bundle extras = getIntent().getExtras(); 
	      int applicationID = -1;
	      if(extras !=null) {
		      applicationID = Integer.parseInt(extras.getString("applicationID"));
		      Log.d(TAG, "you selected application " + applicationID);
	      }
	      if(applicationID > 0) {
	    	  ArrayList<Rating> listOfReview = this.app.getReviewsFromDatabase(this.app.user.getStore().getID(), applicationID);
	    	  parseReviewsAndAddToLayout(listOfReview, parentReviewLayout);
	      } else {
	    	  Log.e(TAG, "Application reviews did not load correctly");
	    	  Toast.makeText(this, "Unable to load reviews", Toast.LENGTH_LONG).show();
	      }
	      setContentView(layout);
	}
	private void parseReviewsAndAddToLayout(ArrayList<Rating> ratings, LinearLayout layout) {
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for(int i = 0; i < ratings.size(); i++) {
			ViewGroup container = (ViewGroup)inflater.inflate(R.layout.reviewlistchild, null);
			RatingBar ratingBar = (RatingBar)container.findViewById(R.id.ratingBar);
			TextView txtSummary = (TextView)container.findViewById(R.id.txtSummary);
			TextView txtAuthor = (TextView)container.findViewById(R.id.txtUsername);
			TextView txtDetails = (TextView)container.findViewById(R.id.txtDetails);
			
			String user = ratings.get(i).getUserID();
			if(TextUtils.equals(user, "") || user == null || user == "null") {
				user = "unknown";
			}
			
			ratingBar.setRating(ratings.get(i).getStars());
			txtSummary.setText(ratings.get(i).getTitle());
			txtAuthor.setText("by " + user);
			txtDetails.setText(ratings.get(i).getReview());
			layout.addView(container);
		}
	}

}
