/*
 * Activity containing the user manual inside a webView
 * 
 * Created by: Omar Almadani
 */

package com.mgh;



import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;



public class helpActivity extends Activity {

	private WebView helpWeb;
	public TextView customFont;	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);


		//change font for title
		customFont = (TextView) findViewById(R.id.titlefont1);
		Typeface titleFont = Typeface.createFromAsset(getAssets(),"fonts/MTCORSVA.TTF");
		customFont.setTypeface(titleFont);

		//Initialize the webView and open the help
		helpWeb = (WebView)findViewById(R.id.helpWeb);
		helpWeb.loadUrl("file:///android_asset/help.html");

		//make sure new links opens in widget
		helpWeb.setWebViewClient(new WebViewClient(){

			public boolean shouldOverrideUrlLoading(WebView view, String url){
				view.loadUrl(url);
				return true;
			}

		});




	}

}
