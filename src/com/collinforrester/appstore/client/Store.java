package com.collinforrester.appstore.client;

public class Store {
	
	private String owner;
	private String url;
	private String email;
	private String title;
	private int ID;
	
	public Store() {
		
	}
	
	public void setOwner(String o) {
		this.owner = o;
	}
	public void setUrl(String u) {
		this.url = u;
	}
	public void setEmail(String e) {
		this.email = e;
	}
	public void setTitle(String t) {
		this.title = t;
	}
	public void setID(int id) {
		this.ID = id;
	}
	
	public int getID() {
		return this.ID;
	}
	public String getOwner() {
		return this.owner;
	}
	public String getUrl() {
		return this.url;
	}
	public String getEmail() {
		return this.email;
	}
	public String getTitle() {
		return this.title;
	}
}
