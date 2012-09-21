package com.cs460402.app.io;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cs460402.app.async_core.TaskBase;
import com.cs460402.app.async_core.UIHandler;
import com.cs460402.app.async_core.UpdateElementBase;
import com.cs460402.app.io.extdb.ExtDbConstants;
import com.cs460402.app.io.sqlite.SQLiteConstants;
import com.cs460402.app.io.sqlite.SQLiteHelper;

public class SQLiteIOTask<E1, E2> extends TaskBase<ArrayList<?>> {

	private final SQLiteHelper sqliteHelper;
	private final int querytype;
	private final String[] params;
	private final Context context;
	private final E1 element1; // element to be updated - i.e. ArrayList,
								// TextView, etc...
	private final E2 element2; // element to be updated - i.e. ArrayList,
								// TextView, etc...

	/* Current (e.g. UI) Thread */
	public SQLiteIOTask(Activity activity, UIHandler handlerUI, int querytype, String[] params, E1 element1, E2 element2) {
		super(handlerUI);

		this.context = activity;
		this.querytype = querytype;
		this.params = params;
		this.element1 = element1;
		this.element2 = element2;

		// setup helper
		this.sqliteHelper = new SQLiteHelper(activity);
	}

	/* Separate Thread */
	public ArrayList<?> call() throws Exception {
		toggleProgress();

		final ArrayList<NodeInfo> results = new ArrayList<NodeInfo>();

		switch (querytype) {
		case SQLiteConstants.QUERY_SYNC_DB:
			boolean syncResults = query_SynchDB();
			
			if (this.future == null || !this.future.isCancelled()) {
				if (syncResults) {
					toggleProgress();
					handlerUI.post(new Runnable() {
						public void run() {
							Toast.makeText(context, "Database sync completed!",
									Toast.LENGTH_SHORT).show();
						}
					});
				} else {
					toggleProgress();
					handlerUI.post(new Runnable() {
						public void run() {
							Toast.makeText(context, "Database sync failed!",
									Toast.LENGTH_SHORT).show();
						}
					});
				}
			}			
			return results;

		case SQLiteConstants.QUERY_LOAD_APP:
			query_SynchDB();

			toggleProgress();
			handlerUI.post((Runnable) element1);
			return null;
		case SQLiteConstants.QUERY_ALLDATA:
			results.addAll(query_AllData());
			break;

		case SQLiteConstants.QUERY_NODES_ALL:
			results.addAll(query_NodesAll());
			break;

		case SQLiteConstants.QUERY_NODE_CHECK:
			results.addAll(query_NodeCheck());
			break;

		case SQLiteConstants.QUERY_NODES_BY_FLOOR_AND_TYPE:
			results.addAll(query_NodesByFloorAndType());
			break;

		case SQLiteConstants.QUERY_FLOOR_LIST:
			ArrayList<String> floor_results = new ArrayList<String>();
			floor_results.addAll(query_FloorList());
			
			if (element1 != null && (this.future == null || !this.future.isCancelled())) {
				handlerUI.post(new UpdateElement(element1, element2, floor_results));
			}
			return floor_results;
			
		case SQLiteConstants.QUERY_TYPE_LIST:
			ArrayList<String> type_tresults = new ArrayList<String>();
			type_tresults.addAll(query_TypeList());
			
			if (element1 != null && (this.future == null || !this.future.isCancelled())) {
				handlerUI.post(new UpdateElement(element1, element2, type_tresults));
			}
			return type_tresults;

		case SQLiteConstants.QUERY_FLOORID_BY_NODEID:
			results.addAll(query_FloorIDbyNodeID());
			break;
			
		case SQLiteConstants.QUERY_DISPLAY_ALLDATA:
			results.addAll(query_DisplayAllData());
			break;

		default:
			// an unknown querytype has been passed, fail.
			toggleProgress();
			return null;
		}

		// post results to UI thread
		if (element1 != null && (this.future == null || !this.future.isCancelled())) {
			handlerUI.post(new UpdateElement(element1, element2, results));
		}

		toggleProgress();
		return results;
	}

