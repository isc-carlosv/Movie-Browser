package com.sample.android.moviebrowser.util;

import java.io.IOException;
import java.util.ArrayList;

import android.util.JsonReader;
import android.util.JsonToken;

import com.sample.android.moviebrowser.models.Movie;

/**
 * @author Carlos Vasconcelos
 *
 */
public class JsonParser {
	public static final String JSON_KEY_RESULTCOUNT = "resultCount";
	public static final String JSON_KEY_RESULTS = "results";
	public static final String JSON_KEY_TRACKID = "trackId";
	public static final String JSON_KEY_ARTISTNAME = "artistName";
	public static final String JSON_KEY_TRACKNAME = "trackName";
	public static final String JSON_KEY_TRACKPRICE = "trackPrice";
	public static final String JSON_KEY_TRACKCOVER = "artworkUrl100";
	
	
	public JsonParser(){
	}
	
	public ArrayList<Movie> parseMovieList(JsonReader jsonReader){
		ArrayList<Movie> movieList = new ArrayList<Movie>();
		
		try {
			jsonReader.beginObject();
		    while (jsonReader.hasNext()) {
		    	String name = jsonReader.nextName();
		    	if (name.equals(JSON_KEY_RESULTS) && jsonReader.peek() != JsonToken.NULL) {
		        	 
		    		jsonReader.beginArray();
		    		while (jsonReader.hasNext()) {
		    			Movie movie = parseMovie(jsonReader);
		    			if(movie != null){
		    				movieList.add(movie);
		    			}
		    		}
		    		jsonReader.endArray();
			   	     
		    	} else {
		    		jsonReader.skipValue();
		    	}
		    }
		    jsonReader.endObject();
		     
		} catch (IOException e) {
			//TODO Handle error parsing result
		}
		
		return movieList;
	}
	
	public Movie parseMovie(JsonReader jsonReader){
		Movie movie = new Movie();
		
		try {
			jsonReader.beginObject();
			while (jsonReader.hasNext()) {
				String name = jsonReader.nextName();
				if (name.equals(JSON_KEY_TRACKID)) {
					movie.setId(jsonReader.nextLong());
				} else if (name.equals(JSON_KEY_ARTISTNAME)) {
					movie.setArtist(jsonReader.nextString());
				} else if (name.equals(JSON_KEY_TRACKNAME)) {
					movie.setTitle(jsonReader.nextString());
				} else if (name.equals(JSON_KEY_TRACKPRICE)) {
					movie.setPrice(jsonReader.nextDouble());
				} else if (name.equals(JSON_KEY_TRACKCOVER)) {
					movie.setCoverURL(jsonReader.nextString());
				} else {
					jsonReader.skipValue();
				}
			}
			jsonReader.endObject();
			
		} catch (IOException e) {
			//TODO Handle error parsing object
			movie = null;
		}
		
		return movie;
	}
	
}
