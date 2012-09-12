/*
 * The map activity, where the map is drawn and the user interacts with the map
 * 
 * Created By: Kevin, Nick, Omar
 * 
 * Last Modified: Omar
 */
package com.mgh;

import java.util.ArrayList;
import com.polites.android.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import com.polites.android.*;

public class mapActivity extends Activity implements OnClickListener, OnInitListener, SensorEventListener {
	/** Called when the activity is first created. */

	// start and end locations expressed in strings (will be attached to bundle from MghActivity)
	private String startString;
	private String endString;

	// start and end nodes
	private Node start;
	private Node end;

	// transition & speaker preference
	private String transitionPreference;
	private boolean speakEnabled;
	private boolean shakeEnabled;

	// database setup
	private SQLiteDatabase db;
	private Cursor cursor;

	// the map object
	private PaintMap map;

	// density of the screen (used to scale the coordinates by phone)
	private double screenDensity;

	// mapview: displays the drawn map
	private GestureImageView mapView;

	// directionView: displays directional pointers (left, right, straight, checkmark)
	private ImageView directionView;
	private ImageView directionImage;

	// speaker
	private TextToSpeech speaker;

	// buttons
	private Button previous;
	private Button next;

	// current step counter
	private int stepNumber = 0;

	// hold all the routes (1 object in list if route is on 1 floor, 2 if on 2 floors, etc- one route/floor)
	private ArrayList<Route> route = new ArrayList<Route>();

	// holds the current route (0 for first floor of route, 1 for second floor of route, etc)
	// will need to be changed when second floor capability is added
	private int routeNumber = 0;

	// handle shaking of phone
	//  private SensorManager sensorMgr;
	// private ShakeEventListener sensorListener;


	private static final int updateInterval = 500;  //measured in msec
	private SensorManager sm = null;
	private final float alpha = (float) 0.8; //use for calculating gravity
	private boolean sensorReady;
	private float[]  linear_acceleration;
	private float[] gravity;
	private float[] accelerometervalues; //used for gravity
	private float[] magneticField;
	private float[]  r;
	private float[] i;
	private float[] worldCoord;
	private float xD= 0;
	private float yD= 0;
	private Thread t1;
	
	//Create Handler object to handle messages placed on queue 
	Handler handler = new Handler() {

		public  void handleMessage(Message msg) {
		}
	};



	//create a runnable object to draw current users position

