/*
 * This Class is responsible for drawing the map. it uses the BitmapFactory's functionality in order to draw the route over the raw map. It also needs to connect 
 * to the database in order to convert the custom grid to pixel points
 * 
 * Created by: Kevin, Omar, Nick
 * Last modifed By: Omar Almadani
 */

package com.mgh;

import java.util.ArrayList;
import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;


public class PaintMap extends Activity {


	// floor point array
	private ArrayList<GridPoint> x = new ArrayList<GridPoint>();
	private ArrayList<GridPoint> y = new ArrayList<GridPoint>();

	// route
	private ArrayList<Node> route = new ArrayList<Node>();

	// current position in route
	private int currentStep;

	// current floor
	private int currentFloor;

	// drawing utils
	private Canvas c;
	private Paint p = new Paint();

	// resources - needed in order to access map images from res
	private Resources res;

	// density of calling screen
	private double density;

	// bitmap of the completed map
	private Bitmap completedMap;

	// database setup
	private SQLiteDatabase db;
	private Cursor cursor;
	
	// user location
	private float xChange = 0;
	private float yChange = 0;
	private final float conversionFactor = 2;
	

	/*
	 * 3 argument constructor
	 * arguments:
	 * 1) Arraylist<Node> representing the route to draw on a given floor map
	 * 2) Resources - required in order to access the given images from res/drawable
	 * 3) Double representing the density multiplier of the user screen - needed in order to scale drawing
	 * 
	 * Created by: Kevin Roisin
	 */
	public PaintMap(ArrayList<Node> rt, Resources r, double screenDensity){

		// have to pass resources in order access map in drawable
		this.res = r;

		// route to draw makes a copy of the arraylist of the route passed to the constructor
		// needed to get correct coordinates of current floor
		for (int i = 0; i < rt.size(); i++) {
			this.route.add(new Node(rt.get(i).getName()));
		}

		// screen density
		this.density = screenDensity;

		// setup painter
		p.setStrokeWidth(4);

		// get current floor
		currentFloor = this.route.get(0).getFloor();

		// open database
		db = SQLiteDatabase.openDatabase("/data/data/com.mgh/databases/mgh", null, SQLiteDatabase.OPEN_READWRITE);

		// query to select the appropriate path ID
		String query = "SELECT axis, grid_line, pixel FROM Grid WHERE Floor = " + currentFloor +";";

		// execute query
		cursor = db.rawQuery(query, null);

		// get the route points
		while (cursor.moveToNext()) {

			// get the next node in the route
			String axis = cursor.getString(cursor.getColumnIndex("axis"));
			int grid_line = cursor.getInt(cursor.getColumnIndex("grid_line"));
			int pixel = cursor.getInt(cursor.getColumnIndex("pixel"));

			// add it to x axis 
			if(axis.equals("x")){
				x.add(new GridPoint(grid_line,pixel));
			} else{
				y.add(new GridPoint(grid_line,pixel));
			}

		}

		// close db
		cursor.close();
		db.close();

		// convert the points to PIXELS - removes (x,y)-datapoint and replaces w/ (x,y)-pixels
		this.convertPoints();

	}

	/*
	 * Updates the currentStep and then calls upon drawMap activity to redraw the map
	 * 
	 * Created by: Kevin Roisin
	 *  
	 */
	// update point
	public void updateCurrentStep(int newStep){

		// update the step
		currentStep = newStep;
		Log.i("currentStep", "currentStep: " + currentStep +", newStep: " +newStep);
		// regenerate the map
		this.drawMap();
	}

	/*
	 * converts all points in the route to actual screen x,y coordinates using the screen density multiplier
	 * assumes that the route ArrayList<node> does exist. 
	 * saves all the new points into the same arrayList
	 * 
	 * Created by: Kevin Roisin
	 */
	public void convertPoints(){

		// iterate through loop
		for(int i = 0; i < route.size(); i++){

			// default no pixel match found, so new x/y are negative to show that
			int newx = -1;
			int newy = -1;

			// match x
			for(int j = 0; j < x.size(); j++){
				if(route.get(i).getX() == x.get(j).getPoint()){
					newx = x.get(j).getPixel();
				}
			}

			// match y
			for(int k = 0; k < y.size(); k++){
				if(route.get(i).getY() == y.get(k).getPoint()){
					newy = y.get(k).getPixel();
				}
			}

			// change the points of routes(i) from (x,y) to the (x,y)-pixels, 
			route.get(i).setX((int)(newx*this.density));
			route.get(i).setY((int)(newy*this.density));

			// log the coordinates
			//Log.i("coord", route.get(i).getX()+","+route.get(i).getY()+"");
		}
	}
	
