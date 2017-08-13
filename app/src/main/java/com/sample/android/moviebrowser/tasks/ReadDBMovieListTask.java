package com.sample.android.moviebrowser.tasks;

import java.util.ArrayList;

import com.sample.android.moviebrowser.MovieListActivity;
import com.sample.android.moviebrowser.db.DBManager;
import com.sample.android.moviebrowser.db.MovieManager;
import com.sample.android.moviebrowser.models.Movie;

import android.os.AsyncTask;

/**
 * @author Carlos Vasconcelos
 *
 */
public class ReadDBMovieListTask extends AsyncTask<Void, Void, Void> {
	private DBManager dbManager;
	private MovieListActivity activity;
	private ArrayList<Movie> movieList;
	
	
	public ReadDBMovieListTask(MovieListActivity activity, DBManager dbManager) {
		this.activity = activity;
		this.dbManager = dbManager;
		movieList = new ArrayList<Movie>();
	}

	
	@Override
	protected Void doInBackground(Void... params) {
		MovieManager movieManager = new MovieManager(activity, dbManager);
		movieList = movieManager.getMoviesList();
		
		return null;
	}

    protected void onPostExecute(Void result) {
    	activity.updateMovieList(movieList);
    }



}
