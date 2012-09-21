package com.cs460402.activities;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.cs460402.activities.R;
import com.cs460402.app.AppConstants;
import com.cs460402.app.async_core.AsyncConstants;
import com.cs460402.app.io.AppIO;
import com.cs460402.app.io.NodeInfo;
import com.cs460402.app.io.sqlite.SQLiteConstants;

//import com.cs460.hospitalnavigator.BarcodeReader;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.content.SharedPreferences;

public class Entry extends Activity implements OnClickListener, OnItemSelectedListener, OnGestureListener {

	// spinners
	private Spinner start_byFloor_spinner;
	private Spinner start_byType_spinner;
	private Spinner start_spinner;
	private Spinner end_byFloor_spinner;
	private Spinner end_byType_spinner;
	private Spinner end_spinner;
	private ArrayList<NodeInfo> start_spinner_list;
	private ArrayList<NodeInfo> start_spinner_list_DATA;
	private ArrayList<NodeInfo> end_spinner_list;
	private ArrayList<NodeInfo> end_spinner_list_DATA;
	private ArrayList<String> byFloor_spinner_list;
	private ArrayList<String> byType_spinner_list;
	private ArrayAdapter<NodeInfo> start_spinner_adapter;
	private ArrayAdapter<NodeInfo> end_spinner_adapter;
	private ArrayAdapter<String> byFloor_spinner_adapter;
	private ArrayAdapter<String> byType_spinner_adapter;

	// auto-complete
	private AutoCompleteTextView start_autoComplete;
	private AutoCompleteTextView end_autoComplete;
	private ArrayList<NodeInfo> autoComplete_list;
	private ArrayAdapter<NodeInfo> autoComplete_adapter;
	
	// buttons
	private Button searchNextButton;
	private Button enterNextButton;
	private Button scanButton;
	private Button scanNextButton;
	private Button mapNextButton;
	private Button mapFloorButton;
	private Button mapFilterButton;
	private Button searchClearButton;
	private Button enterClearButton;
	private Button scanClearButton;
	
	// textviews
	private TextView searchStartText;
	private TextView searchEndText;
	private TextView enterStartText;
	private TextView enterEndText;
	private TextView scanStartText;
	private TextView scanEndText;
	private TextView mapStartText;
	private TextView mapEndText;
	
	// imageviews
	private ImageView searchSwitch;
	private ImageView enterSwitch;
	private ImageView scanSwitch;
	private ImageView mapSwitch;
	
	private NavWebView webView;
	private LinearLayout progress;
	private TabHost tabs;
	
	private AppIO appIO;
	
	// When the Activity is first loaded, the spinners will call onItemSelected as
	// they are initialized.  We dont want this, so as a work around, we use 
	// these booleans to ignore the first "update" 
	private boolean bFirst_startByFloorSpinner = true;
	private boolean bFirst_startByTypeSpinner = true;
	private boolean bFirst_startRoomListSpinner = true;
	private boolean bFirst_endByFloorSpinner = true;
	private boolean bFirst_endByTypeSpinner = true;
	private boolean bFirst_endRoomListSpinner = true;
	
