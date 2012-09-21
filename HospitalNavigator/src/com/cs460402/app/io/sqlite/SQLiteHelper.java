package com.cs460402.app.io.sqlite;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

	private ArrayList<String> tabledata;

	public SQLiteHelper(Context context) {
		super(context, SQLiteConstants.DATABASE_NAME, null,
				SQLiteConstants.DATABASE_VERSION);

		this.tabledata = null;
	}

	public SQLiteHelper(Context context, ArrayList<String> tabledata) {
		super(context, SQLiteConstants.DATABASE_NAME, null,
				SQLiteConstants.DATABASE_VERSION);

		this.tabledata = tabledata;
	}

	public void setTableData(ArrayList<String> tabledata) {
		Log.i("SQLITE", "setTableData table size: " + tabledata.size());
		this.tabledata = tabledata;
	}

	// called to create table
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("SQLITE", "onCreate database");
		db.execSQL("DROP TABLE IF EXISTS " + SQLiteConstants.TABLE_NAME);
		String sql = SQLiteConstants.CREATE_TABLE;
		// db.setVersion((int) System.currentTimeMillis());
		db.execSQL(sql);
		ContentValues values = new ContentValues();

		String[] fields;
		if (tabledata != null) {
			Log.i("SQLITE", "creating table");
			for (String record : tabledata) {
				fields = record.split(",");
				// fields[0] = nodeID
				// fields[1] = neighborNode
				// fields[2] = nodeLabel
				// fields[3] = distance
				// fields[4] = typeName
				// fields[5] = buildingID
				// fields[6] = floorID
				// fields[7] = floorLevel
				// fields[8] = isConnector
				// fields[9] = mapImg
				// fields[10] = photoImg
				// fields[11] = x
				// fields[12] = y
				// fields[13] = isPOI
				// fields[14] = poiIconImg
				// fields[15] = buildingName

				// insert record row
				values.clear();
				values.put(SQLiteConstants.KEY_NODE_ID, fields[0]);
				values.put(SQLiteConstants.KEY_NODE_LABEL, fields[2]);
				values.put(SQLiteConstants.KEY_NODE_TYPE, fields[4]);
				values.put(SQLiteConstants.KEY_NODE_PHOTO, fields[10]);
				values.put(SQLiteConstants.KEY_NODE_X,
						Integer.parseInt(fields[11]));//
				values.put(SQLiteConstants.KEY_NODE_Y,
						Integer.parseInt(fields[12]));//
				values.put(SQLiteConstants.KEY_NODE_IS_CONNECTOR,
						Integer.parseInt(fields[8]));//
				values.put(SQLiteConstants.KEY_NODE_IS_POI,
						Integer.parseInt(fields[13]));//
				values.put(SQLiteConstants.KEY_NODE_POI_Img, fields[14]);
				values.put(SQLiteConstants.KEY_BUILDING_ID, fields[5]);
				values.put(SQLiteConstants.KEY_BUILDING_NAME, fields[15]);
				values.put(SQLiteConstants.KEY_FLOOR_ID, fields[6]);
				values.put(SQLiteConstants.KEY_FLOOR_LEVEL,
						Integer.parseInt(fields[7]));//
				values.put(SQLiteConstants.KEY_FLOOR_MAP, fields[9]);
				values.put(SQLiteConstants.KEY_NEIGHBOR_NODE, fields[1]);
				values.put(SQLiteConstants.KEY_NEIGHBOR_DISTANCE,
						Integer.parseInt(fields[3]));//

				db.insert(SQLiteConstants.TABLE_NAME, null, values);
			}
		}

	}

	// called when database version mismatch
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("SQLITE", "onUpgrade database");
		newVersion++; // always force an update
		if (oldVersion >= newVersion)
			return;

		db.execSQL("DROP TABLE IF EXISTS " + SQLiteConstants.TABLE_NAME);
		onCreate(db);
	}

}
