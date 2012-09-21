package com.cs460402.activities;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

/* 
 * Custom WebView that integrates with our navigation webpage. This class:
 * 	- adds an interface between Java (Android) and Javascript (webpage),
 * 	  which enables 2-way communication (note - this is broken in Android 2.3.*).
 * 	- hides progress bar and shows web view when loading is complete
 * 	- updates a TextView with step directions generated from the webpage
 * 	- handles scrolling of the webpage to center on a point.
 * 	- handles zooming (manual - button based)
 */
public class NavWebView extends WebView {

	public final boolean MODE_BROWSE = false;
	public final boolean MODE_ROUTE = true;

	private final double DEFAULT_ZOOM = 200.0d; // zoom level is set to 150% by
												// default;

	private TextView mTextView;
	private View mProgress;
	
	private Entry parentActivity;
	private final Handler mHandler;
	private final Context mContext;
	private final NavWebView mNavWebView;
	private final Scroller mScroller; // Scroller is a utility class provided by
										// Android that does scroll animation
										// calculations for you
	private double mZoomLevel;
	private boolean bMode = false; // true = route mode, false = browse mode

	private final int HANDLER_UPDATE_WEBVIEW = 0;
	private final int HANDLER_CENTER_WEBVIEW = 1;
	public final int HANDLER_CLOSE_PROGRESS = 2;
	private final int HANDLER_UPDATE_STARTEND = 3;
	public final int HANDLER_UPDATE_MAP_PREFS = 4;
	public final int HANDLER_SHOW_PROGRESS = 5;
	private final int HANDLER_SET_ZOOM_LEVEL = 6;
	private final int HANDLER_TOGGLE_FLING = 7;

	public NavWebView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mScroller = new Scroller(context);
		mHandler = new MyHandler();
		mContext = context;
		mNavWebView = this;

		getSettings().setLoadsImagesAutomatically(true);

		// enable JavaScript and the Java/JavaScript interface
		getSettings().setJavaScriptEnabled(true);
		addJavascriptInterface(new JavaScriptInterface(context), "Android");

		// enable zoom
		mZoomLevel = DEFAULT_ZOOM;
		getSettings().setSupportZoom(true);
		getSettings().setBuiltInZoomControls(true);
		getSettings().setUseWideViewPort(true); // not sure if this is needed?
		setInitialScale((int) mZoomLevel);
		
