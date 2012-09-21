package com.cs460402.activities;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs460402.app.AppConstants;
import com.cs460402.app.async_core.AsyncConstants;
import com.cs460402.app.io.AppIO;
import com.cs460402.app.io.NodeInfo;
import com.cs460402.app.io.sqlite.SQLiteConstants;

public class ShareRouteActivity extends Activity implements OnClickListener {

	private EditText shareEdit;
	private TextView shareStartText;
	private TextView shareEndText;
	private ImageView shareSwitch;
	private Button shareGoButton;
	private ImageButton smsButton;
	private ImageButton emailButton;
	
	private AppIO appIO;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.shareroute);
		
	    shareEdit = (EditText) findViewById(R.id.shareEdit);
	    (shareStartText = (TextView) findViewById(R.id.shareStartText)).setOnClickListener(this);
	    (shareEndText = (TextView) findViewById(R.id.shareEndText)).setOnClickListener(this);
	    
	    (shareSwitch = (ImageView) findViewById(R.id.shareSwitch)).setOnClickListener(this);
	    (shareGoButton = (Button) findViewById(R.id.shareGo)).setOnClickListener(this);
	    (smsButton = (ImageButton) findViewById(R.id.shareSMS)).setOnClickListener(this);
	    (emailButton = (ImageButton) findViewById(R.id.shareEmail)).setOnClickListener(this);
	    
	    shareStartText.setText("Start: " + AppPrefs.getStartLabel(this));
	    shareEndText.setText("End: " + AppPrefs.getEndLabel(this));
	    
	    appIO = new AppIO(AsyncConstants.DEFAULT_THREAD_POOL_SIZE);
		
        // make sure keyboard doesnt auto-popup
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shareSwitch:
			String swapStartID = AppPrefs.getStartID(this);
			String swapStartLabel = AppPrefs.getStartLabel(this);
			AppPrefs.setStartID(AppPrefs.getEndID(this), this);
			AppPrefs.setStartLabel(AppPrefs.getEndLabel(this), this);
			shareStartText.setText("Start: " + AppPrefs.getEndLabel(this));
			AppPrefs.setEndID(swapStartID, this);
			AppPrefs.setEndLabel(swapStartLabel, this);
			shareEndText.setText("End: " + swapStartLabel);
			break;
		case R.id.shareGo:
			Log.i("SQLITE","WTF");
			shareGo();
			break;			
		case R.id.shareSMS:
			shareBySMS();
			break;
		case R.id.shareEmail:
			shareByEmail();
			break;
		case R.id.shareStartText:
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
		case R.id.shareEndText:
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
	
	private void shareGo() {
		if (shareEdit.getText().toString().length() != 0) {
			String[] fields = shareEdit.getText().toString().split(",");
			if (fields.length > 1) {
				// two nodeID have been entered

				Log.i("SQLITE","shareGo - fields[0]: " + fields[0] + ", fields[1]: " + fields[1]); 
				boolean bGoForIt = true;
				
				// verify start location
				ArrayList<NodeInfo>results = verifyNodeCheck(fields[0].substring(fields[0].indexOf(":")+1).trim());
				if (results != null) {
					AppPrefs.setStartID(results.get(0).id, this);
					AppPrefs.setStartLabel(results.get(0).label, this);
					shareStartText.setText(results.get(0).label);
				} else {
					bGoForIt = false;
				}
				
				// verify end location
				results = verifyNodeCheck(fields[1].substring(fields[1].indexOf(":")+1).trim());
				if (results != null) {
					AppPrefs.setEndID(results.get(0).id, this);
					AppPrefs.setEndLabel(results.get(0).label, this);
					shareEndText.setText(results.get(0).label);
				} else {
					bGoForIt = false;
				}
				
				if (bGoForIt) {
					launchWebApp();
				} else {
					Toast.makeText(this, "The entered text \"" + shareEdit.getText().toString() + "\" was not recognized as a valid route.", Toast.LENGTH_LONG).show();
				}
				
			} else {
				// one nodeID has been entered
				//results = verifyNodeCheck(fields[0].substring(fields[0].indexOf(":")).trim());
				Log.i("SQLITE","one node, fields[0]: " + fields[0]);
				if (fields[0].indexOf(":") < 0) {
					showStartEndDialog(fields[0]);
				} else {
					showStartEndDialog(fields[0].substring(fields[0].indexOf(":")+1).trim());
				}
			}
			
		} else {
			Toast.makeText(this, "No text has been entered!",Toast.LENGTH_LONG).show();
		}
		
	}
	
	private boolean shareBySMS() {
		String startID = AppPrefs.getStartID(this);
		String endID = AppPrefs.getEndID(this);
		
		// if both start and end locations are empty, do nothing
		if (startID.length() == 0 && endID.length() == 0) {
			Toast.makeText(this, "Action canceled - both start and end locations are blank!", Toast.LENGTH_LONG).show();
			return false;
		}
		
		// if both start and end locations have something, send it as a route
		if (startID.length() > 0 && endID.length() > 0) {
			Uri uri2 = Uri.parse("sms:" + AppPrefs.getDefaultPhone(this));
			Intent intent4 = new Intent(Intent.ACTION_VIEW, uri2);
			intent4.putExtra("sms_body", "start: " + AppPrefs.getStartID(this) + ", end: " + AppPrefs.getEndID(this));
			startActivity(intent4);
			return true;
		}
		
		// if we get here, only one of the two have something, send it as a node
		Uri uri3 = Uri.parse("sms:" + AppPrefs.getDefaultPhone(this));
		Intent intent8 = new Intent(Intent.ACTION_VIEW, uri3);
		
		if (startID.length() > 0) {
			intent8.putExtra("sms_body", "location ID: " + AppPrefs.getStartID(this));
		} else {
			intent8.putExtra("sms_body", "location ID: " + AppPrefs.getEndID(this));
		}
		startActivity(intent8);
		return true;
	}
	
	private boolean shareByEmail() {
		String startID = AppPrefs.getStartID(this);
		String endID = AppPrefs.getEndID(this);
		
		// if both start and end locations are empty, do nothing
		if (startID.length() == 0 && endID.length() == 0) {
			Toast.makeText(this, "Action canceled - both start and end locations are blank!", Toast.LENGTH_LONG).show();
			return false;
		}
		
		// if both start and end locations have something, send it as a route
		if (startID.length() > 0 && endID.length() > 0) {
			Intent intent9 = new Intent(Intent.ACTION_SEND);
			intent9.setType("plain/text");
			intent9.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{AppPrefs.getDefaultEmail(this)});
			intent9.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hostpital Navigator - Route");
			intent9.putExtra(android.content.Intent.EXTRA_TEXT, "" 
						+ "Hi!\n"
						+ "This message was sent from Hospital Navigator, an indoor navigation application on Android!\n"
						+ "\n"
						+ "A route has been shared with you.  To view it, Please copy and paste the code below into the text "
						+ "field in the 'Share Route' application screen of Hospital Navigator:\n"
						+ "\n"
						+ "\nCode:\n"
						+ "start: " + startID + ", end: " + endID);  
			startActivity(intent9);
			return true;
		}
		
		// if we get here, only one of the two have something, send it as a node
		Intent intent3 = new Intent(Intent.ACTION_SEND);
		intent3.setType("plain/text");
		intent3.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{AppPrefs.getDefaultEmail(this)});
		intent3.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hostpital Navigator - Location");
		String msg = "";
		msg = 	  "Hi!\n"
				+ "This message was sent from Hospital Navigator, an indoor navigation application on Android!\n"
				+ "\n"
				+ "A location has been shared with you.  To view it, Please copy and paste the code below into the text "
				+ "field in the 'Share Route' application screen of Hospital Navigator:\n"
				+ "\n"
				+ "\nCode:\n";  		
		if (startID.length() > 0) {
			msg = msg + "Location ID: " + startID;
		} else {
			msg = msg + "Location ID: " + endID;
		}
		intent3.putExtra(android.content.Intent.EXTRA_TEXT, msg);
		startActivity(intent3);
		return true;
	}
	
	
	private ArrayList<NodeInfo> verifyNodeCheck (String nodeID) {
		Log.i("SQLITE","verifyNodeCheck - nodeID: " + nodeID);
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
					AppPrefs.setStartID(results.get(0).id, context);
					AppPrefs.setStartLabel(results.get(0).label, context);
					shareStartText.setText("Start: " + results.get(0).label);
					
					// switch to tab view, and center map on start location
					if (AppPrefs.getStartID(context).length() != 0) {
						String startFloorID = getFloorIDbyNodeID(AppPrefs.getStartID(context));
						if (startFloorID == null) {
							Toast.makeText(context, "Error - location is not in " + AppPrefs.getBrowseBldg(context) + "!",Toast.LENGTH_LONG).show();
						} else {
							Intent map = new Intent(context, AppConstants.CLASS_ENTRY);
							map.putExtra("tab", 4);		// there is no tab 4, but we use this to tell the Activity to load the browsing map and center it on a node 
							map.putExtra("floor", startFloorID);
							map.putExtra("start", true);
							startActivity(map);
						}
					}
					
				}})
				.setNeutralButton("End", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					// update end location
					AppPrefs.setEndID(results.get(0).id, context);
					AppPrefs.setEndLabel(results.get(0).label, context);
					shareEndText.setText("End: " + results.get(0).label);
					
					// switch to tab view, and center map on end location
					if (AppPrefs.getEndID(context).length() != 0) {
						String endFloorID = getFloorIDbyNodeID(AppPrefs.getEndID(context));
						if (endFloorID == null) {
							Toast.makeText(context, "Error - location is not in " + AppPrefs.getBrowseBldg(context) + "!",Toast.LENGTH_LONG).show();
						} else {
							Intent map2 = new Intent(context, AppConstants.CLASS_ENTRY);
							map2.putExtra("tab", 4);		// there is no tab 4, but we use this to tell the Activity to load the browsing map and center it on a node 
							map2.putExtra("floor", endFloorID);
							map2.putExtra("start", false);
							startActivity(map2);
						}
					}
					
				}})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					dialog.cancel();
				}
				}).show();
		} else {
			Toast.makeText(context, "Sorry, the entered location: " + nodeID + ", is not in " + AppPrefs.getBrowseBldg(context),Toast.LENGTH_LONG).show();
		}
		

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
