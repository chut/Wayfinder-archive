/*
 * activity in charge of if selecting the room. makes a call to the database in order to find all rooms on the current floor
 * 
 */
package com.mgh;

import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class RoomSelectActivity extends ListActivity {

	public ListView list;
	private EditText search;
	public ArrayList<Room> rooms = new ArrayList<Room>();
	public ArrayList<Room> roomsCopy = new ArrayList<Room>();	
	public RoomAdapter adapter;
	public TextView customFont;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);

		//change font for title
		customFont = (TextView) findViewById(R.id.titlefont1);
		Typeface titleFont = Typeface.createFromAsset(getAssets(),"fonts/MTCORSVA.TTF");
		customFont.setTypeface(titleFont);
		
		Bundle extras = getIntent().getExtras();
		Floor choice = extras.getParcelable("parcel");
		rooms = choice.getRooms();
		//edit.setText(choice.getFloor());

		adapter = new RoomAdapter(this, R.layout.row, rooms);
		setListAdapter(this.adapter);

		search = (EditText) findViewById(R.id.search);
		search.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				roomSearch(search.getText().toString());
				Bundle extras = getIntent().getExtras();
				Floor choice = extras.getParcelable("parcel");
				roomsCopy = choice.getRooms();
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {	 
			}
			public void onTextChanged(CharSequence s, int start, int before, int count) {								
			}
		});
	}
		
	public void roomSearch(String query) {		
		ArrayList<Room> queriedRooms = new ArrayList<Room>();
		for (Room room : roomsCopy) {
			if(room.getRoom().startsWith(query.toLowerCase()) || room.getRoom().startsWith(query.toUpperCase()) || room.getType().startsWith(query)) {
				queriedRooms.add(room);
			}
		}
		adapter.rooms.clear();
		for (Room r : queriedRooms) {
			adapter.rooms.add(r);
		}
		adapter.notifyDataSetChanged();		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);    	   	
		Intent intent = getIntent();
		intent.putExtra("room", rooms.get(position));
		setResult(RESULT_OK, intent);
		finish();

		/*
    	Intent backToMain = new Intent(this, MghActivity.class);
    	backToMain.putExtra("parcel", rooms.get(position));

    	Bundle b = new Bundle();

    	b.putParcelable("choice", choice);

    	roomSelect.putExtras(b);    	

        startActivity(backToMain);
		 */        

	}

	public class RoomAdapter extends ArrayAdapter<Room> {

		private ArrayList<Room> rooms;

		public RoomAdapter(Context context, int textViewResourceId, ArrayList<Room> rms) {
			super(context, textViewResourceId, rms);

			this.rooms = rms;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}
			Room r = rooms.get(position);
			if (r != null) {
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				if (tt != null) {
					tt.setText("Room: " + r.getRoom().toString());
					tt.setTextColor(Color.rgb(18, 79, 84));
				}
				if(bt != null){
					bt.setText("Type: "+ r.getType().toString());
					bt.setTextColor(Color.rgb(18, 79, 84));
				}
			}
			return v;
		}


	}



} // end RoomSelectActivity class