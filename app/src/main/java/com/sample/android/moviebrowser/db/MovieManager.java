package com.sample.android.moviebrowser.db;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import com.sample.android.moviebrowser.R;
import com.sample.android.moviebrowser.models.Movie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * @author Carlos Vasconcelos
 *
 */
public class MovieManager {
	private DBManager dbManager;
	private Context context;
	
	public MovieManager(Context context, DBManager dbManager) {
		this.context = context;
		this.dbManager = dbManager;
	}

	
	public synchronized ArrayList<Movie> getMoviesList(){
		ArrayList<Movie> list = new ArrayList<Movie>();
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(context.getString(R.string.sql_table_movie));
		
		SQLiteDatabase db = dbManager.getReadableDatabase();
		if(db == null){
			return list;
		}
		if(!db.isOpen()){
			dbManager.openDB(db);
			db = dbManager.getReadableDatabase();
		}

		String[] values = {};
		Cursor cursor = db.rawQuery(context.getString(R.string.sql_select_all_movies), values);
		
	    if (cursor == null) {
	    	if (db != null && db.isOpen()){
	    		db.close();
	    	}
	        return list;
	    } else if (!cursor.moveToFirst()) {
	        cursor.close();
	    	if (db != null && db.isOpen()){
	    		db.close();
	    	}
	        return list;
	    }

	    int idMovieIndex = cursor.getColumnIndex(context.getString(R.string.sql_column_movie_idmovie));
	    int titleIndex = cursor.getColumnIndex(context.getString(R.string.sql_column_movie_title));
	    int artistIndex = cursor.getColumnIndex(context.getString(R.string.sql_column_movie_artist));
	    int priceIndex = cursor.getColumnIndex(context.getString(R.string.sql_column_movie_price));
	    int coverURLIndex = cursor.getColumnIndex(context.getString(R.string.sql_column_movie_coverURL));
	    int coverIndex = cursor.getColumnIndex(context.getString(R.string.sql_column_movie_cover));
	    
	    do{
		    Movie movie = new Movie();
		    movie.setId(cursor.getLong(idMovieIndex));
		    movie.setTitle(cursor.getString(titleIndex));
		    movie.setArtist(cursor.getString(artistIndex));
		    movie.setPrice(cursor.getDouble(priceIndex));
		    movie.setCoverURL(cursor.getString(coverURLIndex));
		    byte[] imageByteArray = cursor.getBlob(coverIndex);

		    Bitmap image = null;
		    if(imageByteArray != null){
			    ByteArrayInputStream imageStream = new ByteArrayInputStream(imageByteArray);
			    image = BitmapFactory.decodeStream(imageStream);
			    imageStream = null;
		    }
		    movie.setCover(image);
		    
		    list.add(movie);
	    } while(cursor.moveToNext());
	    
	    cursor.close();
    	if (db != null && db.isOpen()){
    		db.close();
    	}
		
		return list;
	}

	public synchronized void deleteMoviesList(){
		SQLiteDatabase db = dbManager.getReadableDatabase();
		if(db == null){
			return;
		}
		if(!db.isOpen()){
			dbManager.openDB(db);
			db = dbManager.getReadableDatabase();
		}

		String[] values = {};
		db.delete(context.getString(R.string.sql_table_movie), "", values);
		
		if (db != null && db.isOpen()){
    		db.close();
    	}
	}
	
    
    public void saveMoviesList(ArrayList<Movie> list){
    	deleteMoviesList();
    	for(Movie movie : list){
    		saveMovie(movie);
    	}
    }
    
	private synchronized void saveMovie(Movie movie){
		SQLiteDatabase db = dbManager.getWritableDatabase();
		if(db == null){
			return;
		}
		ContentValues dataToInsert = new ContentValues();
		dataToInsert.put(context.getString(R.string.sql_column_movie_idmovie), movie.getId());
		dataToInsert.put(context.getString(R.string.sql_column_movie_title), movie.getTitle());
		dataToInsert.put(context.getString(R.string.sql_column_movie_artist), movie.getArtist());
		dataToInsert.put(context.getString(R.string.sql_column_movie_price), movie.getPrice());
		dataToInsert.put(context.getString(R.string.sql_column_movie_coverURL), movie.getCoverURL());
		
		if(!db.isOpen()){
			dbManager.openDB(db);
			db = dbManager.getWritableDatabase();
		}
		if(db == null){
			return;
		}
		db.insertWithOnConflict(context.getString(R.string.sql_table_movie), null, dataToInsert,
				SQLiteDatabase.CONFLICT_IGNORE);
    	if (db != null && db.isOpen()){
    		db.close();
    	}
	}
	
	public synchronized void saveCover(Movie movie, byte[] imageByteArray){
		SQLiteDatabase db = dbManager.getWritableDatabase();
		if(db == null){
			return;
		}
		ContentValues dataToInsert = new ContentValues();
		dataToInsert.put(context.getString(R.string.sql_column_movie_cover), imageByteArray);
		String[] whereValues = {String.valueOf(movie.getId())};

		if(!db.isOpen()){
			dbManager.openDB(db);
			db = dbManager.getWritableDatabase();
		}
		if(db == null){
			return;
		}
		db.update(context.getString(R.string.sql_table_movie), dataToInsert,
				context.getString(R.string.sql_update_movie_cover_whereclause), whereValues);
    	if (db != null && db.isOpen()){
    		db.close();
    	}
	}
}
