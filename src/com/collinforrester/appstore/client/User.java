package com.collinforrester.appstore.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class User implements Runnable {
	
	private String username;
	private String password;
	private String storeCode;
	private String IMEI;
	private String email;
	private Boolean isConnectedToStore = false;
	private Store store;
	
	public User() {
		this.isConnectedToStore = false;
	}
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Username: " + this.username + ", ");
		builder.append("Password: " + this.password + ", ");
		builder.append("storecode: " + this.storeCode + ", ");
		builder.append("IMEI: " + this.IMEI + ", ");
		builder.append("email: " + this.email);
		return builder.toString();
	}
	public void setUsername(String u) {
		this.username = u;
	}
	public void setPassword(String p) {
		this.password = p;
	}
	public void connectToStore(boolean b) {
		this.isConnectedToStore = b;
	}
	public void setStoreCode(String s) {
		this.storeCode = s;
	}
	public void setIMEI(String i) {
		this.IMEI = i;
	}
	public void setEmail(String e) {
		this.email = e;
	}
	public void setStore(Store s) {
		this.store = s;
	}
	
	public String getUsername() {
		return this.username;
	}
	public String getPassword() {
		return this.password;
	}
	public String getStoreCode() {
		return this.storeCode;
	}
	public String getIMEI() {
		return this.IMEI;
	}
	public String getEmail() {
		return this.email;
	}
	
	public Store getStore() {
		return this.store;
	}
	
	public Boolean isUserConnected() {
		return this.isConnectedToStore;
	}
	
	public User getLocalUserDataFromPhone(User user, Context context) {
		Account[] accounts = AccountManager.get(context).getAccounts();
		for (Account account : accounts) {
		  // TODO: Check possibleEmail against an email regex or treat
		  // account.name as an email address only for certain account.type values.
			String possibleEmail = account.name;
			
		}
		String possibleEmail = accounts[0].name;
		user.email = possibleEmail;
		return user;
	}
	@Override
	public void run() {
		
	}
}
