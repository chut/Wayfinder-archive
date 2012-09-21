package com.cs460402.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public class POIprefsActivity extends PreferenceActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setPreferenceScreen(createPreferenceHierarchy());
	}

	private PreferenceScreen createPreferenceHierarchy() {
		// Root
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		
		// Application preferences
		PreferenceCategory poiPrefCat = new PreferenceCategory(this);
		poiPrefCat.setTitle("Point of Interest Filter");
		root.addPreference(poiPrefCat);
		
		// Handicap
		CheckBoxPreference displayHandicapPOI = new CheckBoxPreference(this);
		displayHandicapPOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_HANDICAP);
		displayHandicapPOI.setTitle("Handicap");
		displayHandicapPOI.setSummary("Display handicap accessable areas");
		displayHandicapPOI.setDefaultValue(false);
		poiPrefCat.addPreference(displayHandicapPOI);
		// Restrooms
		CheckBoxPreference displayRestroomsPOI = new CheckBoxPreference(this);
		displayRestroomsPOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_RESTROOM);
		displayRestroomsPOI.setTitle("Restrooms");
		displayRestroomsPOI.setSummary("Display restrooms");
		displayRestroomsPOI.setDefaultValue(true);
		poiPrefCat.addPreference(displayRestroomsPOI);
		// Information
		CheckBoxPreference displayInfoPOI = new CheckBoxPreference(this);
		displayInfoPOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_INFO);
		displayInfoPOI.setTitle("Information");
		displayInfoPOI.setSummary("Display areas that you can get help and information");
		displayInfoPOI.setDefaultValue(false);
		poiPrefCat.addPreference(displayInfoPOI);
		// Reception
		CheckBoxPreference displayReceptionPOI = new CheckBoxPreference(this);
		displayReceptionPOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_RECEPTION);
		displayReceptionPOI.setTitle("Reception");
		displayReceptionPOI.setSummary("Display reception areas");
		displayReceptionPOI.setDefaultValue(false);
		poiPrefCat.addPreference(displayReceptionPOI);
		// Stairs
		CheckBoxPreference displayStairsPOI = new CheckBoxPreference(this);
		displayStairsPOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_STAIRS);
		displayStairsPOI.setTitle("Stairs");
		displayStairsPOI.setSummary("Display stairs");
		displayStairsPOI.setDefaultValue(true);
		poiPrefCat.addPreference(displayStairsPOI);
		// Elevator
		CheckBoxPreference displayElevatorPOI = new CheckBoxPreference(this);
		displayElevatorPOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_ELEVATOR);
		displayElevatorPOI.setTitle("Elevators");
		displayElevatorPOI.setSummary("Display elevators");
		displayElevatorPOI.setDefaultValue(true);
		poiPrefCat.addPreference(displayElevatorPOI);
		// Exit
		CheckBoxPreference displayExitPOI = new CheckBoxPreference(this);
		displayExitPOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_EXIT);
		displayExitPOI.setTitle("Exits");
		displayExitPOI.setSummary("Display building exits");
		displayExitPOI.setDefaultValue(true);
		poiPrefCat.addPreference(displayExitPOI);
		// Cafe
		CheckBoxPreference displayCafePOI = new CheckBoxPreference(this);
		displayCafePOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_CAFE);
		displayCafePOI.setTitle("Cafeterias");
		displayCafePOI.setSummary("Display cafeterias");
		displayCafePOI.setDefaultValue(false);
		poiPrefCat.addPreference(displayCafePOI);
		
		return root;
	}
}