	private boolean query_SynchDB() {
		Log.i("SQLITE", "sync db");

		SQLiteDatabase db = null;
		// Cursor cursor = null;
		//final ArrayList<NodeInfo> results = new ArrayList<NodeInfo>();

		// obtain data from external database
		ExtDatabaseIOTask dbTask = new ExtDatabaseIOTask(context, handlerUI, ExtDbConstants.QUERY_ALLDATA, null);
		try {
			sqliteHelper.setTableData(dbTask.call());
			// ArrayList<String> temp = dbTask.call();
			// Log.i("SQLITE","dbTask result.size: " + temp.size());
			// sqliteHelper.setTableData(temp);
		} catch (Exception e) {
			// TODO error handling of dbTask failure
			Log.e("SQLITE", "error obtaining external data: " + e.toString());	
			//results.add(SQLiteConstants.RESULT_FAILED);
			return false;
		}

		// open/create the SQLite database
		if (this.future == null || !this.future.isCancelled()) {
			try {
				db = sqliteHelper.getWritableDatabase();
				Log.i("SQLITE", "db version: " + db.getVersion());
				sqliteHelper.onCreate(db);
				Log.i("SQLITE", "db version2: " + db.getVersion());
			} catch (SQLException e) {
				// TODO error handling
				if (db != null) {
					db.close();
				}
				Log.e("SQLITE", "error creating database: " + e.toString());	
				//results.add(SQLiteConstants.RESULT_FAILED);
				return false;
			}
		}

		// close the database connection
		// if (cursor != null) {cursor.close();}
		if (db != null) {
			db.close();
		}

		if (this.future == null || !this.future.isCancelled()) {
			Log.i("SQLITE", "sync success");
			return true;
		} else {
			return false;
		}
	}

