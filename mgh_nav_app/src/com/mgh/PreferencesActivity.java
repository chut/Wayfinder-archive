/*
 * preference activity set for the settings
 * 
 * Created by Nick Hentschel
 */
package com.mgh;

import android.os.Bundle;
//import android.preference.PreferenceActivity;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings);
    }
}
