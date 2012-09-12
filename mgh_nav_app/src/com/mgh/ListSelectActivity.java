/*
 * This class is the activity used to list all rooms available
 * 
 * 
 * Created By:Omar Almadani
 * Modified By: Nicholas Hentschel
 * 
 */
package com.mgh;

import java.io.IOException;
import java.util.ArrayList;

import com.mgh.DataBaseHelper;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ListSelectActivity extends ListActivity {

	public ArrayList<Floor> floors = new ArrayList<Floor>();
	public FloorAdapter adapter;
	public Floor choice;
	public ListView list;
	private DataBaseHelper myDbHelper;
	private SQLiteDatabase db;
	private ContentValues values;
	private Cursor cursor;
	public TextView customFont;
	private EditText searchEditText; //created to make it invisible


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);

		//change font for title
		customFont = (TextView) findViewById(R.id.titlefont1);
		Typeface titleFont = Typeface.createFromAsset(getAssets(),"fonts/MTCORSVA.TTF");
		customFont.setTypeface(titleFont);
		
		//connect editText and make it invisible
		searchEditText = (EditText) findViewById(R.id.search);
		searchEditText.setVisibility(View.INVISIBLE);


		//connecting to database using the helper
		DataBaseHelper myDbHelper = new DataBaseHelper(this);
		myDbHelper = new DataBaseHelper(this);

		try { 
			myDbHelper.createDataBase(); 
		} catch (IOException ioe) {	
			throw new Error("Unable to create database");

		}
		try {
			myDbHelper.openDataBase();
		} catch(SQLException sqle){
			throw sqle;
		}

		//run SQL select Statment to get floors
		db = SQLiteDatabase.openDatabase("/data/data/com.mgh/databases/mgh", null, SQLiteDatabase.OPEN_READWRITE);
		String query = "SELECT * FROM Location ORDER BY Floor_num";
		cursor = db.rawQuery(query, null);


		ArrayList<String> tmpFloors = new ArrayList<String>();
		ArrayList<String> tmpRooms = new ArrayList<String>();

		cursor.moveToFirst();
		while (cursor.moveToNext()) {
			tmpFloors.add(cursor.getString(cursor.getColumnIndex("Floor_num")));
			tmpRooms.add(cursor.getString(cursor.getColumnIndex("Floor_num")) + "-" + cursor.getString(cursor.getColumnIndex("_id")) + "-" + cursor.getString(cursor.getColumnIndex("Loc_type")) + "-" + cursor.getString(cursor.getColumnIndex("Loc_point")));
		}
		cursor.close();
		db.close();
		myDbHelper.close();

		// sort out unique floors from tmp array
		String prev = "";
		for (String floor : tmpFloors) {
			if(!floor.equals(prev)){
				floors.add(new Floor(floor, new ArrayList<Room>())); 
			}
			prev = floor;
		}

		// add rooms to floor array				
		for(int i = 0; i <= tmpFloors.size(); i++){
			for(int j = 0; j < tmpRooms.size(); j++){
				String[] tokens = tmpRooms.get(j).split("[-]");
				if(tokens[0].equals(""+(i+1)))
					floors.get(Integer.parseInt(tokens[0])-1).rooms.add(new Room(tokens[0], tokens[1], tokens[2], tokens[3]));
			}
		}


		adapter = new FloorAdapter(this, R.layout.row, floors);
		setListAdapter(this.adapter);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 5) {								
			Intent intent = getIntent();
			intent.putExtra("room", data.getParcelableExtra("room"));
			setResult(RESULT_OK, intent);

			finish();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		choice = floors.get(position);

		Intent roomSelect = new Intent(this, RoomSelectActivity.class);
		roomSelect.putExtra("parcel", choice);

		/*
    	Bundle b = new Bundle();

    	b.putParcelable("choice", choice);

    	roomSelect.putExtras(b);
    	startActivity(roomSelect);
		 */

		startActivityForResult(roomSelect, 5);

	}



	public class FloorAdapter extends ArrayAdapter<Floor> {

		private ArrayList<Floor> floors;


		public FloorAdapter(Context context, int textViewResourceId, ArrayList<Floor> flrs) {
			super(context, textViewResourceId, flrs);

			this.floors = flrs; 

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}
			Floor f = floors.get(position);
			if (f != null) {
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				if (tt != null) {
					tt.setText("Floor: " + f.getFloor().toString()); 
					tt.setTextColor(Color.rgb(18, 79, 84));
				}
				if(bt != null){
					//this is for the description of the floor, commented out because not used
					// bt.setText("Description: "+ "dummy description");
					//bt.setTextColor(Color.rgb(18, 79, 84));
				}
			}
			return v;
		}


	}


} // end ListSelectActivity class