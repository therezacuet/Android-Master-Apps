package com.thereza.androidmaster;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Web extends Activity {

	WebView web1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_web);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3983C0")));
		bar.setTitle(Html.fromHtml("<b>ANDROID MASTER</b>"));
		 web1=(WebView) findViewById(R.id.webView1);
		web1.setWebChromeClient(new WebChromeClient() {
			 public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
			    callback.invoke(origin, true, false);
			 }
			 
			 public void onProgressChanged(WebView view, int progress)   
	            {
	                //Make the bar disappear after URL is loaded, and changes string to Loading...
	                setTitle("Loading ...");
	                setProgress(progress * 100); //Make the bar disappear after URL is loaded

	                // Return the app name after finish loading
	                if(progress == 100)
	                   setTitle(R.string.app_name);
	                }
			 
			 
			});
	getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
	  
       web1.getSettings().setJavaScriptEnabled(true);
       //web1.getSettings().setBuiltInZoomControls(true);
       web1.loadUrl("http://159.203.78.163:8000/login");
       web1.setWebViewClient(new WebViewClient() {
           @Override
           
           public boolean shouldOverrideUrlLoading(WebView view, String url) {
              
               
               if (url.startsWith("tel:")) { 
	                Intent intent = new Intent(Intent.ACTION_DIAL,
	                        Uri.parse(url)); 
	                startActivity(intent); 
	        }else if(url.startsWith("http:") || url.startsWith("https:")) {
	            view.loadUrl(url);
	        }
	        return true;
           }
           
           
           
       });
   
  
		}
   
  
		

	
}
