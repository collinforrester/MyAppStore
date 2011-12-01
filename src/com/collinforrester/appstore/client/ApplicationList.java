package com.collinforrester.appstore.client;

import java.util.ArrayList;

/** Contains getter and setter method for varialbles */
public class ApplicationList {

	/** Variables */
	private ArrayList<String> title = new ArrayList<String>();
	private ArrayList<String> location = new ArrayList<String>();
	private ArrayList<String> description = new ArrayList<String>();
	
	/** In Setter method default it will return arraylist
	* change that to add */
	
	public ArrayList<String> getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title.add(title);
	}
	
	public ArrayList<String> getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location.add(location);
	}
	
	public ArrayList<String> getDescription() {
		return description;
	}
	
	public void setDescription(String desc) {
		this.description.add(desc);
	}

}