		// intercept URL loading and load in widget
		setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				mZoomLevel = DEFAULT_ZOOM; // reset to default
				view.loadUrl(url);
				// webUrlText.setText(url.toString());
				return true;
			}

		});

		// hack - fix issues with WebView focus
		getSettings().setLightTouchEnabled(true);
		requestFocus(View.FOCUS_DOWN | View.FOCUS_UP);
		setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_DOWN:
					if (!v.hasFocus()) {
						v.requestFocus();
					}
					break;
				}
				return false;
			}
		});
	} // end constructor
	
	public NavWebView setParentActivity(Entry activity) {
		this.parentActivity = activity;
		return this;
	}
	
	public NavWebView setMode(boolean mode) {
		this.bMode = mode;
		return this;
	}

	public boolean isRouteMode() {
		return bMode;
	}

	public Handler getHandler() {
		return mHandler;
	}

	// make sure the back key navigates back to the previous web page within the
	// webview
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && canGoBack()) {
			setZoomLevel(DEFAULT_ZOOM); // reset to default
			goBack();
			// webUrlText.setText(webView.getUrl().toString());
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public NavWebView setTextView(TextView mTextView) {
		this.mTextView = mTextView;
		return this;
	}

	public NavWebView setProgressView(View mProgress) {
		this.mProgress = mProgress;
		return this;
	}

	public NavWebView setZoomLevel(double mZoomLevel) {
		setInitialScale((int) mZoomLevel);
		this.mZoomLevel = mZoomLevel;
		return this;
	}

	/*
	 * Handles manual zooming of webview
	 */
	public void zoomView(boolean zoomIn) {
		if (zoomIn) {
			this.zoomIn();
		} else {
			this.zoomOut();
		}
		mZoomLevel = getScale() * 100;

		Log.i("ZOOM", "zoom level: " + mZoomLevel);
	}

	/*
	 * Handles WebView scrolling, with animation
	 */
	public void scrollWebView(int dx, int dy, int imgWidth, int imgHeight) {
		Log.i("CENTER", "raw - dx: " + dx + ", dy: " + dy + ", imgWidth: "
				+ imgWidth + ", imgHeight: " + imgHeight);

		mZoomLevel = getScale() * 100;

		// scale numbers according to zoomlevel
		dx = (int) (dx * (mZoomLevel / 100d));
		dy = (int) (dy * (mZoomLevel / 100d));
		imgWidth = (int) (imgWidth * (mZoomLevel / 100d));
		imgHeight = (int) (imgHeight * (mZoomLevel / 100d));

		Log.i("CENTER", "scaled: " + mZoomLevel + "% - dx: " + dx + ", dy: "
				+ dy + ", imgWidth: " + imgWidth + ", imgHeight: " + imgHeight);

		// center point on screen
		dx = dx - (getWidth() / 2);
		dy = dy - (getHeight() / 2);

		Log.i("CENTER", "centered - dx: " + dx + ", dy: " + dy);
		Log.i("CENTER", "webView width: " + getWidth() + ", webView height: "
				+ getHeight());

		// make sure values are within range (image doesnt move off screen)
		dx = Math.max(0, dx);
		dy = Math.max(0, dy);
		dx = (int) Math.min(dx, Math.max(imgWidth - getWidth(), 0));
		dy = (int) Math.min(dy, Math.max(imgHeight - getHeight(), 0));

		Log.i("CENTER", "ranged - dx: " + dx + ", dy: " + dy);
		Log.i("CENTER", "scrollX: " + getScrollX() + ", scrollY: "
				+ getScrollY());

		// convert into change of x and y, based on current view's x and y
		dx = (int) (dx - getScrollX());
		dy = (int) (dy - getScrollY());

		Log.i("CENTER", "final - dx: " + dx + ", dy: " + dy);

		// scroll to new point
		mScroller.startScroll((int) getScrollX(), (int) getScrollY(), dx, dy);
		new WebViewScroller().startThread("scroller");
	}

	public void rotateDot(int degrees) {
		final int compassAdjust = 90; // adjust degrees for wang bldg map north
		degrees = degrees + compassAdjust;
		loadUrl("javascript:rotateDot(" + degrees + ")");
	}

	public void nextStep() {
		loadUrl("javascript:myRoute.nextStep()");
	}

	public void previousStep() {
		loadUrl("javascript:myRoute.previousStep()");
	}

	public void setPOI() {
		loadUrl("javascript:setPOI('.poiHandicap', '"
				+ AppPrefs.getPOIcolor_handicap(mContext) + "', '"
				+ AppPrefs.getPOIdisplay_handicap(mContext) + "')");
		loadUrl("javascript:setPOI('.poiElevator', '"
				+ AppPrefs.getPOIcolor_elevator(mContext) + "', '"
				+ AppPrefs.getPOIdisplay_elevator(mContext) + "')");
		loadUrl("javascript:setPOI('.poiReception', '"
				+ AppPrefs.getPOIcolor_reception(mContext) + "', '"
				+ AppPrefs.getPOIdisplay_reception(mContext) + "')");
		loadUrl("javascript:setPOI('.poiExit', '"
				+ AppPrefs.getPOIcolor_exit(mContext) + "', '"
				+ AppPrefs.getPOIdisplay_exit(mContext) + "')");
		loadUrl("javascript:setPOI('.poiInfo', '"
				+ AppPrefs.getPOIcolor_info(mContext) + "', '"
				+ AppPrefs.getPOIdisplay_info(mContext) + "')");
		loadUrl("javascript:setPOI('.poiStairs', '"
				+ AppPrefs.getPOIcolor_stairs(mContext) + "', '"
				+ AppPrefs.getPOIdisplay_stairs(mContext) + "')");
		loadUrl("javascript:setPOI('.poiRestrooms', '"
				+ AppPrefs.getPOIcolor_restroom(mContext) + "', '"
				+ AppPrefs.getPOIdisplay_restroom(mContext) + "')");
		loadUrl("javascript:setPOI('.poiCafe', '"
				+ AppPrefs.getPOIcolor_cafe(mContext) + "', '"
				+ AppPrefs.getPOIdisplay_cafe(mContext) + "')");
	}

	public void setLineColor(String color) {
		loadUrl("javascript:setLineColor('" + color + "')");
	}

	public void setDotColor(String color) {
		loadUrl("javascript:setDotColor('" + color + "')");
	}

	public void setLineWidth(int width) {
		loadUrl("javascript:setLineWidth(" + width + ")");
	}

	public void updateAllNodeInfoBoxes(String startID, String startLabel, String endID, String endLabel) {
		loadUrl("javascript:updateAllNodeInfoBoxes('" + startID + "','"
				+ startLabel + "','" + endID + "','" + endLabel + "')");
	}
	
	public void clearFlag (boolean bStart) {
		//Log.i("WEB", "javascript:clearFlag(" + bStart + ")");
		loadUrl("javascript:clearFlag(" + bStart + ")");
	}
	
	public void plotFlag(boolean bStart, String nodeID) {
		//Log.i("WEB", "javascript:plotFlag(" + bStart + ", '" + nodeID + "', -1, -1)");
		loadUrl("javascript:plotFlag(" + bStart + ", '" + nodeID + "', -1, -1)");
	}
	
	public void hideNodeInfoBox() {
		loadUrl("javascript:hideNodeInfoBox()");
	}
	
	/*
	 * Scroll animation needs to be calculated on a separate thread
	 */
	private class WebViewScroller implements Runnable {
		Thread thread;

		public WebViewScroller startThread(String name) {
			thread = new Thread(this, name);
			thread.start();

			return this;
		}

		public void run() {
			int i = 0;
			float mX;
			float mY;
			while (mScroller.computeScrollOffset() && i < 100000) {
				mX = mScroller.getCurrX();
				mY = mScroller.getCurrY();

				Message msg = mHandler.obtainMessage(HANDLER_CENTER_WEBVIEW,
						(int) mX, (int) mY);
				mHandler.sendMessage(msg);

				i++; // as a safeguard against endless loop, thread will
						// terminate if 'i' reaches 100000
			}
			// thread exits
		}

	}

	/*
	 * UI level handler - receives messages from both WebViewScroller and
	 * JavaScriptInterface
	 */
	private class MyHandler extends Handler {

		public void handleMessage(Message msg) {
			Bundle mBundle;
			switch (msg.what) {
			case HANDLER_UPDATE_WEBVIEW: // message from JavaScriptInterface
				mBundle = (Bundle) msg.obj;
				String mStepText = mBundle.getString("stepText");
				int mLeft = mBundle.getInt("left");
				int mTop = mBundle.getInt("top");
				int mWidth = mBundle.getInt("imgWidth");
				int mHeight = mBundle.getInt("imgHeight");

				// Log.i("HANDLER","text: " + mBundle.getString("stepText") +
				// ", left: " + mBundle.getInt("left") + ", top: " +
				// mBundle.getInt("top") + ", width: " +
				// mBundle.getInt("imgWidth") + ", height: " +
				// mBundle.getInt("imgHeight"));
				Log.i("HANDLER", "text: " + mStepText + ", left: " + mLeft
						+ ", top: " + mTop + ", width: " + mWidth
						+ ", height: " + mHeight);

				// mProgress.setVisibility(GONE);
				// mNavWebView.setVisibility(VISIBLE);
				if (bMode)
					mTextView.setText("Step: " + mStepText);
				scrollWebView(mLeft, mTop, mWidth, mHeight);

				Log.i("HANDLER", "exiting");
				break;
			case HANDLER_CENTER_WEBVIEW: // message from WebViewScroller
				// Log.i("CENTER", "Scroller - mX: " + msg.arg1 + ", mY: " +
				// msg.arg2);
				scrollTo(msg.arg1, msg.arg2);

				break;
			case HANDLER_SET_ZOOM_LEVEL:
				setZoomLevel(msg.arg1);
				break;
			case HANDLER_CLOSE_PROGRESS: // message from JavaScriptInterface
											// (close progress bar)
				mProgress.setVisibility(GONE);
				mNavWebView.setVisibility(VISIBLE);

				break;
			case HANDLER_SHOW_PROGRESS: // message from JavaScriptInterface
				mNavWebView.setVisibility(INVISIBLE);
				mProgress.setVisibility(VISIBLE);

				break;
			case HANDLER_UPDATE_STARTEND:
				mBundle = (Bundle) msg.obj;
				String nodeID = mBundle.getString("id");
				String nodeLabel = mBundle.getString("label");

				if (msg.arg1 == 1) {
					// update start location
					//AppPrefs.setStartID(nodeID, mContext);
					//AppPrefs.setStartLabel(nodeLabel, mContext);
					parentActivity.updateStartEndLocation(true, null, nodeID, nodeLabel);
					Toast.makeText(mContext, "Start set to: " + nodeLabel, Toast.LENGTH_SHORT).show();
				} else {
					// update end location
					//AppPrefs.setEndID(nodeID, mContext);
					//AppPrefs.setEndLabel(nodeLabel, mContext);
					parentActivity.updateStartEndLocation(false, null, nodeID, nodeLabel);
					Toast.makeText(mContext, "End set to: " + nodeLabel, Toast.LENGTH_SHORT).show();
				}
				updateAllNodeInfoBoxes(AppPrefs.getStartID(mContext),
						AppPrefs.getStartLabel(mContext),
						AppPrefs.getEndID(mContext),
						AppPrefs.getEndLabel(mContext));
				break;
			case HANDLER_UPDATE_MAP_PREFS:
				hideNodeInfoBox();
				setLineColor(AppPrefs.getLineColor(mContext));
				setDotColor(AppPrefs.getDotColor(mContext));
				setLineWidth(AppPrefs.getLineWidth(mContext));
				if (AppPrefs.getStartID(mContext).length() == 0) {
					clearFlag(true);
				} else {
					plotFlag(true, AppPrefs.getStartID(mContext));
				}
				if (AppPrefs.getEndID(mContext).length() == 0) {
					clearFlag(false);
				} else {
					plotFlag(false, AppPrefs.getEndID(mContext));
				}
				setPOI();
				updateAllNodeInfoBoxes(AppPrefs.getStartID(mContext),
						AppPrefs.getStartLabel(mContext),
						AppPrefs.getEndID(mContext),
						AppPrefs.getEndLabel(mContext));
				break;
			case HANDLER_TOGGLE_FLING:
				if ((Boolean) msg.obj) {
					parentActivity.setMaxFling(parentActivity.ENABLE_FLING);
				} else {
					parentActivity.setMaxFling(parentActivity.DISABLE_FLING);
				}
				break;
			default:
				break;
			}

		}
	}// end MyHandler

	/*
	 * Sets up an interface bewteen Java (Android) and JavaScript (webpage) This
	 * class runs on its own thread (Android sets up the thread automatically)
	 * See this for more info:
	 * http://developer.android.com/guide/webapps/webview.html
	 */
	public class JavaScriptInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		JavaScriptInterface(Context c) {
			mContext = c;
		}

		/** Show a toast from the web page */
		public void showToast(String toast) {
			// Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
			Log.i("WEB", toast);
		}
		
		public void setInitialZoomLevel(int zoomLvl) {
			Message msg = mHandler.obtainMessage(HANDLER_SET_ZOOM_LEVEL, zoomLvl, 0);
			mHandler.sendMessage(msg);
		}
		
		/** Scroll WebView and update step TextView */
		public void updateWebView(String stepText, int left, int top,
				int imgWidth, int imgHeight) {
			// Log.i("ANDROID","text: " + stepText + ", left: " + left +
			// ", top: " + top + ", width: " + imgWidth + ", height: " +
			// imgHeight);
			Bundle myBundle = new Bundle();
			myBundle.putString("stepText", stepText);
			myBundle.putInt("left", left);
			myBundle.putInt("top", top);
			myBundle.putInt("imgWidth", imgWidth);
			myBundle.putInt("imgHeight", imgHeight);
			Log.i("ANDROID",
					"text: " + myBundle.getString("stepText") + ", left: "
							+ myBundle.getInt("left") + ", top: "
							+ myBundle.getInt("top") + ", width: "
							+ myBundle.getInt("imgWidth") + ", height: "
							+ myBundle.getInt("imgHeight"));

			Message msg = mHandler.obtainMessage(HANDLER_UPDATE_WEBVIEW, myBundle);
			mHandler.sendMessage(msg);

			// myBundle.clear();
			// msg.recycle();
			Log.i("ANDROID", "exiting");
		}

		public void closeProgressBar() {
			Message msg = mHandler.obtainMessage(HANDLER_CLOSE_PROGRESS);
			mHandler.sendMessage(msg);
		}

		public void showProgressBar() {
			Message msg = mHandler.obtainMessage(HANDLER_SHOW_PROGRESS);
			mHandler.sendMessage(msg);
		}

		public void updateStartEnd(String nodeID, String nodeLabel,	boolean bStart) {
			int bStartInt = bStart ? 1 : 0;
			Bundle myBundle = new Bundle();
			myBundle.putString("id", nodeID);
			myBundle.putString("label", nodeLabel);

			Message msg = mHandler.obtainMessage(HANDLER_UPDATE_STARTEND, bStartInt, 0, myBundle);
			mHandler.sendMessage(msg);
		}

		public void updateMapPrefs() {
			Message msg = mHandler.obtainMessage(HANDLER_UPDATE_MAP_PREFS);
			mHandler.sendMessage(msg);
		}
		
		public void toggleFling(boolean bOn) {
			Message msg = mHandler.obtainMessage(HANDLER_TOGGLE_FLING, bOn);
			mHandler.sendMessage(msg);
		}
	}// end JavaScriptInterface

}
