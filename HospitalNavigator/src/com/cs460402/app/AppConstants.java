package com.cs460402.app;

import com.cs460402.activities.AppPreferencesActivity;
import com.cs460402.activities.Entry;
import com.cs460402.activities.Home;
import com.cs460402.activities.Instructions;
import com.cs460402.activities.Instructions2;
import com.cs460402.activities.Instructions3;
import com.cs460402.activities.Instructions4;
import com.cs460402.activities.Instructions5;
import com.cs460402.activities.Instructions6;
import com.cs460402.activities.Instructions7;
import com.cs460402.activities.POIprefsActivity;
import com.cs460402.activities.ShareRouteActivity;
import com.cs460402.activities.WebViewActivity;

public final class AppConstants {
	public static final String GLOBAL_IMG_STORAGE_LOC = ""; // some location on android storage
	public static final String GLOBAL_WEB_SERVER_ADDRESS = "wayfinder.mapsdb.com";
	public static final String GLOBAL_WEB_SITE = "http://" + GLOBAL_WEB_SERVER_ADDRESS + "/cs460spr2012/mobile/route.jsp";
	public static final boolean GLOBAL_METHOD_TRACING = false; // not used at this time
	public static final boolean GLOBAL_DEBUGGING = false; // not used at this time

	public static final Class CLASS_HOME = Home.class;
	public static final Class CLASS_WEBVIEW = WebViewActivity.class;
	public static final Class CLASS_PREF = AppPreferencesActivity.class;
	public static final Class CLASS_ENTRY = Entry.class;
	public static final Class CLASS_POIPREF = POIprefsActivity.class;
	public static final Class CLASS_SHARE = ShareRouteActivity.class;
	public static final Class CLASS_INSTRUCTIONS = Instructions.class;
	public static final Class CLASS_INSTRUCTIONS2 = Instructions2.class;
	public static final Class CLASS_INSTRUCTIONS3 = Instructions3.class;
	public static final Class CLASS_INSTRUCTIONS4 = Instructions4.class;
	public static final Class CLASS_INSTRUCTIONS5 = Instructions5.class;
	public static final Class CLASS_INSTRUCTIONS6 = Instructions6.class;
	public static final Class CLASS_INSTRUCTIONS7 = Instructions7.class;
	
	// for fling gestures
	public static final int SWIPE_MIN_DISTANCE = 120;
	public static final int SWIPE_MAX_OFF_PATH = 250;
	public static final int SWIPE_THRESHOLD_VELOCITY = 200;
	
	
}
