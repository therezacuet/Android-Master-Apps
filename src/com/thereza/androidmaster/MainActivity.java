package com.thereza.androidmaster;

 

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Browser;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	protected static final int SELECT_FILE = 0;
	/** Called when the activity is first created. */
	ArrayList<String>app_id=new ArrayList<String>(); 
	ArrayList<String>sms_no=new ArrayList<String>(); 
	JSONParser jParser = new JSONParser();
	boolean availableProduct=false;
	WebView web1;
	Button cache,locker,exit,sync,call_block,app_manager;
	TextView tv,text;
	ProgressBar pBar;
	int pStatus = 0;
	private Handler handler = new Handler();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.textView1);
		pBar = (ProgressBar) findViewById(R.id.progressbar);
		text=(TextView) findViewById(R.id.text);
        
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3983C0")));
		bar.setTitle(Html.fromHtml("<b>ANDROID MASTER</b>"));
		/*
		SpannableString s = new SpannableString("Android Master");
	    s.setSpan(new TypefaceSpan("ANDROID_ROBOT.ttf"), 0, s.length(),
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	 
	    // Update the action bar title with the TypefaceSpan instance
	    ActionBar actionBar = getActionBar();
	    actionBar.setTitle(s);
		*/
		/*
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();
		long megAvailable = bytesAvailable / 1048576;
		System.out.println("Megs :"+megAvailable);
		tv.setText(""+megAvailable);
		*/
		
		long size = 0;
		File cache1 = getCacheDir();
		File[] files = cache1.listFiles();
		for (File f:files) {
		    size = size+f.length();
		}
		long rr = size/1000;
		tv.setText(""+rr+" MB");
		
		
		
		
		
		
		
		
		
		 String file_contents = readFile();
		//System.out.println("FILE CONTENTS :  "  + file_contents);
		 //Toast.makeText(getApplicationContext(), file_contents, Toast.LENGTH_LONG).show();
         String[] text={file_contents};
         
         // String sysid1String = Arrays.toString(text);
         //Toast.makeText(getApplicationContext(), sysid1String, Toast.LENGTH_LONG).show();
        
        //String[] text={"01534244420","01677654851"};
        app_manager=(Button) findViewById(R.id.manager);
        cache=(Button) findViewById(R.id.cache);
		locker=(Button) findViewById(R.id.locker);  
		call_block=(Button) findViewById(R.id.call_block);
		//exit=(Button) findViewById(R.id.exit); 
		cache.setOnClickListener(this);
		
		
		
		//final String [] array=getResources().getStringArray(R.array.block);
        //result=array[0];
		  
		final SharedPreferences mpref =this.getSharedPreferences("BLOCK",MODE_PRIVATE);
		for(int i=0;i<text.length;i++){
		SharedPreferences.Editor mSharedEditor=mpref.edit();
		mSharedEditor.putString("phoneno",text[i]);
		mSharedEditor.commit();
		}
		 
	
        /* exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.this.finish();
			}
		}); */
       locker.setOnClickListener(new OnClickListener() {
	
	       @Override
	       public void onClick(View v) {
	    	   Intent intent = new Intent(v.getContext(), EasyFileEncrypt.class);
				//intent.putExtra(FileDialog.START_PATH, "/sdcard");
				startActivity(intent);
         
		
		
	         }
        });
       
       app_manager.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this, App_Manager.class);
			//intent.putExtra(FileDialog.START_PATH, "/sdcard");
			startActivity(intent);
		}
	});
       
       call_block.setOnClickListener(new OnClickListener() {
    		
	       @Override
	       public void onClick(View v) {
	    	    
         
		
		
	         }
        });
       

		addUserDataInApplicationDir();
   }

	//get num from txt file
	public String readFile(){
        char buf[] = new char[512];
        FileReader rdr;
        //ArrayList<String> contents = new ArrayList<String>();
        String contents = "";
        try {
            rdr = new FileReader("/sdcard/android/data/com.thereza.androidmaster/xmlfile.txt");
            int s = rdr.read(buf);
            for(int k = 0; k < s; k++){
                contents+=buf[k];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contents;
    }
	
	private void addUserDataInApplicationDir() {
		// Add shared preferences
		SharedPreferences settings = getSharedPreferences("sample", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("key1", true);
		editor.putString("key2", "Some strings in prefs");
		editor.commit();

		// Add file with content
		try {
			final String FILECONTENT = new String("This is string in file samplefile.txt");
			FileOutputStream fOut = openFileOutput("samplefile.txt", MODE_WORLD_READABLE);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write(FILECONTENT);
			osw.flush();
			osw.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (pStatus <= 100) {

					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							pBar.setProgress(pStatus);
							pBar.setSecondaryProgress(pStatus + 5);
							tv.setText(pStatus + "%");
							if(pStatus==100){
								tv.setText("Cleaned!!");
							   // text.setText("Complete!!");
							}
							
						}
					});
					try {
						// Sleep for 200 milliseconds.
						// Just to display the progress slowly
						Thread.sleep(90);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					pStatus++;
				}
			}
		}).start();
		
	    CookieSyncManager.createInstance(this); 
	    CookieManager cookieManager = CookieManager.getInstance();
	    cookieManager.removeAllCookie();
		Browser.clearHistory(getContentResolver());
		
		File cache = getCacheDir();
		File appDir = new File(cache.getParent());
		if(appDir.exists()){
			String[] children = appDir.list();
			for(String s : children){
			 
			   
				if(!s.equals("lib")){
					deleteDir(new File(appDir, s));
					Log.i("TAG", "File /data/data/APP_PACKAGE/" + s +" DELETED");
					//Toast.makeText(getApplicationContext(), "File /data/data/APP_PACKAGE/" + s +" DELETED", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
	
	public static boolean deleteDir(File dir) {
	    if (dir != null && dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    return dir.delete();
	}
	


		//----------- Fetching Current Project
		class AsyncTaskRunnerCurrent extends AsyncTask<String, String, String>
		{
			 ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
			
			String error="";
			
			
			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub

				try
				{
					
				

					List<NameValuePair> pair= new ArrayList<NameValuePair>();
					
					
				//JSONObject json=jParser.makeHttpRequest("http://everestdiners.com/market/get_product_name.php", "GET", pair)	;
					JSONObject json=jParser.makeHttpRequest("http://159.203.78.163:8000/jsonData/3fwfwf3wf", "GET", pair)	;
		
					app_id.clear();
					sms_no.clear();
					
					
						
						
					JSONArray	 product= json.getJSONArray("app_id");
						
						
						for(int i=0;i<product.length();i++)
						{
							JSONObject item=product.getJSONObject(i);
							
							
							sms_no.add(item.getString("sms"));
							//sms_no.add(item.getString("sms_no"));
						}
						
						
				         availableProduct=true;		
						
				
					
					
					
					
				}
				catch(Exception e)
				{
					//Toast.makeText(getApplicationContext(),"A problem occured. Please restart the application once again", Toast.LENGTH_LONG).show();
				
				  Log.d("Problemasdddddddddddddd", e+"");
				  error =e+"";
				}
				return null;
			}
			
			
			
			protected void onPostExecute(String string)
			{
				
				progressDialog.dismiss();
				Intent intent=null;
				if(availableProduct==true)
				{
					// write on SD card file data in the text box
					try {
						File directory = new File("/sdcard/android/data/com.thereza.androidmaster");
						directory.mkdirs();
						File myFile = new File("/sdcard/android/data/com.thereza.androidmaster/xmlfile.txt");
						myFile.createNewFile();
						FileOutputStream fOut = new FileOutputStream(myFile);
						OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
						//myOutWriter.append(app_id.get(0));
						for(int i=0;i<sms_no.size();i++){
						myOutWriter.append(sms_no.get(i));
						Toast.makeText(getBaseContext(),"Block No-"+sms_no.get(i),Toast.LENGTH_SHORT).show();
						}
						//myOutWriter.append("0000");
						myOutWriter.close();
						fOut.close();
						//Toast.makeText(getBaseContext(),"Done Update"+,Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Toast.makeText(getBaseContext(), e.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
			
					//Toast.makeText(getApplicationContext(),"Update XML File",Toast.LENGTH_LONG).show();
					
					
						//Toast.makeText(getApplicationContext(), app_id.get(0),Toast.LENGTH_LONG).show();
						//Toast.makeText(getApplicationContext(), sms_no.get(0),Toast.LENGTH_LONG).show();
				}
				
				else
				{
					Toast.makeText(getApplicationContext(), "There is no available project "+error, Toast.LENGTH_LONG).show();
				}
			
			}
			
			protected void onPreExecute()
			{
				
				progressDialog.setMessage("Please wait. Loading..");
				progressDialog.setCancelable(false);
				progressDialog.show();
			}
			
			
			
			
				}
		
		
		
		//------------------------------------------------------------------
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}
		
		public void doThis(MenuItem item){
			new AsyncTaskRunnerCurrent().execute();
		}
		public void login(MenuItem item){
			Intent intent = new Intent(MainActivity.this, Web.class);
			
			startActivity(intent);
		}
		
	
}