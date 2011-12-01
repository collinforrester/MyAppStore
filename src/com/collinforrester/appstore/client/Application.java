package com.collinforrester.appstore.client;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Application {
	private int appID;
	private String storeName;
	private Date submittedDate;
	private String category;
	private Boolean visible;
	private Date lastUpdated;
	private String url;
	private String tags;
	private String title;
	private String description;
	private int major;
	private int minor;
	private String author;
	private String filename;
	private String packageName;
	
	public Application(int appID, String storeName, String submit, String cat,
			String vis, String last, String url, String tags, String title, String desc) {
		DateFormat formatter;
		Date date;
		formatter = new SimpleDateFormat("dd-mm-yy");
		try {
			date = (Date)formatter.parse(submit);
			this.submittedDate = date;
			date = (Date)formatter.parse(last);
			this.lastUpdated = date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(vis.equals("Y"))
			this.visible=true;
		else
			this.visible=false;
		this.appID = appID;
		this.storeName = storeName;
		this.category = cat;
		this.url = url;
		this.tags = tags;
		this.title = title;
		this.description = desc;
	}
	public Application() {
		
	}

	//gets
	public String getPackageName() {
		return this.packageName;
	}
	public int getAppID() {
		return this.appID;
	}
	public String getStoreName() {
		return this.storeName;
	}
	public Date getSubmittedDate() {
		return this.submittedDate;
	}
	public String getCategory() {
		return this.category;
	}
	public Boolean getVisible() {
		return this.visible;
	}
	public Date getLastUpdated() {
		return this.lastUpdated;
	}
	public String getUrl() {
		return this.url;
	}
	public String getTags() {
		return this.tags;
	}
	public String getTitle() {
		return this.title;
	}
	public String getDescription() {
		return this.description;
	}
	public int getMajor() {
		return this.major;
	}
	public int getMinor() {
		return this.minor;
	}
	public String getAuthor() {
		return this.author;
	}
	public String getFilename() {
		return this.filename;
	}
	
	//sets
	public void setPackageName(String p) {
		this.packageName = p;
	}
	public void setFilename(String f) {
		this.filename = f;
	}
	public void setMajor(int i) {
		this.major = i;
	}
	public void setMinor(int i ) {
		this.minor = i;
	}
	public void setAuthor(String a) {
		this.author = a;
	}
	public void setAppID(int appID) {
		this.appID = appID;
	}
	public void setDescription(String desc) {
		this.description = desc;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setStoreID(String storeName) {
		this.storeName = storeName;
	}
	public void setSubmittedDate(Date submit) {
		this.submittedDate = submit;
	}
	public void setCategory(String cat) {
		this.category = cat;
	}
	public void setVisible(Boolean vis) {
		this.visible = vis;
	}
	public void setLastUpdated(Date last) {
		this.lastUpdated = last;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
}
