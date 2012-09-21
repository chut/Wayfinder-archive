package com.cs460402.activities;

import com.cs460402.app.AppConstants;
import com.cs460402.app.async_core.AsyncConstants;
import com.cs460402.app.io.AppIO;
import com.cs460402.app.io.sqlite.SQLiteConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends Activity {

	private AppIO appIO;
	private Activity activity;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		// clear start and end locations if setting is set to do so
		if (AppPrefs.getClearData(this)) {
			Log.i("SPLASH","getClearData - true");
			AppPrefs.setStartID("", this);
			AppPrefs.setStartLabel("", this);
			AppPrefs.setEndID("", this);
			AppPrefs.setEndLabel("", this);
		}
		
		if (AppPrefs.getSyncOnStartup(this)) {
			Log.i("SPLASH","sync database");
			activity = this;
			appIO = new AppIO(AsyncConstants.DEFAULT_THREAD_POOL_SIZE);
			appIO.sqlite_async_getData(SQLiteConstants.QUERY_LOAD_APP,
					SQLiteConstants.PROGRESS_NONE, new Runnable() {
						public void run() {
							Intent intent = new Intent(activity, AppConstants.CLASS_HOME);
							startActivity(intent);
							finish();
						}
					}, null, this);
		} else {
			Log.i("SPLASH","dont sync database");
			Intent intent2 = new Intent(this, AppConstants.CLASS_HOME);
			startActivity(intent2);
			finish();
		}
	}

}