	private ArrayList<NodeInfo> query_AllData() {
		Log.i("SQLITE", "all data");

		SQLiteDatabase db = null;
		Cursor cursor = null;
		final ArrayList<NodeInfo> results = new ArrayList<NodeInfo>();

		// open the SQLite database
		try {
			db = sqliteHelper.getWritableDatabase();
		} catch (SQLException e) {
			// TODO error handling
			if (db != null) {
				db.close();
			}
			//results.add(SQLiteConstants.RESULT_FAILED);
			return null;
		}

		// obtain data from sqlite database
		cursor = db.query(SQLiteConstants.TABLE_NAME, // table
				SQLiteConstants.ALL_COLUMNS, // columns
				null, // where clause
				null, // selection args
				null, // groupBy
				null, // having
				SQLiteConstants.KEY_NODE_ID // orderBy
				);

		// create a comma delimited ArrayList<String> from cursor results
		if (this.future == null || !this.future.isCancelled()) {
			while (cursor.moveToNext()) {
				results.add(new NodeInfo(
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_ID)), 
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_LABEL)),
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_TYPE)),
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_PHOTO)),
						Integer.parseInt(cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_X))),
						Integer.parseInt(cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_Y))),
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_IS_CONNECTOR)).equals("1"),
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_IS_POI)).equals("1"),
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_POI_Img)),
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_BUILDING_ID)),
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_BUILDING_NAME)),
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_FLOOR_ID)),
						Integer.parseInt(cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_FLOOR_LEVEL))),
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_FLOOR_MAP)),
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NEIGHBOR_NODE)),
						Integer.parseInt(cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NEIGHBOR_DISTANCE))
						)));						
			}
		}

		// close the database connection, and return results
		if (cursor != null) {
			cursor.close();
		}
		if (db != null) {
			db.close();
		}

		if (this.future == null || !this.future.isCancelled()) {
			Log.i("SQLITE", "AllData - results.size: " + results.size());
			return results;
		} else {
			return null;
		}
	}

	private ArrayList<NodeInfo> query_NodesAll() {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		final ArrayList<NodeInfo> results = new ArrayList<NodeInfo>();

		// open the SQLite database
		try {
			db = sqliteHelper.getWritableDatabase();
		} catch (SQLException e) {
			// TODO error handling
			if (db != null) {
				db.close();
			}
			//results.add(SQLiteConstants.RESULT_FAILED);
			return null;
		}

		// obtain data from sqlite database
		cursor = db.query(true, // distinct
				SQLiteConstants.TABLE_NAME, // table name
				new String[] { SQLiteConstants.KEY_NODE_ID,	SQLiteConstants.KEY_NODE_LABEL }, // table columns returned
				SQLiteConstants.KEY_BUILDING_ID + "='" + params[0] + "'", // where clause
				null, // selection args
				null, // groupBy
				null, // having
				SQLiteConstants.KEY_NODE_LABEL, // orderBy
				null // limit
				);

		// create an ArrayList<String> from cursor results
		if (this.future == null || !this.future.isCancelled()) {
			while (cursor.moveToNext()) {
				results.add(new NodeInfo(
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_ID)),
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_LABEL))
						));
			}
		}

		// close the database connection, and return results
		if (cursor != null) {
			cursor.close();
		}
		if (db != null) {
			db.close();
		}

		if (this.future == null || !this.future.isCancelled()) {
			Log.i("SQLITE", "query_NodesAll - results.size: " + results.size());
			return results;
		} else {
			return null;
		}
	}

	private ArrayList<NodeInfo> query_NodesByFloorAndType() {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		final ArrayList<NodeInfo> results = new ArrayList<NodeInfo>();

		// open the SQLite database
		try {
			db = sqliteHelper.getWritableDatabase();
		} catch (SQLException e) {
			// TODO error handling
			if (db != null) {
				db.close();
			}
			//results.add(SQLiteConstants.RESULT_FAILED);
			return null;
		}

		// obtain data from sqlite database
		// params[0] == floorID
		// params[1] == buildingID
		// params[2] == type		
		String whereSQL = "";
		if (params[0].length() > 0) {
			whereSQL = SQLiteConstants.KEY_FLOOR_LEVEL + "=" + params[0] + " AND ";
		}
		if (params[2].length() > 0) {
			whereSQL = whereSQL + SQLiteConstants.KEY_NODE_TYPE + "='" + params[2] + "' AND ";
		}
		
		Log.i("SQLITE","floorID: " + params[0] + ", buildingID: " + params[1] + ", type: " + params[2]);
		cursor = db.query(true, // distinct
				SQLiteConstants.TABLE_NAME, // table name
				new String[] { SQLiteConstants.KEY_NODE_ID,	SQLiteConstants.KEY_NODE_LABEL }, // table columns returned 
				whereSQL + SQLiteConstants.KEY_BUILDING_ID + "='" + params[1] + "'", // where clause
				null, // selection args
				null, // groupBy
				null, // having
				SQLiteConstants.KEY_NODE_LABEL, // orderBy
				null // limit
				);

		// create an ArrayList<String> from cursor results
		if (this.future == null || !this.future.isCancelled()) {
			// always have a blank entry as first item
			results.add(new NodeInfo("",""));
			while (cursor.moveToNext()) {
				results.add(new NodeInfo(
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_ID)),
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_LABEL))
						));
			}
		}

		// close the database connection, and return results
		if (cursor != null) {
			cursor.close();
		}
		if (db != null) {
			db.close();
		}

		if (this.future == null || !this.future.isCancelled()) {
			Log.i("SQLITE",	"query_NodesByFloorAndType - results.size: " + results.size());
			return results;
		} else {
			return null;
		}
	}

	private ArrayList<String> query_FloorList() {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		final ArrayList<String> results = new ArrayList<String>();

		// open the SQLite database
		try {
			db = sqliteHelper.getWritableDatabase();
		} catch (SQLException e) {
			// TODO error handling
			if (db != null) {
				db.close();
			}
			//results.add(SQLiteConstants.RESULT_FAILED);
			return null;
		}

		// obtain data from sqlite database
		// params[0] = buildingID
		cursor = db.query(true, // distinct
				SQLiteConstants.TABLE_NAME, // table name
				new String[] { SQLiteConstants.KEY_FLOOR_LEVEL }, // table columns returned
				SQLiteConstants.KEY_BUILDING_ID + "='" + params[0] + "'", // where clause
				null, // selection args
				null, // groupBy
				null, // having
				SQLiteConstants.KEY_FLOOR_LEVEL, // orderBy
				null // limit
				);

		// create an ArrayList<String> from cursor results
		results.add("");
		if (this.future == null || !this.future.isCancelled()) {
			while (cursor.moveToNext()) {
				results.add(cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_FLOOR_LEVEL)));
			}
		}

		// close the database connection, and return results
		if (cursor != null) {
			cursor.close();
		}
		if (db != null) {
			db.close();
		}

		if (this.future == null || !this.future.isCancelled()) {
			Log.i("SQLITE",	"query_FloorList - results.size: " + results.size());
			return results;
		} else {
			return null;
		}
	}

	private ArrayList<String> query_TypeList() {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		final ArrayList<String> results = new ArrayList<String>();

		// open the SQLite database
		try {
			db = sqliteHelper.getWritableDatabase();
		} catch (SQLException e) {
			// TODO error handling
			if (db != null) {
				db.close();
			}
			//results.add(SQLiteConstants.RESULT_FAILED);
			return null;
		}

		// obtain data from sqlite database
		// params[0] = buildingID
		cursor = db.query(true, // distinct
				SQLiteConstants.TABLE_NAME, // table name
				new String[] { SQLiteConstants.KEY_NODE_TYPE }, // table columns returned
				"(" + SQLiteConstants.KEY_NODE_TYPE + " NOT LIKE 'hall%') AND " + SQLiteConstants.KEY_BUILDING_ID + "='" + params[0] + "'", // where clause
				null, // selection args
				null, // groupBy
				null, // having
				SQLiteConstants.KEY_NODE_TYPE, // orderBy
				null // limit
				);

		// create an ArrayList<String> from cursor results
		results.add("");
		if (this.future == null || !this.future.isCancelled()) {
			while (cursor.moveToNext()) {
				results.add(capitalize(cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_TYPE))));
			}
		}

		// close the database connection, and return results
		if (cursor != null) {
			cursor.close();
		}
		if (db != null) {
			db.close();
		}

		if (this.future == null || !this.future.isCancelled()) {
			Log.i("SQLITE",	"query_TypeList - results.size: " + results.size());
			return results;
		} else {
			return null;
		}
	}
	
	private ArrayList<NodeInfo> query_NodeCheck() {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		final ArrayList<NodeInfo> results = new ArrayList<NodeInfo>();

		// open the SQLite database
		try {
			db = sqliteHelper.getWritableDatabase();
		} catch (SQLException e) {
			// TODO error handling
			if (db != null) {
				db.close();
			}
			//results.add(SQLiteConstants.RESULT_FAILED);
			return null;
		}

		// obtain data from sqlite database
		// params[0] == buildingID
		// params[1] == nodeID
		cursor = db.query(true, // distinct
				SQLiteConstants.TABLE_NAME, // table name
				new String[] { SQLiteConstants.KEY_NODE_ID,	SQLiteConstants.KEY_NODE_LABEL }, // table columns returned
				SQLiteConstants.KEY_BUILDING_ID + "='" + params[0] + "' AND " + SQLiteConstants.KEY_NODE_ID + "='" + params[1] + "'", // where clause
				null, // selection args
				null, // groupBy
				null, // having
				SQLiteConstants.KEY_NODE_ID, // orderBy
				null // limit
				);

		// create an ArrayList<String> from cursor results
		if (this.future == null || !this.future.isCancelled()) {
			while (cursor.moveToNext()) {
				results.add(new NodeInfo(
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_ID)),
						cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_NODE_LABEL))
						));
			}
		}

		// close the database connection, and return results
		if (cursor != null) {
			cursor.close();
		}
		if (db != null) {
			db.close();
		}

		if (this.future == null || !this.future.isCancelled()) {
			Log.i("SQLITE", "query_NodesAll - results.size: " + results.size());
			return results;
		} else {
			return null;
		}
	}
	
	private ArrayList<NodeInfo> query_FloorIDbyNodeID() {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		final ArrayList<NodeInfo> results = new ArrayList<NodeInfo>();

		// open the SQLite database
		try {
			db = sqliteHelper.getWritableDatabase();
		} catch (SQLException e) {
			// TODO error handling
			if (db != null) {
				db.close();
			}
			//results.add(SQLiteConstants.RESULT_FAILED);
			return null;
		}

		// obtain data from sqlite database
		// params[0] == buildingID
		// params[1] == nodeID
		cursor = db.query(true, // distinct
				SQLiteConstants.TABLE_NAME, // table name
				new String[] { SQLiteConstants.KEY_FLOOR_ID }, // table columns returned
				SQLiteConstants.KEY_BUILDING_ID + "='" + params[0] + "' AND " + SQLiteConstants.KEY_NODE_ID + "='" + params[1] + "'", // where clause
				null, // selection args
				null, // groupBy
				null, // having
				null, // orderBy
				null // limit
				);

		// create an ArrayList<String> from cursor results
		if (this.future == null || !this.future.isCancelled()) {
			while (cursor.moveToNext()) {
						results.add(new NodeInfo(cursor.getString(cursor.getColumnIndex(SQLiteConstants.KEY_FLOOR_ID))
						));
			}
		}

		// close the database connection, and return results
		if (cursor != null) {
			cursor.close();
		}
		if (db != null) {
			db.close();
		}

		if (this.future == null || !this.future.isCancelled()) {
			Log.i("SQLITE", "query_NodesAll - results.size: " + results.size());
			return results;
		} else {
			return null;
		}
	}
	
	private ArrayList<NodeInfo> query_DisplayAllData() {
		final ArrayList<NodeInfo> results = new ArrayList<NodeInfo>();
		boolean success = false;
		
		// first, synch database
		success = (query_SynchDB());

		// second, get all data
		if (this.future == null || !this.future.isCancelled()) {
			if (success) {
				results.addAll(query_AllData());
			}
		}
		Log.i("SQLITE",	"query_DisplayAllData - results.size: " + results.size());
		return results;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (this.future != null) this.future.cancel(true);
		Toast.makeText(context, "Task canceled!", Toast.LENGTH_SHORT).show();
	}
	
	public String capitalize(final String string) {
		if (string == null)
			throw new NullPointerException("string");
		if (string.equals(""))
		    throw new NullPointerException("string");
		
		return Character.toUpperCase(string.charAt(0)) + string.substring(1);
	}
	
	private class UpdateElement extends UpdateElementBase<E1, E2> {

		private final Collection<?> mResults;

		public UpdateElement(E1 element1, E2 element2, Collection<?> results) {
			super(element1, element2);
			Log.i("SQLITE", "UpdateElement: " + results.size());
			this.mResults = results;
		}

		@Override
		public void updateTextView() {
			Log.i("SQLITE", "updateTextView: " + mResults.size());
			if (mResults.size() > 0) {
				handlerUI.post(new Runnable() {
					public void run() {
						int mCount = 1;
						for (Object object : mResults) {
							((TextView) element1).append(mCount + ". " + object.toString()
									+ "\n");
							mCount++;
						}
					}
				});

			} else {
				handlerUI.post(new Runnable() {
					public void run() {
						((TextView) element1)
								.append("\n** no records found in sqlite database\n");
					}
				});
			}
		}

		// TODO fix this unchecked type cast warning
		@Override
		public void updateArrayList() {
			// first, clear the items list
			((ArrayList<?>) element1).clear();

			// update arrayList
			//((ArrayList<?>) element1).addAll(mResults);
			((ArrayList<?>) element1).addAll((Collection) mResults);
			// notify array adapter
			((ArrayAdapter<?>) element2).notifyDataSetChanged();
		}

		@Override
		public void updateStringArray() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void updateRunnable() {
			handlerUI.post((Runnable) element1);			
		}

	}
	
}
