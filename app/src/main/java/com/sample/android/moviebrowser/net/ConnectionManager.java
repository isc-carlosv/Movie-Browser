package com.sample.android.moviebrowser.net;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.JsonReader;


/**
 * @author Carlos Vasconcelos
 *
 */
public class ConnectionManager {
	public static final String ENCODING_UTF = "UTF-8";
	private HttpURLConnection httpConnection = null;
	private URL url = null;
	private InputStream is = null;
	private JsonReader jsonReader = null;
	
	private Context context;
	
	
	public ConnectionManager(Context context, String requestURL){
		this.context = context;
		
		try {
			url = new URL(requestURL);
			
		} catch (MalformedURLException e) {
			//TODO Handle malformed url
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	
	public JsonReader requestJson(){
		try {
			jsonReader = new JsonReader(new InputStreamReader(request(), ENCODING_UTF));
		} catch (UnsupportedEncodingException e) {
			//TODO Handle wrong encoding
		}
		
		return jsonReader;
	}
	
	public InputStream request(){
		if(!isNetworkAvailable()){
			//TODO Handle Network not available
		}
		
	    try {
	        httpConnection = (HttpURLConnection) url.openConnection();

	        int responseCode = httpConnection.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            is = httpConnection.getInputStream();
	            
	        } else {
	        	//TODO Handle error response from service
	        }
	        
	    } catch (Exception ex) {
	    	//TODO Handle error in connection
	    }
	    
	    return is;
	}
	
	public void closeConnection(){
	    try{
	    	if(is != null){
	    		is.close();
	    	}
	    	if(httpConnection != null){
	    		httpConnection.disconnect();
	    	}
		} catch(Exception e){
			//TODO Handle error closing inputStream
		}
	}
	
	
	public ByteArrayBuffer requestImage(){
		HttpURLConnection httpConnection = null;
		ByteArrayBuffer baf = new ByteArrayBuffer(1024);
		BufferedInputStream bis = null;

		if(!isNetworkAvailable()){
			return null;
		}
		
	    try {
	        httpConnection = (HttpURLConnection) url.openConnection();
	        
	        int responseCode = httpConnection.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	        	bis = new BufferedInputStream(httpConnection.getInputStream(), 1024);

				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}
	            
	        } 
	        
	    } catch (Exception ex) {

	    } finally{
	    	try{
		    	if(bis != null){
		    		bis.close();
		    	}
	    	} catch(Exception e){}

	    	if(httpConnection != null){
	    		httpConnection.disconnect();
	    	}
	    }
	    return baf;
	} 
	
}
