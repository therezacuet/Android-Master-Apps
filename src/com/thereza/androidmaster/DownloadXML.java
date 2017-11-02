package com.thereza.androidmaster;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.Environment;

public class DownloadXML {
	
	
	public void DownloadFiles(){

	    try {
	        URL url = new URL("https://code.google.com/p/hassanpurcom/source/browse/trunk/android/AndroidFileDownloaderExample/res/layout/main.xml");
	        URLConnection conexion = url.openConnection();
	        conexion.connect();
	        int lenghtOfFile = conexion.getContentLength();
	        InputStream is = url.openStream();
	        File testDirectory = new File(Environment.getExternalStorageDirectory() + "/xmlupdate");
	        if (!testDirectory.exists()) {
	            testDirectory.mkdir();
	        }
	        FileOutputStream fos = new FileOutputStream(testDirectory + "/main.xml");
	        byte data[] = new byte[1024];
	        int count = 0;
	        long total = 0;
	        int progress = 0;
	        while ((count = is.read(data)) != -1) {
	            total += count;
	            int progress_temp = (int) total * 100 / lenghtOfFile;
	            if (progress_temp % 10 == 0 && progress != progress_temp) {
	                progress = progress_temp;
	            }
	            fos.write(data, 0, count);
	        }
	        is.close();
	        fos.close();
	    } catch (MalformedURLException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}
}