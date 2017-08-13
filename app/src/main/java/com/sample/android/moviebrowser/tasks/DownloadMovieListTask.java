package com.sample.android.moviebrowser.tasks;

import java.util.ArrayList;

import com.sample.android.moviebrowser.R;
import com.sample.android.moviebrowser.MovieListActivity;
import com.sample.android.moviebrowser.db.DBManager;
import com.sample.android.moviebrowser.db.MovieManager;
import com.sample.android.moviebrowser.net.ConnectionManager;
import com.sample.android.moviebrowser.util.JsonParser;
import android.os.AsyncTask;

/**
 * @author Carlos Vasconcelos
 *
 */
public class DownloadMovieListTask extends AsyncTask<Void, Void, Void> {
	private DBManager dbManager;
	private MovieListActivity activity;
	private ArrayList<com.sample.android.moviebrowser.models.Movie> movieList;
	
	private ConnectionManager connectionManager;
	
	public DownloadMovieListTask(MovieListActivity activity, DBManager dbManager) {
		this.activity = activity;
		this.dbManager = dbManager;
		movieList = new ArrayList<com.sample.android.moviebrowser.models.Movie>();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		JsonParser parser = new JsonParser();
		connectionManager = new ConnectionManager(activity, activity.getString(R.string.service_url));
		movieList = parser.parseMovieList(connectionManager.requestJson());
		connectionManager.closeConnection();
		
		MovieManager movieManager = new MovieManager(activity, dbManager);
		movieManager.saveMoviesList(movieList);
		
		return null;
	}

    protected void onPostExecute(Void result) {
    	activity.updateMovieList(movieList);
    }



}
