package com.cs460402.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class AppPrefs {
	public static final String PREFERENCE_MAP_MODE = "mode";

	// preferences for map route
	public static final String PREFERENCE_START_ID = "startid";
	public static final String PREFERENCE_START_LABEL = "startlabel";
	public static final String PREFERENCE_END_ID = "endid";
	public static final String PREFERENCE_END_LABEL = "endlabel";
	public static final String PREFERENCE_VERBOSE = "verbose";
	public static final String PREFERENCE_STAIRS = "stairs";
	public static final String PREFERENCE_INCLUDE_HALLWAYS = "hallways";
	public static final String PREFERENCE_DOT_COLOR = "dot";
	public static final String PREFERENCE_LINE_COLOR = "line";
	public static final String PREFERENCE_LINE_WIDTH = "linewidth";
	public static final String PREFERENCE_BROWSE_BLDG = "browsebldg";

	public static final String PREFERENCE_POI_DISPLAY_HANDICAP = "disp_handi";
	public static final String PREFERENCE_POI_DISPLAY_ELEVATOR = "disp_elev";
	public static final String PREFERENCE_POI_DISPLAY_EXIT = "disp_exit";
	public static final String PREFERENCE_POI_DISPLAY_INFO = "disp_info";
	public static final String PREFERENCE_POI_DISPLAY_STAIRS = "disp_stair";
	public static final String PREFERENCE_POI_DISPLAY_RESTROOM = "disp_rest";
	public static final String PREFERENCE_POI_DISPLAY_RECEPTION = "disp_recep";
	public static final String PREFERENCE_POI_DISPLAY_CAFE = "disp_cafe";
	
	public static final String PREFERENCE_POI_COLOR_HANDICAP = "color_handi";
	public static final String PREFERENCE_POI_COLOR_ELEVATOR = "color_elev";
	public static final String PREFERENCE_POI_COLOR_EXIT = "color_exit";
	public static final String PREFERENCE_POI_COLOR_INFO = "color_info";
	public static final String PREFERENCE_POI_COLOR_STAIRS = "color_stair";
	public static final String PREFERENCE_POI_COLOR_RESTROOM = "color_rest";
	public static final String PREFERENCE_POI_COLOR_RECEPTION = "color_recep";
	public static final String PREFERENCE_POI_COLOR_CAFE = "color_cafe";

	// preferences for map browsing
	public static final String PREFERENCE_BUILDING_ID = "building";
	public static final String PREFERENCE_FLOOR_ID = "floor";
	
	// default phone number/email used when calling/sms/email
	public static final String PREFERENCE_DEFAULT_PHONE = "phone";
	public static final String DEFAULT_PHONE_NUMBER = "8005551234";
	public static final String PREFERENCE_DEFAULT_EMAIL = "email";
	public static final String DEFAULT_EMAIL_ADDRESS = "krichtech123@gmail.com";
	
	public static final String PREFERENCE_CLEAR_DATA = "clearlocdata";
	public static final String PREFERENCE_SYNC_ON_STARTUP = "startupsync";
		
	private static SharedPreferences prefs;
	private static SharedPreferences.Editor editor;

	private AppPrefs() {
	}

	public static String getMapMode(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_MAP_MODE, "browse");
	}

	public static void setMapMode(String mode, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_MAP_MODE, mode);
		editor.commit();
	}

	public static String getStartID(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_START_ID, "");
	}

	public static void setStartID(String id, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_START_ID, id);
		editor.commit();
	}

	public static String getStartLabel(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_START_LABEL, "");
	}

	public static void setStartLabel(String label, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_START_LABEL, label);
		editor.commit();
	}

	public static String getEndID(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_END_ID, "");
	}

	public static void setEndID(String id, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_END_ID, id);
		editor.commit();
	}

	public static String getEndLabel(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_END_LABEL, "");
	}

	public static void setEndLabel(String label, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_END_LABEL, label);
		editor.commit();
	}

	public static boolean getVerbose(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFERENCE_VERBOSE, false);
	}

	public static void setVerbose(boolean verbose, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_VERBOSE, verbose);
		editor.commit();
	}

	public static String getStairs(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_STAIRS, "elevator");
	}

	public static void setStairs(String stairs, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_STAIRS, stairs);
		editor.commit();
	}

	public static boolean getIncludeHallways(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFERENCE_INCLUDE_HALLWAYS, false);
	}

	public static void setIncludeHallways(boolean halls, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_INCLUDE_HALLWAYS, halls);
		editor.commit();
	}

	public static String getDotColor(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_DOT_COLOR, "greenarrowdot.png");
	}

	public static void setDotColor(String dotcolor, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_DOT_COLOR, dotcolor);
		editor.commit();
	}

	public static String getLineColor(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_LINE_COLOR, "blue");
	}

	public static void setLineColor(String linecolor, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_LINE_COLOR, linecolor);
		editor.commit();
	}

	public static int getLineWidth(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return Integer.parseInt(prefs.getString(PREFERENCE_LINE_WIDTH, "3"));
	}

	public static void setLineWidth(int width, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_LINE_WIDTH, Integer.toString(width));
		editor.commit();
	}

	public static String getBuildingId(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_BUILDING_ID, "wang");
	}

	public static void setBuildingId(String bldg, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_BUILDING_ID, bldg);
		editor.commit();
	}

	public static String getFloorId(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_FLOOR_ID, "");
	}

	public static void setFloorId(String floor, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_FLOOR_ID, floor);
		editor.commit();
	}

	public static String getBrowseBldg(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_BROWSE_BLDG, "wang");
	}

	public static void setBrowseBldg(String bldgID, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_BROWSE_BLDG, bldgID);
		editor.commit();
	}

	public static String getPOIdisplay_handicap(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFERENCE_POI_DISPLAY_HANDICAP, false) ? "inline-block"
				: "none";
	}

	public static void setPOIdisplay_handicap(boolean bdisplay, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_HANDICAP, bdisplay);
		editor.commit();
	}

	public static void setPOIdisplay_handicap(String display, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_HANDICAP,
				display.equals("inline-block"));
		editor.commit();
	}

	public static String getPOIdisplay_elevator(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFERENCE_POI_DISPLAY_ELEVATOR, true) ? "inline-block"
				: "none";
	}

	public static void setPOIdisplay_elevator(boolean bdisplay, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_ELEVATOR, bdisplay);
		editor.commit();
	}

	public static void setPOIdisplay_elevator(String display, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_ELEVATOR,
				display.equals("inline-block"));
		editor.commit();
	}

	public static String getPOIdisplay_exit(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFERENCE_POI_DISPLAY_EXIT, true) ? "inline-block"
				: "none";
	}

	public static void setPOIdisplay_exit(boolean bdisplay, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_EXIT, bdisplay);
		editor.commit();
	}

	public static void setPOIdisplay_exit(String display, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_EXIT,
				display.equals("inline-block"));
		editor.commit();
	}

	public static String getPOIdisplay_info(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFERENCE_POI_DISPLAY_INFO, false) ? "inline-block"
				: "none";
	}

	public static void setPOIdisplay_info(boolean bdisplay, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_INFO, bdisplay);
		editor.commit();
	}

	public static void setPOIdisplay_info(String display, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_INFO,
				display.equals("inline-block"));
		editor.commit();
	}

	public static String getPOIdisplay_stairs(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFERENCE_POI_DISPLAY_STAIRS, true) ? "inline-block"
				: "none";
	}

	public static void setPOIdisplay_stairs(boolean bdisplay, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_STAIRS, bdisplay);
		editor.commit();
	}

	public static void setPOIdisplay_stairs(String display, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_STAIRS,
				display.equals("inline-block"));
		editor.commit();
	}

	public static String getPOIdisplay_restroom(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFERENCE_POI_DISPLAY_RESTROOM, true) ? "inline-block"
				: "none";
	}

	public static void setPOIdisplay_restroom(boolean bdisplay, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_RESTROOM, bdisplay);
		editor.commit();
	}

	public static void setPOIdisplay_restroom(String display, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_RESTROOM,
				display.equals("inline-block"));
		editor.commit();
	}

	public static String getPOIdisplay_reception(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFERENCE_POI_DISPLAY_RECEPTION, false) ? "inline-block"
				: "none";
	}

	public static void setPOIdisplay_reception(boolean bdisplay, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_RECEPTION, bdisplay);
		editor.commit();
	}

	public static void setPOIdisplay_reception(String display, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_RECEPTION,
				display.equals("inline-block"));
		editor.commit();
	}
	
	public static String getPOIdisplay_cafe(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFERENCE_POI_DISPLAY_CAFE, false) ? "inline-block"
				: "none";
	}

	public static void setPOIdisplay_cafe(boolean bdisplay, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_CAFE, bdisplay);
		editor.commit();
	}

	public static void setPOIdisplay_cafe(String cafe, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_POI_DISPLAY_CAFE,
				cafe.equals("inline-block"));
		editor.commit();
	}
	
	public static String getPOIcolor_handicap(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_POI_COLOR_HANDICAP,
				"disability_purple.png");
	}

	public static void setPOIcolor_handicap(String color, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_POI_COLOR_HANDICAP, color);
		editor.commit();
	}

	public static String getPOIcolor_elevator(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_POI_COLOR_ELEVATOR,
				"elevator_red.png");
	}

	public static void setPOIcolor_elevator(String color, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_POI_COLOR_ELEVATOR, color);
		editor.commit();
	}

	public static String getPOIcolor_exit(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_POI_COLOR_EXIT, "exit_orange.png");
	}

	public static void setPOIcolor_exit(String color, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_POI_COLOR_EXIT, color);
		editor.commit();
	}

	public static String getPOIcolor_info(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_POI_COLOR_INFO,
				"information_green.png");
	}

	public static void setPOIcolor_info(String color, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_POI_COLOR_INFO, color);
		editor.commit();
	}

	public static String getPOIcolor_stairs(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_POI_COLOR_STAIRS, "stairs_red.png");
	}

	public static void setPOIcolor_stairs(String color, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_POI_COLOR_STAIRS, color);
		editor.commit();
	}

	public static String getPOIcolor_restroom(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_POI_COLOR_RESTROOM,
				"toilets_yellow.png");
	}

	public static void setPOIcolor_restroom(String color, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_POI_COLOR_RESTROOM, color);
		editor.commit();
	}

	public static String getPOIcolor_reception(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_POI_COLOR_RECEPTION,
				"waiting_blue.png");
	}

	public static void setPOIcolor_reception(String color, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_POI_COLOR_RECEPTION, color);
		editor.commit();
	}

	public static String getPOIcolor_cafe(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_POI_COLOR_CAFE,
				"coffee_purple.png");
	}

	public static void setPOIcolor_cafe(String color, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_POI_COLOR_CAFE, color);
		editor.commit();
	}
	
	public static String getDefaultPhone(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_DEFAULT_PHONE, DEFAULT_PHONE_NUMBER);
	}

	public static void setDefaultPhone(String phone, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_DEFAULT_PHONE, phone);
		editor.commit();
	}
	
	public static String getDefaultEmail(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_DEFAULT_EMAIL, DEFAULT_EMAIL_ADDRESS);
	}

	public static void setDefaultEmail(String email, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putString(PREFERENCE_DEFAULT_EMAIL, email);
		editor.commit();
	}
	
	public static boolean getClearData(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFERENCE_CLEAR_DATA, false);
	}

	public static void setClearData(boolean bClear, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_CLEAR_DATA, bClear);
		editor.commit();
	}
	
	public static boolean getSyncOnStartup(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFERENCE_SYNC_ON_STARTUP, false);
	}

	public static void setSyncOnStartup(boolean bSync, Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		editor.putBoolean(PREFERENCE_SYNC_ON_STARTUP, bSync);
		editor.commit();
	}
}
