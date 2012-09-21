package com.cs460402.activities;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cs460402.app.AppConstants;
import com.cs460402.app.async_core.AsyncConstants;
import com.cs460402.app.io.AppIO;
import com.cs460402.app.io.NodeInfo;
import com.cs460402.app.io.sqlite.SQLiteConstants;

public class WebViewActivity extends Activity implements OnClickListener,
		SensorEventListener {

	private NavWebView webView;
	private LinearLayout progress;
	private TextView textView, startText, endText;
	private ImageButton leftBtn, rightBtn;
	private Button goButton, backButton, filterButton;
	private ImageView routeSwitch;
	private SensorManager sm = null;
	private AppIO appIO;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		
		appIO = new AppIO(AsyncConstants.DEFAULT_THREAD_POOL_SIZE);
		
		// create a sensor manager object
		sm = (SensorManager) getSystemService(SENSOR_SERVICE); 

		progress = (LinearLayout) findViewById(R.id.progresslayout);
		textView = (TextView) findViewById(R.id.stepText);
		(startText = (TextView) findViewById(R.id.routeStartText)).setOnClickListener(this);
		(endText = (TextView) findViewById(R.id.routeEndText)).setOnClickListener(this);
		
		startText.setText("Start: " + AppPrefs.getStartLabel(this));
		endText.setText("End: " + AppPrefs.getEndLabel(this));
		
		(leftBtn = (ImageButton) findViewById(R.id.leftButton)).setOnClickListener(this);
		(rightBtn = (ImageButton) findViewById(R.id.rightButton)).setOnClickListener(this);
		(goButton = (Button) findViewById(R.id.routeGo)).setOnClickListener(this);
		(backButton = (Button) findViewById(R.id.routeBack)).setOnClickListener(this);
		(filterButton = (Button) findViewById(R.id.routeFilter)).setOnClickListener(this);
		(routeSwitch = (ImageView) findViewById(R.id.routeSwitch)).setOnClickListener(this);

		(webView = (NavWebView) findViewById(R.id.webView)).setTextView(textView).setProgressView(progress).setMode(webView.MODE_ROUTE).setZoomLevel(150);
		// webView.addJavascriptInterface(new JavaScriptInterface(this),
		// "Android");
		
		webView.loadUrl(AppConstants.GLOBAL_WEB_SITE + "?mode=route&start="
				+ AppPrefs.getStartID(this) + "&end=" + AppPrefs.getEndID(this)
				+ "&verbose=" + (AppPrefs.getVerbose(this) ? "true" : "false")
				+ "&soe=" + AppPrefs.getStairs(this));

	}

	public void onClick(View button) {
		switch (button.getId()) {
		case R.id.leftButton:
			webView.previousStep();
			break;
		case R.id.rightButton:
			webView.nextStep();
			break;
		case R.id.routeGo:
			webView.loadUrl(AppConstants.GLOBAL_WEB_SITE + "?mode=route&start="
					+ AppPrefs.getStartID(this) + "&end=" + AppPrefs.getEndID(this)
					+ "&verbose=" + (AppPrefs.getVerbose(this) ? "true" : "false")
					+ "&soe=" + AppPrefs.getStairs(this));
			break;
		case R.id.routeBack:
			Intent entry = new Intent(this, AppConstants.CLASS_ENTRY);
			entry.putExtra("tab", 0);
			startActivity(entry);
			break;
		case R.id.routeFilter:
			Intent intent6 = new Intent(this, AppConstants.CLASS_POIPREF);
			startActivity(intent6);
			break;
		case R.id.routeSwitch:
			String swapStartID = AppPrefs.getStartID(this);
			String swapStartLabel = AppPrefs.getStartLabel(this);
			AppPrefs.setStartID(AppPrefs.getEndID(this), this);
			AppPrefs.setStartLabel(AppPrefs.getEndLabel(this), this);
			startText.setText("Start: " + AppPrefs.getEndLabel(this));
			AppPrefs.setEndID(swapStartID, this);
			AppPrefs.setEndLabel(swapStartLabel, this);
			endText.setText("End: " + swapStartLabel);
			
			break;
		case R.id.routeStartText:
			if (AppPrefs.getStartID(this).length() == 0) break;
			String startFloorID = getFloorIDbyNodeID(AppPrefs.getStartID(this));
			if (startFloorID == null) {
				Toast.makeText(this, "Error - Start location is not in " + AppPrefs.getBrowseBldg(this) + "!",Toast.LENGTH_LONG).show();
				break;
			}
			Intent map = new Intent(this, AppConstants.CLASS_ENTRY);
			map.putExtra("tab", 4);		// there is no tab 4, but we use this to tell the Activity to load the browsing map and center it on a node 
			map.putExtra("floor", startFloorID);
			map.putExtra("start", true);
			startActivity(map);
			break;
		case R.id.routeEndText:
			if (AppPrefs.getEndID(this).length() == 0) break;
			String endFloorID = getFloorIDbyNodeID(AppPrefs.getEndID(this));
			if (endFloorID == null) {
				Toast.makeText(this, "Error - End location is not in " + AppPrefs.getBrowseBldg(this) + "!",Toast.LENGTH_LONG).show();
			}
			Intent map2 = new Intent(this, AppConstants.CLASS_ENTRY);
			map2.putExtra("tab", 4);		// there is no tab 4, but we use this to tell the Activity to load the browsing map and center it on a node 
			map2.putExtra("floor", endFloorID);
			map2.putExtra("start", false);
			startActivity(map2);	
			
			break;
		default:
			break;
		}
	}

	private String getFloorIDbyNodeID (String nodeID) {
		Future<ArrayList<?>> future = appIO.sqlite_async_getData(SQLiteConstants.QUERY_FLOORID_BY_NODEID, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, null, null, this, AppPrefs.getBrowseBldg(this), nodeID);
		try {
			ArrayList<NodeInfo> results  = (ArrayList<NodeInfo>) future.get();
			if (results != null && results.size() > 0) {
				return results.get(0).floorID;
			} 
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
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

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	public void onSensorChanged(SensorEvent event) {
		int type = event.sensor.getType();

		if (type == Sensor.TYPE_ORIENTATION) {
			// x == event.values[0]
			// y == event.values[1]
			// z == event.values[2]

			webView.rotateDot((int) event.values[0]);
		}
		if (type == Sensor.TYPE_ACCELEROMETER) {
			// x == event.values[0]
			// y == event.values[1]
			// z == event.values[2]

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
		if (webView.isRouteMode()) {
			// register listeners
			sm.registerListener(this,
					sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),
					SensorManager.SENSOR_DELAY_NORMAL);

			sm.registerListener(this,
					sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);

			sm.registerListener(this,
					sm.getDefaultSensor(Sensor.TYPE_TEMPERATURE),
					SensorManager.SENSOR_DELAY_NORMAL);
		}

		webView.getHandler().sendMessage(
				webView.getHandler().obtainMessage(
						webView.HANDLER_UPDATE_MAP_PREFS));
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStop() {
		// Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
		if (webView.isRouteMode())
			sm.unregisterListener(this);
		super.onStop();
	}

}