	private GestureDetector myGesture;
	private int currentTab;
	private int numTab;
	public final int ENABLE_FLING = 4;
	public final int DISABLE_FLING = 3;
	private int maxFling = ENABLE_FLING;	// by default, we have fling turned on, on the map tab 
											// setting this to 3 will turn it off on the map tab
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry);
		
		Log.i("CLEAR","Entry - onCreate: " + AppPrefs.getClearData(this));
		appIO = new AppIO(AsyncConstants.DEFAULT_THREAD_POOL_SIZE);
		
		myGesture = new GestureDetector(this);
		
		Drawable searchIcon, enterIcon, codeIcon, mapIcon;

		tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setup();

		TabHost.TabSpec spec;

		// Tab Search
		spec = tabs.newTabSpec("Type");
		spec.setContent(R.id.Search);
		searchIcon = getResources().getDrawable(R.drawable.menuicon36);
		spec.setIndicator("By Type", searchIcon);
		tabs.addTab(spec);

		// Tab - Enter
		spec = tabs.newTabSpec("Enter");
		spec.setContent(R.id.Enter);
		enterIcon = getResources().getDrawable(R.drawable.searchicon36);
		spec.setIndicator("Search", enterIcon);
		tabs.addTab(spec);

		// Tab - QR Scanner
		spec = tabs.newTabSpec("QR");
		spec.setContent(R.id.QR);
		codeIcon = getResources().getDrawable(R.drawable.codeicon36);
		spec.setIndicator("Scan", codeIcon);
		tabs.addTab(spec);

		// Tab - Map
		spec = tabs.newTabSpec("Map");
		spec.setContent(R.id.mapView);
		mapIcon = getResources().getDrawable(R.drawable.map36);
		spec.setIndicator("Map", mapIcon);
		tabs.addTab(spec);
		progress = (LinearLayout) findViewById(R.id.progresslayout);
		(webView = (NavWebView) findViewById(R.id.webView)).setProgressView(progress).setMode(webView.MODE_BROWSE).setParentActivity(this).setZoomLevel(200d);
		webView.loadUrl(AppConstants.GLOBAL_WEB_SITE + "?mode=browse&bldg="	+ AppPrefs.getBrowseBldg(this));
				
		// Start and End location textviews
		(searchStartText = (TextView) findViewById(R.id.searchStartText)).setOnClickListener(this);
		(searchEndText = (TextView) findViewById(R.id.searchEndText)).setOnClickListener(this);
		(enterStartText = (TextView) findViewById(R.id.enterStartText)).setOnClickListener(this);
		(enterEndText = (TextView) findViewById(R.id.enterEndText)).setOnClickListener(this);
		(scanStartText = (TextView) findViewById(R.id.scanStartText)).setOnClickListener(this);
		(scanEndText = (TextView) findViewById(R.id.scanEndText)).setOnClickListener(this);
		(mapStartText = (TextView) findViewById(R.id.mapStartText)).setOnClickListener(this);
		(mapEndText = (TextView) findViewById(R.id.mapEndText)).setOnClickListener(this);
		
		// Switch imageview
		(searchSwitch = (ImageView) findViewById(R.id.searchSwitch)).setOnClickListener(this);
		(enterSwitch = (ImageView) findViewById(R.id.enterSwitch)).setOnClickListener(this);
		(scanSwitch = (ImageView) findViewById(R.id.scanSwitch)).setOnClickListener(this);
		(mapSwitch = (ImageView) findViewById(R.id.mapSwitch)).setOnClickListener(this);
		
		// next buttons
		searchNextButton = (Button) findViewById(R.id.searchNext);
		searchNextButton.setOnClickListener(this);

		enterNextButton = (Button) findViewById(R.id.enterNext);
		enterNextButton.setOnClickListener(this);

		scanNextButton = (Button) findViewById(R.id.scanNext);
		scanNextButton.setOnClickListener(this);

		mapNextButton = (Button) findViewById(R.id.mapNext);
		mapNextButton.setOnClickListener(this);
		
		// clear buttons
		(searchClearButton = (Button) findViewById(R.id.searchClear)).setOnClickListener(this);;
		(enterClearButton = (Button) findViewById(R.id.enterClear)).setOnClickListener(this);;
		(scanClearButton = (Button) findViewById(R.id.scanClear)).setOnClickListener(this);;
		
		// scan buttons
		scanButton = (Button) findViewById(R.id.ScanStartButton);
		scanButton.setOnClickListener(this);
		
		// map tab buttons
		(mapFloorButton = (Button) findViewById(R.id.mapFloor)).setOnClickListener(this);
		(mapFilterButton = (Button) findViewById(R.id.mapPOI)).setOnClickListener(this);
		
		// auto complete
		autoComplete_list = new ArrayList<NodeInfo>();
		autoComplete_adapter = new ArrayAdapter<NodeInfo>(this, android.R.layout.simple_dropdown_item_1line, autoComplete_list);
				
		start_autoComplete = (AutoCompleteTextView) findViewById(R.id.startRoomText);
		start_autoComplete.setAdapter(autoComplete_adapter);
		start_autoComplete.setOnItemClickListener(new OnItemClickListener() {
			@Override 
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// close the keyboard
				InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		        in.hideSoftInputFromWindow(start_autoComplete.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS); 
				
		        Log.i("AUTO","start_autoComplete, position: " + position + ", id: " + id);
		        updateStartEndLocation(true, (NodeInfo) parent.getItemAtPosition(position), null, null);
		        //updateStartEndLocation(true, autoComplete_list.get(position).toString(), null, null);
			}
		});
		
		end_autoComplete = (AutoCompleteTextView) findViewById(R.id.endRoomText);
		end_autoComplete.setAdapter(autoComplete_adapter);
		end_autoComplete.setOnItemClickListener(new OnItemClickListener() {
			@Override 
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) { 
				InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		        in.hideSoftInputFromWindow(end_autoComplete.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS); 
				
		        Log.i("AUTO","end_autoComplete, position: " + position + ", id: " + id);
		        updateStartEndLocation(false, (NodeInfo) parent.getItemAtPosition(position), null, null);
		        //updateStartEndLocation(false, autoComplete_list.get(position).toString(), null, null);
		        
			}
		});
		
		// spinners
		start_spinner = (Spinner) findViewById(R.id.startRoomListSpinner);
		start_spinner.setOnItemSelectedListener(this);
		start_spinner_list = new ArrayList<NodeInfo>();
		start_spinner_list_DATA = new ArrayList<NodeInfo>();
		start_spinner_adapter = new ArrayAdapter<NodeInfo>(this, android.R.layout.simple_spinner_item, start_spinner_list);
		start_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		start_spinner.setAdapter(start_spinner_adapter);

		end_spinner = (Spinner) findViewById(R.id.endRoomListSpinner);
		end_spinner.setOnItemSelectedListener(this);
		end_spinner_list = new ArrayList<NodeInfo>();
		end_spinner_list_DATA = new ArrayList<NodeInfo>();
		end_spinner_adapter = new ArrayAdapter<NodeInfo>(this, android.R.layout.simple_spinner_item, end_spinner_list);
		end_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		end_spinner.setAdapter(end_spinner_adapter);
		
		byFloor_spinner_list = new ArrayList<String>();
		byFloor_spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, byFloor_spinner_list);
		byFloor_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		byType_spinner_list = new ArrayList<String>();
		byType_spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, byType_spinner_list);
		byType_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		start_byFloor_spinner = (Spinner) findViewById(R.id.startByFloorSpinner);
		start_byFloor_spinner.setOnItemSelectedListener(this);
		start_byFloor_spinner.setAdapter(byFloor_spinner_adapter);
		
		start_byType_spinner = (Spinner) findViewById(R.id.startByTypeSpinner);
		start_byType_spinner.setOnItemSelectedListener(this);
		start_byType_spinner.setAdapter(byType_spinner_adapter);
		
		end_byFloor_spinner = (Spinner) findViewById(R.id.endByFloorSpinner);
		end_byFloor_spinner.setOnItemSelectedListener(this);
		end_byFloor_spinner.setAdapter(byFloor_spinner_adapter);
		
		end_byType_spinner = (Spinner) findViewById(R.id.endByTypeSpinner);
		end_byType_spinner.setOnItemSelectedListener(this);
		end_byType_spinner.setAdapter(byType_spinner_adapter);
		
		// load spinner and auto-complete list data from database
		appIO.sqlite_async_getData(SQLiteConstants.QUERY_FLOOR_LIST, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, byFloor_spinner_list, byFloor_spinner_adapter, this, AppPrefs.getBrowseBldg(this));
		appIO.sqlite_async_getData(SQLiteConstants.QUERY_TYPE_LIST,	SQLiteConstants.PROGRESS_BAR_INDETERMINATE, byType_spinner_list, byType_spinner_adapter, this, AppPrefs.getBrowseBldg(this));
		appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODES_BY_FLOOR_AND_TYPE, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, start_spinner_list, start_spinner_adapter, this, "", AppPrefs.getBrowseBldg(this), "", "list");
		appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODES_BY_FLOOR_AND_TYPE, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, start_spinner_list_DATA, start_spinner_adapter, this, "", AppPrefs.getBrowseBldg(this), "", "data");
		appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODES_BY_FLOOR_AND_TYPE, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, end_spinner_list, end_spinner_adapter, this, "", AppPrefs.getBrowseBldg(this), "", "list");
		appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODES_BY_FLOOR_AND_TYPE, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, end_spinner_list_DATA, end_spinner_adapter, this, "", AppPrefs.getBrowseBldg(this), "", "data");
		appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODES_ALL,	SQLiteConstants.PROGRESS_BAR_INDETERMINATE,	autoComplete_list, autoComplete_adapter, this, AppPrefs.getBrowseBldg(this));
		appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODES_ALL, SQLiteConstants.PROGRESS_BAR_INDETERMINATE,	autoComplete_list, autoComplete_adapter, this, AppPrefs.getBrowseBldg(this));
		
		// make sure keyboard doesnt auto-popup
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	    
		// determine which tab to display first
		Bundle extras = getIntent().getExtras();
		int tabNum = extras.getInt("tab");
		if (tabNum < 4) {
			tabs.setCurrentTab(tabNum);
		} else {
			// load the map tab, and center it on the current Start or End location
			boolean bStart = extras.getBoolean("start");
			String floorID = extras.getString("floor");
			
			webView.getHandler().sendMessage(webView.getHandler().obtainMessage(webView.HANDLER_SHOW_PROGRESS));
			webView.setZoomLevel(150);
			
			if (bStart) {
				// center on start location
				webView.loadUrl(AppConstants.GLOBAL_WEB_SITE + "?mode=browse&bldg=" + AppPrefs.getBrowseBldg(this) + "&floor=" + floorID + "&center=" + AppPrefs.getStartID(this));
			} else {
				// center on end location
				webView.loadUrl(AppConstants.GLOBAL_WEB_SITE + "?mode=browse&bldg=" + AppPrefs.getBrowseBldg(this) + "&floor=" + floorID + "&center=" + AppPrefs.getEndID(this));
			}
			
			tabs.setCurrentTab(3);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.searchNext:
		case R.id.enterNext:
		case R.id.scanNext:
		case R.id.mapNext:
			launchWebApp();
			
			break;

		case R.id.ScanStartButton:

			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE","QR_CODE_MODE");
			startActivityForResult(intent, 0);

			// Toast.makeText(this, "ScanButton", Toast.LENGTH_LONG)
			// .show();
			break;
		
		case R.id.searchSwitch:
		case R.id.enterSwitch:
		case R.id.scanSwitch:
		case R.id.mapSwitch:
			String swapStartID = AppPrefs.getStartID(this);
			String swapStartLabel = AppPrefs.getStartLabel(this);
			webView.clearFlag(true);	// clear start flag
			webView.clearFlag(false);	// clear end flag
			updateStartEndLocation(true, null, AppPrefs.getEndID(this), AppPrefs.getEndLabel(this));
			updateStartEndLocation(false, null, swapStartID, swapStartLabel);
			break;
			
		case R.id.searchStartText:
		case R.id.enterStartText:
		case R.id.scanStartText:
		case R.id.mapStartText:
			//Toast.makeText(this, "StartText", Toast.LENGTH_LONG).show();
			if (AppPrefs.getStartID(this).length() == 0) break;
			String startFloorID = getFloorIDbyNodeID(AppPrefs.getStartID(this));
			if (startFloorID == null) {
				Toast.makeText(this, "Error - Start location is not in " + AppPrefs.getBrowseBldg(this) + "!",Toast.LENGTH_LONG).show();
				break;
			}
			webView.getHandler().sendMessage(webView.getHandler().obtainMessage(webView.HANDLER_SHOW_PROGRESS));
			webView.setZoomLevel(150);
			webView.loadUrl(AppConstants.GLOBAL_WEB_SITE + "?mode=browse&bldg=" + AppPrefs.getBrowseBldg(this) + "&floor=" + startFloorID + "&center=" + AppPrefs.getStartID(this));
			tabs.setCurrentTab(3);
			break;
			
		case R.id.searchEndText:
		case R.id.enterEndText:
		case R.id.scanEndText:
		case R.id.mapEndText:
			//Toast.makeText(this, "EndText", Toast.LENGTH_LONG).show();
			if (AppPrefs.getEndID(this).length() == 0) break;
			String endFloorID = getFloorIDbyNodeID(AppPrefs.getEndID(this));
			if (endFloorID == null) {
				Toast.makeText(this, "Error - End location is not in " + AppPrefs.getBrowseBldg(this) + "!",Toast.LENGTH_LONG).show();
			}
			webView.getHandler().sendMessage(webView.getHandler().obtainMessage(webView.HANDLER_SHOW_PROGRESS));
			webView.setZoomLevel(150);
			webView.loadUrl(AppConstants.GLOBAL_WEB_SITE + "?mode=browse&bldg=" + AppPrefs.getBrowseBldg(this) + "&floor=" + endFloorID + "&center=" + AppPrefs.getEndID(this));
			tabs.setCurrentTab(3);
			break;
			
		case R.id.mapFloor:
			webView.setZoomLevel(200);
			webView.getHandler().sendMessage(webView.getHandler().obtainMessage(webView.HANDLER_SHOW_PROGRESS));
			webView.loadUrl(AppConstants.GLOBAL_WEB_SITE + "?mode=browse&bldg=" + AppPrefs.getBrowseBldg(this));
			webView.scrollTo(0, 0);
			break;
			
		case R.id.mapPOI:
			Intent intent6 = new Intent(this, AppConstants.CLASS_POIPREF);
			startActivity(intent6);
			break;
		
		case R.id.searchClear:
		case R.id.enterClear:
		case R.id.scanClear:
			showClearDialog();
			break;
		}
	}
				
	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		// When the Activity is first loaded, the spinners will call onItemSelected as
		// they are initialized.  We dont want this, so as a work around, we ignore
		// the first "update"
		switch (parent.getId()) {
		case R.id.startByFloorSpinner:
			if (!bFirst_startByFloorSpinner) {
				//Toast.makeText(this, "startByFloorSpinner, value: " + byFloor_spinner_list.get(position) + ", startByTypeSpinner: " + byType_spinner_list.get(start_byType_spinner.getSelectedItemPosition()), Toast.LENGTH_SHORT).show();
				appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODES_BY_FLOOR_AND_TYPE, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, start_spinner_list, start_spinner_adapter, this, byFloor_spinner_list.get(position), AppPrefs.getBrowseBldg(this), byType_spinner_list.get(start_byType_spinner.getSelectedItemPosition()).toLowerCase());
				appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODES_BY_FLOOR_AND_TYPE, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, start_spinner_list_DATA, start_spinner_adapter, this, byFloor_spinner_list.get(position), AppPrefs.getBrowseBldg(this), byType_spinner_list.get(start_byType_spinner.getSelectedItemPosition()).toLowerCase());
				if (start_spinner.getSelectedItemPosition() > 0) {
					bFirst_startRoomListSpinner = true;
					start_spinner.setSelection(0);
				}
			} else {bFirst_startByFloorSpinner = false;}
			break;
			
		case R.id.startByTypeSpinner:
			if (!bFirst_startByTypeSpinner) {
				//Toast.makeText(this, "startByTypeSpinner, value: " + byType_spinner_list.get(position), Toast.LENGTH_SHORT).show();
				appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODES_BY_FLOOR_AND_TYPE, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, start_spinner_list, start_spinner_adapter, this, byFloor_spinner_list.get(start_byFloor_spinner.getSelectedItemPosition()), AppPrefs.getBrowseBldg(this), byType_spinner_list.get(position).toLowerCase());
				appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODES_BY_FLOOR_AND_TYPE, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, start_spinner_list_DATA, start_spinner_adapter, this, byFloor_spinner_list.get(start_byFloor_spinner.getSelectedItemPosition()), AppPrefs.getBrowseBldg(this), byType_spinner_list.get(position).toLowerCase());
				if (start_spinner.getSelectedItemPosition() > 0) {
					bFirst_startRoomListSpinner = true;
					start_spinner.setSelection(0);
				}
			} else {bFirst_startByTypeSpinner = false;}
			break;
			
		case R.id.startRoomListSpinner:
			if (!bFirst_startRoomListSpinner) {
				//Toast.makeText(this, "startRoomListSpinner, value: " + start_spinner_list.get(position), Toast.LENGTH_SHORT).show();
				updateStartEndLocation(true, start_spinner_list.get(position), null, null);
				
			} else {bFirst_startRoomListSpinner = false;}
			break;
			
		case R.id.endByFloorSpinner:
			if (!bFirst_endByFloorSpinner) {
				//Toast.makeText(this, "endByFloorSpinner, value: " + byFloor_spinner_list.get(position), Toast.LENGTH_SHORT).show();
				appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODES_BY_FLOOR_AND_TYPE, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, end_spinner_list, end_spinner_adapter, this, byFloor_spinner_list.get(position), AppPrefs.getBrowseBldg(this), byType_spinner_list.get(end_byType_spinner.getSelectedItemPosition()).toLowerCase(), "list");
				appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODES_BY_FLOOR_AND_TYPE, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, end_spinner_list_DATA, end_spinner_adapter, this, byFloor_spinner_list.get(position), AppPrefs.getBrowseBldg(this), byType_spinner_list.get(end_byType_spinner.getSelectedItemPosition()).toLowerCase(), "data");
				if (end_spinner.getSelectedItemPosition() > 0) {
					bFirst_endRoomListSpinner = true;
					end_spinner.setSelection(0);
				}
			} else {bFirst_endByFloorSpinner = false;}
			break;
			
		case R.id.endByTypeSpinner:
			if (!bFirst_endByTypeSpinner) {
				//Toast.makeText(this, "endByTypeSpinner, value: " + byType_spinner_list.get(position), Toast.LENGTH_SHORT).show();
				appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODES_BY_FLOOR_AND_TYPE, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, end_spinner_list, end_spinner_adapter, this, byFloor_spinner_list.get(end_byFloor_spinner.getSelectedItemPosition()), AppPrefs.getBrowseBldg(this), byType_spinner_list.get(position).toLowerCase(), "list");
				appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODES_BY_FLOOR_AND_TYPE, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, end_spinner_list_DATA, end_spinner_adapter, this, byFloor_spinner_list.get(end_byFloor_spinner.getSelectedItemPosition()), AppPrefs.getBrowseBldg(this), byType_spinner_list.get(position).toLowerCase(), "data");
				if (end_spinner.getSelectedItemPosition() > 0) {
					bFirst_endRoomListSpinner = true;
					end_spinner.setSelection(0);
				}
			} else {bFirst_endByTypeSpinner = false;}
			break;
			
		case R.id.endRoomListSpinner:
			if (!bFirst_endRoomListSpinner) {
				//Toast.makeText(this, "endRoomListSpinner, value: " + end_spinner_list.get(position), Toast.LENGTH_SHORT).show();
				updateStartEndLocation(false, end_spinner_list.get(position), null, null);
				
			} else {bFirst_endRoomListSpinner = false;}
			break;

		default:
			break;
	
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "onNothingSelected", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		AppOptionsMenu.buildOptionMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return AppOptionsMenu.handleMenuSelection(item, this, AppPrefs.getStartID(this), AppPrefs.getEndID(this), this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Log.i("SCAN","scan: " + contents);
				
				String[] fields = contents.split(",");
				if (fields[0].equals("1")) {
					// start/end input scan
					showStartEndDialog(fields[1]);
					
				} else if (fields[0].equals("2")) {
					// route input scan
					boolean bGoForIt = true;
					
					// verify start location
					ArrayList<NodeInfo> results = verifyNodeCheck(fields[1]);
					if (results != null) {
						updateStartEndLocation(true, null, results.get(0).id, results.get(0).label);
					} else {
						bGoForIt = false;
					}
					
					// verify end location
					results = verifyNodeCheck(fields[2]);
					if (results != null) {
						updateStartEndLocation(false, null, results.get(0).id, results.get(0).label);
					} else {
						bGoForIt = false;
					}
					
					if (bGoForIt) {
						launchWebApp();
					} else {
						Toast.makeText(this, "Scan error - \"" + contents + "\" was not recognized as a valid scan.", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(this, "Scan error - \"" + contents + "\" was not recognized as a valid scan.", Toast.LENGTH_LONG).show();
				}
				
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// Handle cancel
				Toast.makeText(this, "Scan Canceled, or Scan Error", Toast.LENGTH_LONG).show();
			}
		}
	}

	public void updateStartEndLocation(boolean bStart, NodeInfo nodeinfo, String myID, String myLabel) {
		//Toast.makeText(this, "updateStartEndLocation, bStart: " + bStart + ", location: " + myLocation, Toast.LENGTH_SHORT).show();
		String id = myID;
		String label = myLabel;
		if (nodeinfo != null) {
			id = nodeinfo.id;
			label = nodeinfo.label;
		} else {
			id = myID;
			label = myLabel;
		}
		Log.i("TAG", "id: " + id + ", label: " + label);
		if (bStart) {
			// update Start location
			searchStartText.setText("Start: " + label);
			enterStartText.setText("Start: " + label);
			scanStartText.setText("Start: " + label);
			mapStartText.setText("Start: " + label);
			AppPrefs.setStartID(id, this);
			AppPrefs.setStartLabel(label, this);
		} else {
			// update End location
			searchEndText.setText("End: " + label);
			enterEndText.setText("End: " + label);
			scanEndText.setText("End: " + label);
			mapEndText.setText("End: " + label);
			AppPrefs.setEndID(id, this);
			AppPrefs.setEndLabel(label, this);
		}
		
		// update the webview
		webView.getHandler().sendMessage(webView.getHandler().obtainMessage(webView.HANDLER_UPDATE_MAP_PREFS));
	}
	
	public void changeTabs(int tabNum) {
		tabs.setCurrentTab(tabNum);
	}
	
	public void setMaxFling(int maxFling) {
		this.maxFling = maxFling;
	}
	
	private ArrayList<NodeInfo> verifyNodeCheck (String nodeID) {
		Future<ArrayList<?>> future = appIO.sqlite_async_getData(SQLiteConstants.QUERY_NODE_CHECK, SQLiteConstants.PROGRESS_BAR_INDETERMINATE, null, null, this, AppPrefs.getBrowseBldg(this), nodeID);
		try {
			ArrayList<NodeInfo> results = (ArrayList<NodeInfo>) future.get();
			if (results != null && results.size() > 0) {
				return results;
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
	
	private boolean launchWebApp() {
		// verify start and end locations before opening route map
			if (AppPrefs.getStartID(this).length() == 0) {
				// start location is blank
				Toast.makeText(this, "Please select a start location!",Toast.LENGTH_LONG).show();
				return false;
			}
			
			if (AppPrefs.getEndID(this).length() == 0) {
				// end location is blank
				Toast.makeText(this, "Please select a end location!",Toast.LENGTH_LONG).show();
				return false;
			}			
			
			if (AppPrefs.getStartID(this).equals(AppPrefs.getEndID(this))) {
				// start and end locations are identical - issue error
				Toast.makeText(this, "Start and End locations are the same! Please change.",Toast.LENGTH_LONG).show();				
				return false;
			} 

			if (verifyNodeCheck(AppPrefs.getStartID(this)) == null) {
				// start location does not exist in currently selected building (in app settings)
				Toast.makeText(this, "Error - Start location is not in " + AppPrefs.getBrowseBldg(this) + "!",Toast.LENGTH_LONG).show();
				return false;
			}
			
			if (verifyNodeCheck(AppPrefs.getEndID(this)) == null) {
				// end location does not exist in currently selected building (in app settings)
				Toast.makeText(this, "Error - End location is not in " + AppPrefs.getBrowseBldg(this) + "!",Toast.LENGTH_LONG).show();
				return false;
			}
			
			// we are good to go
			Intent intent5 = new Intent(this, AppConstants.CLASS_WEBVIEW);
			startActivity(intent5);
			
			return true;
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
	
	private void showStartEndDialog(final String nodeID){
		final Context context = this;
		Log.i("SCAN","showStartEndDialog: " + nodeID);
		final ArrayList<NodeInfo> results = verifyNodeCheck(nodeID);
		if (results != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Start or End Location")
				.setMessage("Do you want to set \"" + results.get(0).label + "\" as the start or end location?")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(true)			
				.setPositiveButton("Start", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					// update start location
					updateStartEndLocation(true, null, results.get(0).id, results.get(0).label);
					
					// switch to tab view, and center map on start location
					if (AppPrefs.getStartID(context).length() != 0) {
						String startFloorID = getFloorIDbyNodeID(AppPrefs.getStartID(context));
						if (startFloorID == null) {
							Toast.makeText(context, "Error - location is not in " + AppPrefs.getBrowseBldg(context) + "!",Toast.LENGTH_LONG).show();
						} else {
							webView.getHandler().sendMessage(webView.getHandler().obtainMessage(webView.HANDLER_SHOW_PROGRESS));
							webView.setZoomLevel(150);
							webView.loadUrl(AppConstants.GLOBAL_WEB_SITE + "?mode=browse&bldg=" + AppPrefs.getBrowseBldg(context) + "&floor=" + startFloorID + "&center=" + AppPrefs.getStartID(context));
							tabs.setCurrentTab(3);
						}
					}
				}})
				.setNeutralButton("End", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					// update end location
					updateStartEndLocation(false, null, results.get(0).id, results.get(0).label);
					
					// switch to tab view, and center map on end location
					if (AppPrefs.getEndID(context).length() != 0) {
						String endFloorID = getFloorIDbyNodeID(AppPrefs.getEndID(context));
						if (endFloorID == null) {
							Toast.makeText(context, "Error - location is not in " + AppPrefs.getBrowseBldg(context) + "!",Toast.LENGTH_LONG).show();
						} else {
							webView.getHandler().sendMessage(webView.getHandler().obtainMessage(webView.HANDLER_SHOW_PROGRESS));
							webView.setZoomLevel(150);
							webView.loadUrl(AppConstants.GLOBAL_WEB_SITE + "?mode=browse&bldg=" + AppPrefs.getBrowseBldg(context) + "&floor=" + endFloorID + "&center=" + AppPrefs.getEndID(context));
							tabs.setCurrentTab(3);
						}
					}
				}})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					dialog.cancel();
				}
				}).show();
		} else {
			Toast.makeText(context, "Scan Error - the scanned location: " + nodeID + ", is not in " + AppPrefs.getBrowseBldg(context),Toast.LENGTH_LONG).show();
		}
		

	}
	
	private void showClearDialog(){
		final Context context = this;
		Log.i("CLEAR","showClearDialog");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Clear Locations")
			.setMessage("Do you want clear both the Start and End locations?")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setCancelable(true)			
			.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				// clear locations
				updateStartEndLocation(true, null, "", "");
				updateStartEndLocation(false, null, "", "");
				start_autoComplete.setText("");
				end_autoComplete.setText("");
				start_byFloor_spinner.setSelection(0);
				start_byType_spinner.setSelection(0);
				//start_spinner.setSelection(0);	// this is done when byFloor and byType spinners are reset to 0;
				end_byFloor_spinner.setSelection(0);
				end_byType_spinner.setSelection(0);
				//end_spinner.setSelection(0);
			}})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				dialog.cancel();
			}
			}).show();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if (myGesture.onTouchEvent(event)) 
	        return true; 
	    else 
	        return false;
    }
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	// fixes issue with fling/swipe and scrollview
	@Override 
    public boolean dispatchTouchEvent(MotionEvent ev){ 
        super.dispatchTouchEvent(ev); 
        return myGesture.onTouchEvent(ev); 
    } 
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,	float velocityY) {
	try {
		//do not do anything if the swipe does not reach a certain length of distance
		if (Math.abs(e1.getY() - e2.getY()) > AppConstants.SWIPE_MAX_OFF_PATH) return false;
		 
		// right to left swipe
		if(e1.getX() - e2.getX() > AppConstants.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > AppConstants.SWIPE_THRESHOLD_VELOCITY) {
			//Toast.makeText(this, "RIGHT TO LEFT", Toast.LENGTH_SHORT).show();
			numTab = tabs.getTabWidget().getTabCount();
			currentTab = tabs.getCurrentTab();
			
			if (currentTab < numTab)
		    tabs.setCurrentTab(currentTab + 1);
		}
		
		// left to right swipe
		else if (e2.getX() - e1.getX() > AppConstants.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > AppConstants.SWIPE_THRESHOLD_VELOCITY) {
			//Toast.makeText(this, "LEFT TO RIGHT", Toast.LENGTH_SHORT).show();
			currentTab = tabs.getCurrentTab();
			
			// turn off fling when on map tab (currentTab < 3)
			if (currentTab > 0 && currentTab < maxFling)
			tabs.setCurrentTab(currentTab - 1);
		}
	} catch (Exception e) {
	    // nothing
	}
	return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
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
		// webView.loadUrl(AppConstants.GLOBAL_WEB_SITE + "?mode=browse&bldg=" +
		// AppPrefs.getBrowseBldg(this));
		
		updateStartEndLocation(true, null, AppPrefs.getStartID(this), AppPrefs.getStartLabel(this));
		updateStartEndLocation(false, null, AppPrefs.getEndID(this), AppPrefs.getEndLabel(this));
		webView.getHandler().sendMessage(webView.getHandler().obtainMessage(webView.HANDLER_UPDATE_MAP_PREFS));
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
	}

			
}