	Runnable task = new Runnable() {
		public void run(){
			int counter =0;


			while(true){

				//make the tread sleep for half a second 
				try {
					Thread.sleep(updateInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				//make a copy of the vector then translates them into distances
				float[] a = worldCoord.clone();

				xD =+ (float) (a[0] *.5 *.25);
				yD =+  (float) (a[1] *.5 *.25);
				counter++;

				if(counter == 5){
					if( xD >=1 || yD>=1 ){
						map.setxChange(xD);
						map.setyChange(yD);
						drawMap();
						mapChanged();
					}

					xD=0;
					yD=0;
				}
			}
		}
	};




	/*
	 * onCreate to handle what happens the activity is created
	 * 
	 * Created by: Omar Almadani, Nick Hentschel, Kevin Roisin
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set which xml layout to use
		setContentView(R.layout.map);

		//creat thread and give it a name
		t1 = new Thread(task);
		t1.setName("T1");

	}

	/*
	 * onPause to handle what happens when the activity is paused
	 * 
	 * Created by: Kevin Roisin
	 */
	@Override
	protected void onPause() {
		super.onPause();

		t1.stop();
		sm.unregisterListener(this);

		/*
		// unregister the accelerometer sensor
		if(shakeEnabled){
			sensorMgr.unregisterListener(sensorListener);
		}*/

	}

	/*
	 * onResume to handle what happens when the activity is resumed
	 * 
	 * Created by: Kevin Roisin
	 */
	@Override
	protected void onResume() {
		super.onResume();
		t1.start();

		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);

		// register the accelerometer sensor
		/*if(shakeEnabled){
			sensorMgr.registerListener(sensorListener,sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);
		}*/
	}

	/*
	 * onStart method to initialize activity and variables
	 * 
	 * Created by: Kevin Roisin, Nick Hentschel, Omar Almadani
	 */
	@Override
	public void onStart() {
		super.onStart();
		//t1.start();

		// get the route start and end points - passed into bundle from MghActivity
		Bundle b = getIntent().getExtras();
		startString = b.getString("startpoint");
		endString = b.getString("endpoint");

		// get preferences for transition preference
		getPrefs();

		// connect xml elements to variables
		mapView = (GestureImageView) findViewById(R.id.map);
		directionView = (ImageView) findViewById(R.id.direction);
		directionImage = (ImageView) findViewById(R.id.imagedirection);
		previous = (Button) findViewById(R.id.previousButton);
		next = (Button) findViewById(R.id.nextButton);

		// set button listeners
		previous.setOnClickListener(this);
		next.setOnClickListener(this);

		// open database
		openDatabase();

		// setup speech
		speaker = new TextToSpeech(this, this);

		// create the start and end nodes from the strings (passed in from MghActivity)
		start = new Node(startString);
		end = new Node(endString);

		// call route
		planRoute(start, end);

		// close the db
		cursor.close();
		db.close();

		// get screen density
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenDensity = metrics.density;

		// shake listener setup
		/*
		if(shakeEnabled){
			setUpShakeListener();
		}*/


		// draw the initial map
		this.drawMap();

	}

	/*
	 * helper method to open the database
	 * 
	 * Created by: Kevin Roisin
	 */
	private void openDatabase(){
		// open database
		db = SQLiteDatabase.openDatabase("/data/data/com.mgh/databases/mgh", null, SQLiteDatabase.OPEN_READWRITE);
	}

	/*
	 * sets up a sensor listener to handle shake events.
	 * used to allow user to shake their phone if they get lost
	 * 
	 * Created by: Kevin Roisin
	 */
	/*
	private void setUpShakeListener() {
		sensorListener = new ShakeEventListener();
		sensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorMgr.registerListener(sensorListener,sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);
		sensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
			public void onShake() {
				lost();
			}
		});
	}*/

	//accuracy change listenerr in order to listen for change in position
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.d("sensorChange","onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
		//create the sensor manager object
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		

		synchronized (this){
			int type = event.sensor.getType();

			//get accelerometer values
			if (type == Sensor.TYPE_ACCELEROMETER) {

				//to be getRotationMatrix
				accelerometervalues = event.values.clone();
				//removes gravity
				gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
				gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
				gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

				//sets the linear acceleration by removing gravity
				linear_acceleration[0] = event.values[0] - gravity[0];
				linear_acceleration[1] = event.values[1] - gravity[1];
				linear_acceleration[2] = event.values[2] - gravity[2];

	
			}
			if (type == Sensor.TYPE_MAGNETIC_FIELD){
				magneticField = event.values.clone();

			}



			if (magneticField != null && accelerometervalues != null){


				SensorManager.getRotationMatrix(r, i, accelerometervalues, magneticField);
				
				float[] Rs = new float[16];
				float[] I = new float[16];
				SensorManager.getRotationMatrix(Rs, I, accelerometervalues, magneticField);


			}
			worldCoord[0] = (linear_acceleration[0]*r[0] + linear_acceleration[1]*r[1] + linear_acceleration[1]*r[1] );
			worldCoord[1] = (linear_acceleration[0]*r[3] + linear_acceleration[1]*r[4] + linear_acceleration[1]*r[5] );
			worldCoord[2] =  (linear_acceleration[0]*r[6] + linear_acceleration[1]*r[7] + linear_acceleration[1]*r[8]);

			//write the values to the log
			Log.i("realWorldCoord", "coord <" +worldCoord[0] +", " + worldCoord[1] + ", " + worldCoord[2] + ">" );

		}
	}



	/*
	 * sets the user preference for floor transition
	 * gets the preference that might have been set by the user at the start screen
	 * 
	 * sets transitionPreference to either "elevator", or, "stairs" 
	 * 
	 * Created by: Nick Hentschel
	 */
	private void getPrefs() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		transitionPreference = prefs.getString("elevatorstairspreference", "elevator");
		speakEnabled = prefs.getBoolean("speakEnabled", true);
		shakeEnabled = prefs.getBoolean("enableShake", true);


	}

