package com.collinforrester.appstore.client;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Rating {
	/*
	 * From the database:
	 * 0: REVIEW_ID
	 * 1: APP_ID_FOR_REVIEW
	 * 2: RATING
	 * 3: USER_ID
	 * 4: COMMENTS
	 * 5: SUBMITTED_DATE
	 * 6: STORE_ID
	 * 7: TITLE
	 */
	private String comments = "";
	private int stars;
	private int app_id;
	private String user_id;
	private Date submit;
	private int store_id;
	private int review_id;
	private String title;
	
	public Rating() {
		stars = 0;
	}
	//setters
	public void setReview(String review) {
		this.comments = review;
	}
	public void setTitle(String t) {
		this.title = t;
	}
	public void setStars(int stars) {
		this.stars = stars;
	}
	public void setReviewID(int ID) {
		this.review_id = ID;
	}
	public void setAppID(int ID) {
		this.app_id = ID;
	}
	public void setUserID(String ID) {
		this.user_id = ID;
	}
	public void setSubmitDate(String d) {
		DateFormat formatter;
		Date date;
		formatter = new SimpleDateFormat("dd-mm-yy");
		try {
			date = (Date)formatter.parse(d);
			this.submit = date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//getters
	public String getTitle() {
		return this.title;
	}
	public String getReview() {
		return this.comments;
	}
	public int getStars() {
		return this.stars;
	}
	public int getReviewID() {
		return this.review_id;
	}
	public int getAppID() {
		return this.app_id;
	}
	public String getUserID() {
		return this.user_id;
	}
	public Date getSubmitDate() {
		return this.submit;
	}
}
