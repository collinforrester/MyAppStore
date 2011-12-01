package com.collinforrester.appstore.client;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class MyXMLHandler extends DefaultHandler {

	Boolean currentElement = false;
	String currentValue = null;
	public static ApplicationList appList = null;

	public static ApplicationList getSitesList() {
		return appList;
	}
	
	public static void setSitesList(ApplicationList sitesList) {
		MyXMLHandler.appList = sitesList;
	}
	
	/** Called when tag starts ( ex:- <name>AndroidPeople</name>
	* -- <name> )*/
	@Override
	public void startElement(String uri, String localName, String qName,
	Attributes attributes) throws SAXException {
	
		currentElement = true;
		
		if (localName.toUpperCase().equals("APPLICATIONS"))
		{
			/** Start */
			appList = new ApplicationList();
		} /*else if (localName.equals("website")) {
			/** Get attribute value */
			/*String attr = attributes.getValue("category");
			appList.setDescription(attr);
		}*/
	
	}
	
	/** Called when tag closing ( ex:- <name>AndroidPeople</name>
	* -- </name> )*/
	@Override
	public void endElement(String uri, String localName, String qName)
	throws SAXException {
	
		currentElement = false;
		
		/** set value */
		if (localName.equalsIgnoreCase("title"))
			appList.setTitle(currentValue);
		else if (localName.equalsIgnoreCase("location"))
			appList.setLocation(currentValue);
		else if (localName.equalsIgnoreCase("description"))
			appList.setDescription(currentValue);
	
	}
	
	/** Called to get tag characters ( ex:- <name>AndroidPeople</name>
	* -- to get AndroidPeople Character ) */
	@Override
	public void characters(char[] ch, int start, int length)
	throws SAXException {
	
		if (currentElement) {
			currentValue = new String(ch, start, length);
			currentElement = false;
		}
	
	}

}