package com.cs460402.activities;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public class AppPreferencesActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setPreferenceScreen(createPreferenceHierarchy());
	}

	private PreferenceScreen createPreferenceHierarchy() {
		// Root
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(
				this);

		// Application preferences
		PreferenceCategory AppPrefCat = new PreferenceCategory(this);
		AppPrefCat.setTitle("Application Preferences");
		root.addPreference(AppPrefCat);

		// Display POI (Screen preference)
		PreferenceScreen screenDisplayPOI = getPreferenceManager().createPreferenceScreen(this);
		// screenDisplayPOI.setKey("screen_preference");
		screenDisplayPOI.setTitle("Display Points of Interest");
		screenDisplayPOI.setSummary("Select which POIs you want displayed on map");
		AppPrefCat.addPreference(screenDisplayPOI);
			// Handicap
			CheckBoxPreference displayHandicapPOI = new CheckBoxPreference(this);
			displayHandicapPOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_HANDICAP);
			displayHandicapPOI.setTitle("Handicap");
			displayHandicapPOI.setSummary("Display handicap accessable areas");
			displayHandicapPOI.setDefaultValue(false);
			screenDisplayPOI.addPreference(displayHandicapPOI);
			// Restrooms
			CheckBoxPreference displayRestroomsPOI = new CheckBoxPreference(this);
			displayRestroomsPOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_RESTROOM);
			displayRestroomsPOI.setTitle("Restrooms");
			displayRestroomsPOI.setSummary("Display restrooms");
			displayRestroomsPOI.setDefaultValue(true);
			screenDisplayPOI.addPreference(displayRestroomsPOI);
			// Information
			CheckBoxPreference displayInfoPOI = new CheckBoxPreference(this);
			displayInfoPOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_INFO);
			displayInfoPOI.setTitle("Information");
			displayInfoPOI.setSummary("Display areas that you can get help and information");
			displayInfoPOI.setDefaultValue(false);
			screenDisplayPOI.addPreference(displayInfoPOI);
			// Reception
			CheckBoxPreference displayReceptionPOI = new CheckBoxPreference(this);
			displayReceptionPOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_RECEPTION);
			displayReceptionPOI.setTitle("Reception");
			displayReceptionPOI.setSummary("Display reception areas");
			displayReceptionPOI.setDefaultValue(false);
			screenDisplayPOI.addPreference(displayReceptionPOI);
			// Stairs
			CheckBoxPreference displayStairsPOI = new CheckBoxPreference(this);
			displayStairsPOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_STAIRS);
			displayStairsPOI.setTitle("Stairs");
			displayStairsPOI.setSummary("Display stairs");
			displayStairsPOI.setDefaultValue(true);
			screenDisplayPOI.addPreference(displayStairsPOI);
			// Elevator
			CheckBoxPreference displayElevatorPOI = new CheckBoxPreference(this);
			displayElevatorPOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_ELEVATOR);
			displayElevatorPOI.setTitle("Elevators");
			displayElevatorPOI.setSummary("Display elevators");
			displayElevatorPOI.setDefaultValue(true);
			screenDisplayPOI.addPreference(displayElevatorPOI);
			// Exit
			CheckBoxPreference displayExitPOI = new CheckBoxPreference(this);
			displayExitPOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_EXIT);
			displayExitPOI.setTitle("Exits");
			displayExitPOI.setSummary("Display building exits");
			displayExitPOI.setDefaultValue(true);
			screenDisplayPOI.addPreference(displayExitPOI);
			// Cafe
			CheckBoxPreference displayCafePOI = new CheckBoxPreference(this);
			displayCafePOI.setKey(AppPrefs.PREFERENCE_POI_DISPLAY_CAFE);
			displayCafePOI.setTitle("Cafeterias");
			displayCafePOI.setSummary("Display cafeterias");
			displayCafePOI.setDefaultValue(false);
			screenDisplayPOI.addPreference(displayCafePOI);
					
			
		// Stairs or Elevator preference
		ListPreference elevatorPref = new ListPreference(this);
		elevatorPref.setEntries(new CharSequence[] { "Elevator", "Stairs", "Either" });
		elevatorPref.setEntryValues(new CharSequence[] { "elevator", "stairs", "either" });
		elevatorPref.setDialogTitle("Elevator or Stairs");
		elevatorPref.setKey(AppPrefs.PREFERENCE_STAIRS);
		elevatorPref.setTitle("Elevator or Stairs");
		elevatorPref.setSummary("Default method of traveling between floors");
		elevatorPref.setDefaultValue("elevator");
		AppPrefCat.addPreference(elevatorPref);

		// Verbose
		CheckBoxPreference verbosePref = new CheckBoxPreference(this);
		verbosePref.setKey(AppPrefs.PREFERENCE_VERBOSE);
		verbosePref.setTitle("Verbose");
		verbosePref.setSummary("No map points are skipped when generating route steps");
		verbosePref.setDefaultValue(false);
		AppPrefCat.addPreference(verbosePref);
		
		// Phone
		EditTextPreference defaultPhonePref = new EditTextPreference(this);
		defaultPhonePref.setDialogTitle("Default Phone Number");
		defaultPhonePref.setKey(AppPrefs.PREFERENCE_DEFAULT_PHONE);
		defaultPhonePref.setTitle("Default Phone Number");
		defaultPhonePref.setSummary("Enter phone number without any dashes. e.g. 8005551234. Used when sharing routes");
        AppPrefCat.addPreference(defaultPhonePref);
		
        // email
 		EditTextPreference defaultEmailPref = new EditTextPreference(this);
 		defaultEmailPref.setDialogTitle("Default Email Address");
 		defaultEmailPref.setKey(AppPrefs.PREFERENCE_DEFAULT_EMAIL);
 		defaultEmailPref.setTitle("Default Email Address");
 		defaultEmailPref.setSummary("Enter default email address. Used when sharing routes");
         AppPrefCat.addPreference(defaultEmailPref);     		
        
         // Clear Data
 		CheckBoxPreference cleardataPref = new CheckBoxPreference(this);
 		cleardataPref.setKey(AppPrefs.PREFERENCE_CLEAR_DATA);
 		cleardataPref.setTitle("Clear Data on Startup");
 		cleardataPref.setSummary("Clear start and end locations when app starts");
 		cleardataPref.setDefaultValue(false);
 		AppPrefCat.addPreference(cleardataPref);
 		
		// POI color (Screen preference)
		PreferenceScreen screenPOIcolor = getPreferenceManager().createPreferenceScreen(this);
		// screenDisplayPOI.setKey("screen_preference");
		screenPOIcolor.setTitle("POI Color Options");
		screenPOIcolor.setSummary("Configure icon colors for POIs");
		AppPrefCat.addPreference(screenPOIcolor);
			// Handicap
			ListPreference colorHandicapPOI = new ListPreference(this);
			colorHandicapPOI.setEntries(new CharSequence[] { "Red", "Orange",
					"Yellow", "Green", "Blue", "Purple", "Black" });
			colorHandicapPOI.setEntryValues(new CharSequence[] {
					"disability_red.png", "disability_orange.png",
					"disability_yellow.png", "disability_green.png",
					"disability_blue.png", "disability_purple.png",
					"disability_black.png" });
			colorHandicapPOI.setDialogTitle("Handicap Icon Color");
			colorHandicapPOI.setKey(AppPrefs.PREFERENCE_POI_COLOR_HANDICAP);
			colorHandicapPOI.setTitle("Handicap");
			colorHandicapPOI.setSummary("Change handicap icon color");
			colorHandicapPOI.setDefaultValue("disability_purple.png");
			screenPOIcolor.addPreference(colorHandicapPOI);
			// Restrooms
			ListPreference colorRestroomsPOI = new ListPreference(this);
			colorRestroomsPOI.setEntries(new CharSequence[] { "Red", "Orange",
					"Yellow", "Green", "Blue", "Purple", "Black" });
			colorRestroomsPOI.setEntryValues(new CharSequence[] {
					"toilets_red.png", "toilets_orange.png", "toilets_yellow.png",
					"toilets_green.png", "toilets_blue.png", "toilets_purple.png",
					"toilets_black.png" });
			colorRestroomsPOI.setDialogTitle("Restrooms Icon Color");
			colorRestroomsPOI.setKey(AppPrefs.PREFERENCE_POI_COLOR_RESTROOM);
			colorRestroomsPOI.setTitle("Restrooms");
			colorRestroomsPOI.setSummary("Change restrooms icon color");
			colorRestroomsPOI.setDefaultValue("toilets_yellow.png");
			screenPOIcolor.addPreference(colorRestroomsPOI);
			// Information
			ListPreference colorInfoPOI = new ListPreference(this);
			colorInfoPOI.setEntries(new CharSequence[] { "Red", "Orange", "Yellow",
					"Green", "Blue", "Purple", "Black" });
			colorInfoPOI.setEntryValues(new CharSequence[] { "information_red.png",
					"information_orange.png", "information_yellow.png",
					"information_green.png", "information_blue.png",
					"information_purple.png", "information_black.png" });
			colorInfoPOI.setDialogTitle("Information Icon Color");
			colorInfoPOI.setKey(AppPrefs.PREFERENCE_POI_COLOR_INFO);
			colorInfoPOI.setTitle("Information");
			colorInfoPOI.setSummary("Change information icon color");
			colorInfoPOI.setDefaultValue("information_green.png");
			screenPOIcolor.addPreference(colorInfoPOI);
			// Reception
			ListPreference colorReceptionPOI = new ListPreference(this);
			colorReceptionPOI.setEntries(new CharSequence[] { "Red", "Orange",
					"Yellow", "Green", "Blue", "Purple", "Black" });
			colorReceptionPOI.setEntryValues(new CharSequence[] {
					"waiting_red.png", "waiting_orange.png", "waiting_yellow.png",
					"waiting_green.png", "waiting_blue.png", "waiting_purple.png",
					"waiting_black.png" });
			colorReceptionPOI.setDialogTitle("Reception Icon Color");
			colorReceptionPOI.setKey(AppPrefs.PREFERENCE_POI_COLOR_RECEPTION);
			colorReceptionPOI.setTitle("Reception");
			colorReceptionPOI.setSummary("Change reception icon color");
			colorReceptionPOI.setDefaultValue("waiting_blue.png");
			screenPOIcolor.addPreference(colorReceptionPOI);
			// Stairs
			ListPreference colorStairsPOI = new ListPreference(this);
			colorStairsPOI.setEntries(new CharSequence[] { "Red", "Orange",
					"Yellow", "Green", "Blue", "Purple", "Black" });
			colorStairsPOI.setEntryValues(new CharSequence[] { "stairs_red.png",
					"stairs_orange.png", "stairs_yellow.png", "stairs_green.png",
					"stairs_blue.png", "stairs_purple.png", "stairs_black.png" });
			colorStairsPOI.setDialogTitle("Stairs Icon Color");
			colorStairsPOI.setKey(AppPrefs.PREFERENCE_POI_COLOR_STAIRS);
			colorStairsPOI.setTitle("Stairs");
			colorStairsPOI.setSummary("Change stairs icon color");
			colorStairsPOI.setDefaultValue("stairs_red.png");
			screenPOIcolor.addPreference(colorStairsPOI);
			// Elevator
			ListPreference colorElevatorPOI = new ListPreference(this);
			colorElevatorPOI.setEntries(new CharSequence[] { "Red", "Orange",
					"Yellow", "Green", "Blue", "Purple", "Black" });
			colorElevatorPOI.setEntryValues(new CharSequence[] {
					"elevator_red.png", "elevator_orange.png",
					"elevator_yellow.png", "elevator_green.png",
					"elevator_blue.png", "elevator_purple.png",
					"elevator_black.png" });
			colorElevatorPOI.setDialogTitle("Elevator Icon Color");
			colorElevatorPOI.setKey(AppPrefs.PREFERENCE_POI_COLOR_ELEVATOR);
			colorElevatorPOI.setTitle("Elevators");
			colorElevatorPOI.setSummary("Change elevator icon color");
			colorElevatorPOI.setDefaultValue("elevator_red.png");
			screenPOIcolor.addPreference(colorElevatorPOI);
			// Exit
			ListPreference colorExitPOI = new ListPreference(this);
			colorExitPOI.setEntries(new CharSequence[] { "Red", "Orange", "Yellow",
					"Green", "Blue", "Purple", "Black" });
			colorExitPOI.setEntryValues(new CharSequence[] { "exit_red.png",
					"exit_orange.png", "exit_yellow.png", "exit_green.png",
					"exit_blue.png", "exit_purple.png", "exit_black.png" });
			colorExitPOI.setDialogTitle("Exit Icon Color");
			colorExitPOI.setKey(AppPrefs.PREFERENCE_POI_COLOR_EXIT);
			colorExitPOI.setTitle("Exits");
			colorExitPOI.setSummary("Change exit icon color");
			colorExitPOI.setDefaultValue("exit_orange.png");
			screenPOIcolor.addPreference(colorExitPOI);
			// Cafe
			ListPreference colorCafePOI = new ListPreference(this);
			colorCafePOI.setEntries(new CharSequence[] { "Red", "Orange", "Yellow",
					"Green", "Blue", "Purple", "Black" });
			colorCafePOI.setEntryValues(new CharSequence[] { "coffee_red.png",
					"coffee_orange.png", "coffee_yellow.png", "coffee_green.png",
					"coffee_blue.png", "coffee_purple.png", "coffee_black.png" });
			colorCafePOI.setDialogTitle("Cafeteria Icon Color");
			colorCafePOI.setKey(AppPrefs.PREFERENCE_POI_COLOR_CAFE);
			colorCafePOI.setTitle("Cafeterias");
			colorCafePOI.setSummary("Change cafeteria icon color");
			colorCafePOI.setDefaultValue("coffee_purple.png");
			screenPOIcolor.addPreference(colorCafePOI);
		
		// Dot color
		ListPreference dotColorPref = new ListPreference(this);
		dotColorPref.setEntries(new CharSequence[] { "Red", "Yellow", "Green", "Blue", "Purple" });
		dotColorPref.setEntryValues(new CharSequence[] { "redarrowdot.png", "yellowarrowdot.png", "greenarrowdot.png", "bluearrowdot.png", "purplearrowdot.png" });
		dotColorPref.setDialogTitle("Location Marker Color");
		dotColorPref.setKey(AppPrefs.PREFERENCE_DOT_COLOR);
		dotColorPref.setTitle("Location Marker Color");
		dotColorPref.setSummary("Preferred color of the dot that marks your spot on the map");
		dotColorPref.setDefaultValue("greenarrowdot.png");
		AppPrefCat.addPreference(dotColorPref);

		// Line width (list)
		ListPreference lineWidthPref = new ListPreference(this);
		lineWidthPref.setEntries(new CharSequence[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" });
		lineWidthPref.setEntryValues(new CharSequence[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" });
		lineWidthPref.setDialogTitle("Route Line Width");
		lineWidthPref.setKey(AppPrefs.PREFERENCE_LINE_WIDTH);
		lineWidthPref.setTitle("Route Line Width");
		lineWidthPref.setSummary("Preferred thickness of the route line");
		lineWidthPref.setDefaultValue("3");
		AppPrefCat.addPreference(lineWidthPref);

		// Line color (list)
		ListPreference lineColorPref = new ListPreference(this);
		lineColorPref.setEntries(new CharSequence[] { "Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Black", "Brown", "Gray" });
		lineColorPref.setEntryValues(new CharSequence[] { "red", "orange", "yellow", "green", "blue", "purple", "black", "brown", "gray" });
		lineColorPref.setDialogTitle("Route Line Color");
		lineColorPref.setKey(AppPrefs.PREFERENCE_LINE_COLOR);
		lineColorPref.setTitle("Route Line Color");
		lineColorPref.setSummary("Choose a color from a list");
		lineColorPref.setDefaultValue("blue");
		AppPrefCat.addPreference(lineColorPref);

		// Line color (edit box)
		EditTextPreference customLineColorPref = new EditTextPreference(this);
		customLineColorPref.setDialogTitle("Custom Line Color: ex. #ff00ff");
		customLineColorPref.setKey(AppPrefs.PREFERENCE_LINE_COLOR);
		customLineColorPref.setTitle("Custom Route Line Color");
		customLineColorPref.setSummary("Enter a #hex color code");
		AppPrefCat.addPreference(customLineColorPref);
		
		// Sync on startup
		CheckBoxPreference synconstartupPref = new CheckBoxPreference(this);
		synconstartupPref.setKey(AppPrefs.PREFERENCE_SYNC_ON_STARTUP);
		synconstartupPref.setTitle("Sync on Startup");
		synconstartupPref.setSummary("Determines if app will sync with database upon startup");
		synconstartupPref.setDefaultValue(false);
		AppPrefCat.addPreference(synconstartupPref);
				
		// Default building used in app (list)
		ListPreference browseMap = new ListPreference(this);
		browseMap.setEntries(new CharSequence[] {"Wang Ambulatory Care Center", "Test Building" });
		browseMap.setEntryValues(new CharSequence[] { "wang", "testBldg" });
		browseMap.setDialogTitle("Choose Building");
		browseMap.setKey(AppPrefs.PREFERENCE_BROWSE_BLDG);
		browseMap.setTitle("Default Browsing Building");
		browseMap.setSummary("Which building is used when browsing maps");
		browseMap.setDefaultValue("wang");
		AppPrefCat.addPreference(browseMap);

		return root;
	}
}
