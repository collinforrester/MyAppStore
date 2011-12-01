package com.collinforrester.appstore.client;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ApplicationInfoActivity extends Activity {

	private MainApplication app;
	private static final String TAG = ApplicationInfoActivity.class.getSimpleName();
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
	      
	      if(this.selectedApplication == null && appID > 0)
	    	  this.selectedApplication = this.app.getSelectedAppInfo(appID);
	      else
	    	  Toast.makeText(
	    			  getBaseContext(),
	    			  "Unable to retreive application selection",
	    			  Toast.LENGTH_LONG).show();
	    	  
	      setContentView(R.layout.infoscreen);
	      
	      TextView txtTitle = (TextView)findViewById(R.id.txtInfoScreenTitle);
	      TextView txtAuthor = (TextView)findViewById(R.id.txtInfoScreenAuthor);
	      TextView txtDesc = (TextView)findViewById(R.id.txtInfoScreenDescription);
	      TextView txtVersion = (TextView)findViewById(R.id.txtInfoScreenVersion);
	      
	      if(this.selectedApplication != null) {
		      txtTitle.setText(this.selectedApplication.getTitle());
		      txtAuthor.setText("Developed by " + this.selectedApplication.getAuthor());
		      txtDesc.setText(this.selectedApplication.getDescription()); 
		      txtVersion.setText("Version " + this.selectedApplication.getMajor() + 
		    		  "." + this.selectedApplication.getMinor());
	      } else {
	    	  Toast.makeText(this, "Unable to load application information", Toast.LENGTH_LONG).show();
	      }
	}
	
	

}