	public float getxChange() {
		return xChange;
	}

	public void setxChange(float xChange) {
		this.xChange = xChange;
	}

	public float getyChange() {
		return yChange;
	}

	public void setyChange(float yChange) {
		this.yChange = yChange;
	}



	/*
	 * return the completed map as a bitmap
	 * 
	 * Created by: Kevin Roisin
	 */
	public Bitmap getMap(){
		return this.completedMap;
	}

	/*
	 * Draws the actual map. uses the bitmatFactory
	 * assumes the route array and the image of the map do exist
	 * draws the route on top of the map
	 * 
	 * created by Kevin Roisin
	 *  
	 */
	// draw the actual map
	public void drawMap(){


		// declare bitmap
		Bitmap src = null;

		// decide which image to use to paint the map on
		//because each "route" consists of one floor it is enough to check the floor of the first node
		switch(route.get(0).getFloor()){

		case 1 :{
			src = BitmapFactory.decodeResource(this.res, R.drawable.floor1); 
			break;
		}
		case 2 :{
			src = BitmapFactory.decodeResource(this.res, R.drawable.floor2); 
			break;
		}

		}

		// load map image as mutable image
		Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

		// create the canvas to draw on with the sizes of the bitmap
		c = new Canvas(dest);

		// draw map on canvas
		c.drawBitmap(src, 0f, 0f, null);

		// set start and end labels
		p.setColor(Color.BLACK);
		c.drawText("Start", this.route.get(0).getX(), this.route.get(0).getY() - 5, p);
		c.drawText("End", this.route.get(this.route.size()-1).getX(), this.route.get(this.route.size()-1).getY() - 5, p);



		// draw points and lines on map
		for(int i = 0; i < route.size(); i++){
			int cx = route.get(i).getX();
			int cy = route.get(i).getY();

			Log.i("PaintPoint: ", "cx,cy: " + cx +","+cy);

			// reset color to default
			p.setColor(Color.rgb(80,00,00));

			// if it's not the first step, draw a line connecting the previous step to the current step
			if(i > 0){
				// get previous step coordinates
				int cxprev = route.get(i-1).getX();
				int cyprev = route.get(i-1).getY();

				// draw connector line
				c.drawLine(cxprev, cyprev, cx, cy, p);

			}

			Log.i("StepNumber from Paint", "currentStep: "+currentStep+", i: " + i);

			// if it's drawing the current step, draw it in red
			if(i != currentStep){
				// draw the point as a circle
				c.drawCircle(cx, cy, 4, p);

			} 

		}


		// draw current step
		p.setColor(Color.RED);
		Log.i("currentstep after forloop", "afterfor currentStep: " + currentStep);
		c.drawCircle(route.get(currentStep).getX(), route.get(currentStep).getY(), 8, p);
		
		// draw where the user is
		c.drawCircle(route.get(currentStep).getX()+(xChange*conversionFactor), route.get(currentStep).getY()+(xChange*conversionFactor), 10,p);
		

		// map is drawn, so set completedMap instance var as the map
		this.completedMap = dest;

		// NO LONGER NEED TO SAVE IMAGE TO FILE
		/* write the map to file - PROBABLY NOT NEEDED ANYMORE DUE TO getMAP() returning a bitmap
		try {
			dest.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(new File("/sdcard/map.PNG")));
			Log.i("image", "map image created & saved");
			// dest is Bitmap, if you want to preview the final image, you can display it on screen also before saving
		} catch (FileNotFoundException e) {
			Log.i("image", "error creating image");
		}
		 */
	}
}