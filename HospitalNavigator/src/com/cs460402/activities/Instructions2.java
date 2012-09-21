package com.cs460402.activities;

import com.cs460402.app.AppConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;

public class Instructions2 extends Activity implements OnClickListener, OnGestureListener {

	private Button next, back;
	private GestureDetector myGesture;
	
	private final Class previousActivity = AppConstants.CLASS_INSTRUCTIONS;
	private final Class nextActivity = AppConstants.CLASS_INSTRUCTIONS3;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instructions2);
		
		myGesture = new GestureDetector(this);
		
		next = (Button) findViewById(R.id.instructionNext);
		next.setOnClickListener(this);

		back = (Button) findViewById(R.id.instructionBack);
		back.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.instructionNext:
			loadNextScreen();
			break;

		case R.id.instructionBack:
			loadPreviousScreen();
			break;
		}
	}

	private void loadNextScreen() {
		Intent intent = new Intent(this, nextActivity);
		startActivity(intent);
	}
	
	private void loadPreviousScreen() {
		Intent intent = new Intent(this, previousActivity);
		startActivity(intent);
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

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,	float velocityY) {
		try {
			//do not do anything if the swipe does not reach a certain length of distance
			if (Math.abs(e1.getY() - e2.getY()) > AppConstants.SWIPE_MAX_OFF_PATH) return false;
			 
			// right to left swipe (next)
			if(e1.getX() - e2.getX() > AppConstants.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > AppConstants.SWIPE_THRESHOLD_VELOCITY) {
				//Toast.makeText(this, "RIGHT TO LEFT", Toast.LENGTH_SHORT).show();
				loadNextScreen();
			}
			
			// left to right swipe (back)
			else if (e2.getX() - e1.getX() > AppConstants.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > AppConstants.SWIPE_THRESHOLD_VELOCITY) {
				//Toast.makeText(this, "LEFT TO RIGHT", Toast.LENGTH_SHORT).show();
				loadPreviousScreen();
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
}
