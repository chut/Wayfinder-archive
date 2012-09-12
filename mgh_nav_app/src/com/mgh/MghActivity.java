/*
 * This is the main activity of the application. It is called once the application is launched.
 * 
 * Created By: Kevin, Nick and Omar
 * 
 * Last Modified By: Omar
 *  
 */
package com.mgh;


import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MghActivity extends Activity implements OnClickListener {

	public Button startLocationListButton;
	public Button scanStartButton;
	public Button endLocationListButton;
	public Button go;
	public Room startRoom;
	public Room endRoom = null;
	public TextView startText;
	public TextView endText;
	public TextView customFont;
	private int counter = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);



		//change font for title
		customFont = (TextView) findViewById(R.id.titlefont);
		Typeface titleFont = Typeface.createFromAsset(getAssets(),"fonts/MTCORSVA.TTF");
		customFont.setTypeface(titleFont);

		// connect buttons
		startLocationListButton = (Button) findViewById(R.id.listStartButton);
		endLocationListButton = (Button) findViewById(R.id.listEndButton);
		scanStartButton = (Button) findViewById(R.id.scanStartButton);
		go = (Button) findViewById(R.id.goButton);

		// set listeners
		startLocationListButton.setOnClickListener(this);
		scanStartButton.setOnClickListener(this);
		endLocationListButton.setOnClickListener(this);
		go.setOnClickListener(this);


		//connect TextViews
		endText = (TextView) findViewById(R.id.endText);
		startText = (TextView) findViewById(R.id.startText);

	}


	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 * 
	 * created by nick
	 */
	@Override
	public void onConfigurationChanged(Configuration config) {	
		super.onConfigurationChanged(config);
		if (counter >= 1) {
			startText.setText("");
		}		
		Bundle b = new Bundle();
		onSaveInstanceState(b);
		Log.i("foo", "config called");
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 * 
	 * created by: nick
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		if(endRoom != null) {
			if (!endRoom.getRoom().equals("")) {
				savedInstanceState.putParcelable("endroom", endRoom);
				savedInstanceState.putInt("counter", counter);
			}
		}
		// etc.		
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 * 
	 * Created by: Nick
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		Room r = savedInstanceState.getParcelable("endroom");
		counter = savedInstanceState.getInt("counter");	
		endText.setText(r.getRoom().toString());
	}
	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * 
	 * http://developer.android.com/resources/articles/can-i-use-this-intent.html
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	//checks if an intent is available from other applications
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
				packageManager.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
	// code for preferences menu	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		return true;
	}

	//Listener for option menu items
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, PreferencesActivity.class));
			return true;
		case R.id.how_to:
			startActivity(new Intent(this, helpActivity.class));
			return true;
		case R.id.help:
			startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:617-726-2000")));
			return true;
		case R.id.quit:
			System.exit(0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	//checks to make sure that the scan is valid
	public boolean isValidScanResult(String result) {
		//format for barcode text: mghnav-floor-room name-type-location point
		boolean isValid = false;
		String[] parsed = result.split("[-]");
		if (parsed[0].toLowerCase().equals("mghnav")) {
			isValid = true;
		}
		return isValid;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 * 
	 * when an button is pressed it returns request code and what it does is handeled here
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		//result from selecting a room from the room activity
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {			
				startRoom = data.getParcelableExtra("room");
				startText.setText("Room: "+ startRoom.getRoom());
			}
		}
		//scanned barcode must be text and fulfills floor-room-type-pathpoint
		/*
		 * if "scan" button is selected checks if there is a valid barcode scanner if it does exist it launches the barcode scanner intent
		 * after scanning barcode checks its a valid barcode
		 * if barcode is valid creates a room object and sets the start room text
		 * if the barcode scanned was not valid gives an alert
		 * 
		 * if no barcode scanner was found gives an alert dialog which also sends the user to google play to install a barcode scanner
		 *
		 *created by omar 
		 */
		else if (requestCode == 1){
			if (resultCode == Activity.RESULT_OK) {
				String scannedRoom = data.getStringExtra("SCAN_RESULT");
				if (isValidScanResult(scannedRoom)) {
					startRoom = new Room(scannedRoom);
					startText.setText("Room: "+ startRoom.getRoom());
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage("Invalid scan. Please scan a valid code or select a room from the list.")
					.setCancelable(true)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}
			} 
			else if (resultCode == Activity.RESULT_CANCELED) {
				// Handle cancel
				Toast.makeText(this, "Scan Canceled", Toast.LENGTH_SHORT).show();
			}
		}

		/*
		 * does the same for request code 1 but for the end room
		 */
		if (requestCode == 2) {
			if (resultCode == RESULT_OK) {			
				endRoom = data.getParcelableExtra("room");
				endText.setText("Room: "+ endRoom.getRoom());
			}
		}

	}

	//onclick button listener, gets the ID of the button pressed and tells it what to do (starting activities, then sending it onActivity result
	public void onClick(View v) {
		switch(v.getId()){

		case R.id.listStartButton :{
			Intent list = new Intent(this, ListSelectActivity.class);
			startActivityForResult(list, 0);
			break;
		}
		case R.id.scanStartButton:{

			//checks if a barcode scanner is available otherwise pops up a message and asks the user to install a scanner
			if(isIntentAvailable(this,"com.google.zxing.client.android.SCAN")){

				//Toast.makeText(this, "start scanner", 2).show(); //toast to show button working
				Intent scanIntent = new Intent("com.google.zxing.client.android.SCAN");   
				scanIntent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(scanIntent, 1);
			}
			else{
				//create an alert if no barcode scanner found and redirect to google play
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("No Barcode Scanner found. Install one?")
				.setCancelable(true)
				.setPositiveButton("install", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("market://details?id=com.google.zxing.client.android"));
						startActivity(intent);						
					}
				})
				.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();

			}
			break;
		}
		case R.id.listEndButton:{
			Intent list = new Intent(this, ListSelectActivity.class);
			startActivityForResult(list, 2);
			break;
		}
		/*
		 * go button to take user to map activity
		 * 
		 * if starting or ending null --> pulls up an alert box with error msg
		 * if start == end --> pulls up an alert box with error msg
		 * if start and end are associated with the same location point tells the user they are already at ending location
		 * 
		 *  
		 *  if all is good, bundles the start and end location into an intent and starts the activity
		 */
		case R.id.goButton:{
			//Intent loadMap = new Intent(this, mapActivity.class);
			counter++;

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(true).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				} 
			});
			AlertDialog alert = builder.create();		

			if (startText.getText().toString().equals(endText.getText().toString())) {
				alert.setMessage("Please select a different starting or ending point. They are currently the same.");
				alert.show();
			} else if(startText.getText().toString().equals("") || startText.getText().toString() == null || endText.getText().toString().equals("") || endText.getText().toString() ==null) {
				alert.setMessage("Please enter a starting and ending location.");
				alert.show();
			} else if(startRoom.getLocationPoint().equals(endRoom.getLocationPoint())) {
				alert.setMessage("You are in the same location. Look around for the room.");
				alert.show();
			} else {
				Intent intent = new Intent(this, mapActivity.class);
				Bundle b = new Bundle();
				b.putString("startpoint",startRoom.getLocationPoint());
				b.putString("endpoint", endRoom.getLocationPoint());
				intent.putExtras(b);
				startActivity(intent);
				startText.setText("");
				//Toast.makeText(this, "Go Pressed", 2).show();
				break;
			}
		}

		}

	}


} 
