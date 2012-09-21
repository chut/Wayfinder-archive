package com.cs460402.activities;

import com.cs460402.app.AppConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class Home extends Activity implements OnClickListener {

	private Button directions, map, instructions;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		directions = (Button) findViewById(R.id.getDirectionsButton);
		directions.setOnClickListener(this);

		map = (Button) findViewById(R.id.viewMapButton);
		map.setOnClickListener(this);

		instructions = (Button) findViewById(R.id.instructionsButton);
		instructions.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.getDirectionsButton:
			Intent entry = new Intent(this, AppConstants.CLASS_ENTRY);
			entry.putExtra("tab", 0);
			startActivity(entry);

			break;

		case R.id.viewMapButton:
			Intent map = new Intent(this, AppConstants.CLASS_ENTRY);
			map.putExtra("tab", 3);
			startActivity(map);

			// Toast.makeText(this, "View Map", Toast.LENGTH_LONG)
			// .show();

			break;

		case R.id.instructionsButton:
			Intent instructions = new Intent(this,
					AppConstants.CLASS_INSTRUCTIONS);
			startActivity(instructions);

			break;

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		AppOptionsMenu.buildOptionMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return AppOptionsMenu.handleMenuSelection(item, this, AppPrefs.getStartID(this), AppPrefs.getEndID(this), null);
	}

}
