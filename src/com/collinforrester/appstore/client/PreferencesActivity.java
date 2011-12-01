package com.collinforrester.appstore.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.preferences);
        MainApplication app = (MainApplication)getApplication();
        CheckBoxPreference preference = (CheckBoxPreference)findPreference("chkRunUpdater");
        if(app.isUpdaterRunning()) {
        	preference.setChecked(true);
        } else {
        	preference.setChecked(false);
        }
	}

}