	/* 
	 * reverses the current route. useful for backwards navigation
	 * this method re-plans the route by reversing the start and end nodes
	 * the method also resets the current step (stepNumber) to 0, as well as the current floor of the route
	 * 
	 * Created by: Kevin Roisin
	 */
	private void reverseRoute(){
		// open db
		openDatabase();

		// reverse the route
		String temp = startString;
		startString = endString;
		endString = temp;
		start = new Node(startString);
		end = new Node(endString);

		// reset route and step numbers
		stepNumber = 0;
		routeNumber = 0;

		// clear the previous route
		route.clear();

		// plan on draw the route
		planRoute(start, end);
		drawMap();

		// close db
		cursor.close();
		db.close();

	}

	/* 
	 * this method handles the steps involved in drawing the actual map
	 * 
	 * 1) creates a new PaintMap object with a given route
	 * 2) calls the PaintMap method "drawMap()" which creates a bitmap of the route
	 * 3) calls the mapChanged() method which handles updating the custom ImageView with the current map
	 * 4) calls the calcDirection() which calculates the upcoming turn (left,right,straight,arrived,transition) & sets direction images
	 * 5) finally this method sets the orientation based on which floor (floor 1 = landscape, floor 2 = portrait)
	 * 
	 * Created by: Kevin Roisin
	 * 
	 */
	public void drawMap(){

		// draw based on ArrayList route
		map = new PaintMap(route.get(routeNumber).getRoute(), getResources(), screenDensity);

		// draw the map
		map.drawMap();  

		// set the image in mapView to the route drawn through PaintMap object
		mapChanged();

		// calculate the direction of the upcoming turn
		calcDirection();

		// set the orientation based on floor
		if(route.get(routeNumber).getRoute().get(0).getFloor() == 1){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if(route.get(routeNumber).getRoute().get(0).getFloor() == 2) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	/*
	 *  code for preferences menu
	 *  creates the preferences menu from the given xml layout
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 * 
	 * Created by: Nick Hentschel
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu2, menu);
		return true;
	}

	/*
	 * handles which preference menu option was selected
	 * takes action based on button press
	 * 
	 * Created by: Nick Hentschel, Kevin Roisin
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.lost:
			lost();
			return true;
		case R.id.reverse:
			Log.i("Reverse", "reverse clicked");
			reverseRoute();
			return true;
		case R.id.quit:
			System.exit(0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* 
	 * show AlertDialog which allows the user to return to the start activity to select a new start location
	 * this method allows code reuse between the preferences menu and the shake-if-lost functionality
	 * 
	 * Created by: Nick Hentschel
	 *
	 */
	public void lost(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Would you like to return to the main screen and enter a new starting location?")
		.setCancelable(true)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mapActivity.this.finish();
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}


	/*
	 * plans the given route and calls the method to retrieve the actual routes
	 * 
	 * if the start and end nodes are on the same floor, it plans the route using the original start & end nodes
	 * if the start and end nodes aren't on the same floor, it creates the route by:
	 * 		1) find transition node of start floor (elevator/stairs, depending on preference of user)
	 * 		2) plan a route given the start node, and the first transition as end node
	 * 		3) find the transition node of the second floor of the route
	 * 		4) plan a route with start node being the transition node, and the end node being the original end node
	 * 
	 * Created by: Kevin Roisin
	 */
	public void planRoute(Node aStart, Node aEnd){

		// if equal, route all on one floor start -> end
		if(aStart.getFloor() == aEnd.getFloor()){
			// query start -> end
			route.add(getRoute(aStart, aEnd));

			// else floor start -> elevator, elevator -> end
		} else {

			// floors
			int floorStart = aStart.getFloor();
			int floorEnd = aEnd.getFloor();


			// start floor new end calc


			// find the appropriate elevator/stairs location
			String query = "SELECT MAX(Loc_point) FROM Location WHERE Floor_num = "+ floorStart +" AND Loc_type = \""+transitionPreference+"\"";

			// execute query
			cursor = db.rawQuery(query, null);

			// save the first floor elevator
			String startTransition = "";
			while (cursor.moveToNext()) {
				startTransition = cursor.getString(0);
			}

			// create the start floor end node (elevator for now
			Node startFloorEnd = new Node(startTransition);

			// calculate the first route
			route.add(getRoute(aStart, startFloorEnd));




			// end floor new start

			// query to select the appropriate path ID
			query = "SELECT MAX(Loc_point) FROM Location WHERE Floor_num = "+ floorEnd +" AND Loc_type = \""+transitionPreference+"\"";

			// execute query
			cursor = db.rawQuery(query, null);

			// save the second floor elevator
			String endTransition = "";
			while (cursor.moveToNext()) {
				endTransition = cursor.getString(0);
			}

			Log.i("FLoorChange", "EndFloor: " + floorEnd + ", endTransition: " + endTransition + ", aEnd: " + aEnd);

			// create the end floor start node
			Node endFloorStart = new Node(endTransition);

			// calculate the second route
			route.add(getRoute(endFloorStart, aEnd));


		}



	}

	/* 
	 * return an array of nodes representing a route from a start node to an end node
	 * queries the database to retrieve the given route
	 * removes all non-intersection nodes from the route to minimize user confusion
	 * 
	 * Created by: Kevin Roisin, Omar Almadani, Nick Hentschel
	 */
	public Route getRoute(Node start, Node end){

		// start and end node
		String startLoc = start.getName();
		String endLoc = end.getName();

		/*
		 * SELECT _id
		 * FROM Route
		 * WHERE Start_point = start.getName() AND End_point = end.getName()		 * 
		 */

		// query to select the appropriate path ID
		String query = "SELECT _id FROM Route WHERE Start_point = \'"+startLoc+"\' AND End_point = \'"+endLoc+"\'";

		Log.i("Start/End", "Start: " + startLoc + ", End: " + endLoc);

		Log.i("Query", query+"");
		// execute query
		cursor = db.rawQuery(query, null);

		Log.i("Route result", "Cursor size. Columns: " + cursor.getColumnCount()+ ", Rows: " + cursor.getCount());

		// save the path ID
		int pathId = -1;
		while (cursor.moveToNext()) {
			pathId = cursor.getInt(cursor.getColumnIndex("_id"));
		}

		Log.i("PathId", "PathId: " + pathId);

		/*
		 * SELECT Loc_point
		 * FROM RoutePathPoint
		 * WHERE Route_id = pathID
		 * ORDER BY point_in_sequence
		 */

		// query to select the appropriate path ID
		query = "SELECT Loc_point FROM RoutePathPoint WHERE Route_id = "+pathId+"  ORDER BY point_in_sequence";

		// execute query
		cursor = db.rawQuery(query, null);

		// arraylist to hold route from db
		ArrayList<Node> n = new ArrayList<Node>();

		// get the route points
		while (cursor.moveToNext()) {

			// get the next node in the route
			String pathPoint = cursor.getString(cursor.getColumnIndex("Loc_point"));

			// add the point to the arraylist of nodes
			n.add(new Node(pathPoint));
		}


		/* remove all points that do not fit:
		 * 1) Start point
		 * 2) End point
		 * 3) Intersection
		 */
		for (int i = (n.size() - 2) ; i > 0; i--) {
			if(n.get(i).getType() != 3){
				n.remove(i);
			}
		}


		// the route r, which is comprised of an arraylist of nodes
		Route r = new Route(n);

		// log the route points
		for (int i = 0; i < r.getRoute().size(); i++) {
			Log.i("Route", "Floor: " + r.getRoute().get(i).getFloor() + ", Point: " + r.getRoute().get(i).getName());
		}





		// return the route
		return r;
	}

	/*
	 * handles what happens when a button is clicked by the user
	 * -Previous arrow button
	 * -Next arrow button
	 * 
	 * Created by: Kevin Roisin, Omar Almadani, Nick Hentschel
	 */
	public void onClick(View v){

		Log.i("Transition", "RouteNumber: " + routeNumber + ", stepNumber: " + stepNumber);
		switch(v.getId()){

		case R.id.previousButton :{
			// if the user is not on the first step of a route on a given floor
			//  then we want to send them to the previous step
			if(stepNumber > 0){
				stepNumber--;
				map.updateCurrentStep(stepNumber);
				mapChanged();
				break;
			} 
			//if the current position in the current route is zero and there is a route prior to the current route in the route arrayList
			// then we want to send them back to the previous floor of the route
			else if(stepNumber == 0 && routeNumber > 0){
				routeNumber--;
				stepNumber = route.get(routeNumber).getRoute().size()-1;
				drawMap();
				map.updateCurrentStep(stepNumber);
				Log.i("Step", "stepNumber: " + stepNumber);
				mapChanged();
				break;

			}
			break;
		}

		case R.id.nextButton :{


			//if there is a next point in the current route, then send user to the next point
			if(stepNumber < route.get(routeNumber).getRoute().size()-1){
				stepNumber++;
				map.updateCurrentStep(stepNumber);
				mapChanged();
				break;
			}

			//if there is no next point in the current route but another route exists in the Route arrayList
			// then we want to send the user to the first step of the next route (ie, the next floor)
			if(stepNumber == route.get(routeNumber).getRoute().size()-1 && routeNumber+1 < route.size() ){
				routeNumber++;
				stepNumber = 0;
				drawMap();
				break;
			}
			break;
		}
		}



		// steps left is used to see how many steps are left until the destination
		int stepsLeft = route.get(routeNumber).getRoute().size() - (stepNumber+1);

		// just one more step, eg next step if destination (so automatic straight)
		if(stepsLeft == 1){
			setDirection(1);

			// the user has arrived, show checkmark
		} else if(stepsLeft == 0 && routeNumber == (route.size() -1)){
			setDirection(0);

			// no matter previous or next, we still want to calcDirection()
		} else {
			calcDirection();

		}

	}

	/*
	 * update the map in the layout with current map
	 * 
	 * Created by: Kevin Roisin
	 */
	public void mapChanged(){
		mapView.setImageBitmap(map.getMap());
	}

	/*
	 *  calculate based on changes in x and y the direction (right, left) of the upcoming turn
	 *  set the directional image to the appropriate turn
	 *  set the app drawer (drag-up turn image) to the appropriate direction as well
	 *  
	 *  Created by: Kevin Roisin
	 *  
	 */
	public void calcDirection(){

		// if the step is into an elevator or staircase for transition
		if(route.get(routeNumber).getRoute().size() -1 == stepNumber && routeNumber < route.size() -1){

			// if it's an elevator transition (set in shared preferences), use elevator image
			if(transitionPreference.equals("elevator")){
				setDirection(4);
				// else use the staircase image
			} else {
				setDirection(5);
			}

		} else 

			// if the route is greater than 2 nodes, there must be turn(s)
			//checks the change in y and x to determine the next turn
			if(route.get(routeNumber).getRoute().size() > 2 && stepNumber < route.get(routeNumber).getRoute().size()-2){

				int changeY01 = route.get(routeNumber).getRoute().get(stepNumber+1).getY() - route.get(routeNumber).getRoute().get(stepNumber).getY();

				int changeX01 = route.get(routeNumber).getRoute().get(stepNumber+1).getX() - route.get(routeNumber).getRoute().get(stepNumber).getX();

				int changeY12 = route.get(routeNumber).getRoute().get(stepNumber+2).getY() - route.get(routeNumber).getRoute().get(stepNumber+1).getY();

				int changeX12 = route.get(routeNumber).getRoute().get(stepNumber+2).getX() - route.get(routeNumber).getRoute().get(stepNumber+1).getX();

				// determine direction of upcoming turn
				if(changeY01 < 0 && changeX12 > 0){
					setDirection(3);
				} else if(changeY01 < 0 && changeX12 < 0){
					setDirection(2);
				} else if(changeY01 > 0 && changeX12 > 0){
					setDirection(2);
				} else if(changeY01 > 0 && changeX12 < 0){
					setDirection(3);
				} else if(changeX01 > 0 && changeY12 > 0){
					setDirection(3);
				} else if(changeX01 > 0 && changeY12 < 0){
					setDirection(2);
				} else if(changeX01 < 0 && changeY12 > 0){
					setDirection(2);
				} else if(changeX01 < 0 && changeY12 < 0){
					setDirection(3);
				} else if(changeY01 < 0 && changeX12 == 0){
					setDirection(1);
				} else if(changeY01 > 0 && changeX12 == 0){
					setDirection(1);
				} else if(changeX01 < 0 && changeY12 == 0){
					setDirection(1);
				} else if(changeX01 > 0 && changeY12 == 0){
					setDirection(1);
				}

				// route size is 2, so no turns, display straight
			} else if(route.get(routeNumber).getRoute().size() == 2){
				setDirection(1);
			}
	}

	/*
	 * set the directional and drag-up turn images
	 * called by passing it a int (0-5) representing which image to display
	 * 
	 * 0 = checkmark / arrival
	 * 1 = straight
	 * 2 = left
	 * 3 = right
	 * 4 = elevator
	 * 5 = stairs
	 * 
	 * Created by: Kevin Roisin
	 */
	public void setDirection(int direction){


		switch(direction){

		// arrival / checkmark
		case 0:{
			directionView.setImageResource(R.drawable.checkmark);
			directionImage.setImageResource(R.drawable.checkmark_img);
			if(speakEnabled){
				speak("Ahead, you have arrived at your destination.");
			}
			break;
		}

		// straight
		case 1:{
			directionView.setImageResource(R.drawable.straight);
			directionImage.setImageResource(R.drawable.straight_img);
			if(speakEnabled){
				speak("Go straight ahead.");
			}
			break;
		}

		// left
		case 2:{
			directionView.setImageResource(R.drawable.left);
			directionImage.setImageResource(R.drawable.left_img);
			if(speakEnabled){
				speak("Turn left ahead.");
			}
			break;
		}

		// right
		case 3:{
			directionView.setImageResource(R.drawable.right);
			directionImage.setImageResource(R.drawable.right_img);
			if(speakEnabled){
				speak("Turn right ahead.");
			}
			break;
		}

		// elevator
		case 4:{
			directionView.setImageResource(R.drawable.elevator);
			directionImage.setImageResource(R.drawable.elevator_img);
			if(speakEnabled){
				speak("Enter the elevator ahead.");
			}
			break;
		}

		// stairs
		case 5:{
			directionView.setImageResource(R.drawable.stairs);
			directionImage.setImageResource(R.drawable.stairs_img);
			if(speakEnabled){
				speak("Use the staircase ahead.");
			}
			break;
		}
		}
	}

	/*
	 * handles speech navigation to say upcoming direction/turn
	 * receives a string representing the text to speak to the user
	 * 
	 * Created by: Kevin Roisin
	 */
	public void speak(String output){
		// if speaker is talking, stop it
		if(this.speaker.isSpeaking()){
			this.speaker.stop();
			// else start speech
		} else {
			speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	/*
	 * required for speaker under implementation, but not needed
	 * specifically required by implementing onInitListener
	 */
	public void onInit(int arg0) {}

}







