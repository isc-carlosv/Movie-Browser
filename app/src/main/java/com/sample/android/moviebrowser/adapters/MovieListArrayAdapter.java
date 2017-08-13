package com.sample.android.moviebrowser.adapters;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import com.sample.android.moviebrowser.MovieListActivity;
import com.sample.android.moviebrowser.R;
import com.sample.android.moviebrowser.db.DBManager;
import com.sample.android.moviebrowser.db.MovieManager;
import com.sample.android.moviebrowser.net.ConnectionManager;
import com.sample.android.moviebrowser.models.Movie;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author Carlos Vasconcelos
 *
 */
public class MovieListArrayAdapter extends ArrayAdapter<Movie> implements Filterable{
	private DBManager dbManager;
	private final MovieListActivity activity;
	private ArrayList<Movie> filteredMoviesList;
	private ArrayList<Movie> moviesList;

	public MovieListArrayAdapter(MovieListActivity activity, ArrayList<Movie> movies, DBManager dbManager) {
		super(activity, R.layout.movie_list_row, movies);
		this.activity = activity;
		this.moviesList = movies;
		this.filteredMoviesList = movies;
		this.dbManager = dbManager;
	}

	public void updateList(ArrayList<Movie> movies) {
		this.moviesList = movies;
		this.filteredMoviesList.clear();
		this.filteredMoviesList.addAll(movies);
		this.notifyDataSetChanged();
	}
	
	public void updateList(ArrayList<Movie> movies, Editable sequence) {
		this.moviesList = movies;
		this.filteredMoviesList.clear();
		this.filteredMoviesList.addAll(movies);
		this.getFilter().filter(sequence);
	}
	
	static class MovieViewHolder {
		ImageView albumCover;
		TextView movieName;
		TextView movieArtist;
		TextView moviePrice;
		
		ProgressBar progressBar;
		
		CoverLoaderTask task;
		
		public void cancelTask(){
			if(task != null){
				task.cancel(false);
			}
			this.task = null;
		}
		
		public void setTask(CoverLoaderTask newTask){
			if(task != null){
				if(newTask.getMovie().getId() != task.getMovie().getId()){
					cancelTask();
					this.task = newTask;
					this.task.execute();
				}
			} else {
				this.task = newTask;
				this.task.execute();
			}
		}
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final MovieViewHolder viewHolder;
		
		if(convertView == null){
			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.movie_list_row, parent, false);
			
			viewHolder = new MovieViewHolder();
			viewHolder.albumCover = (ImageView)convertView.findViewById(R.id.album_cover);
			viewHolder.movieName = (TextView)convertView.findViewById(R.id.movie_title);
			viewHolder.movieArtist = (TextView)convertView.findViewById(R.id.movie_artist);
			viewHolder.moviePrice = (TextView)convertView.findViewById(R.id.movie_price);
			viewHolder.progressBar = (ProgressBar)convertView.findViewById(R.id.progress_bar);
			convertView.setTag(viewHolder);
		
		} else {
			viewHolder = (MovieViewHolder) convertView.getTag();
		}
		
		final Movie movie = filteredMoviesList.get(position);
		
		viewHolder.movieName.setText(movie.getTitle());
		viewHolder.movieArtist.setText(movie.getArtist());
		viewHolder.moviePrice.setText(activity.getString(R.string.activity_table_price_currency) + String.valueOf(movie.getPrice()));
		
		if(movie.getCover() != null){
			viewHolder.albumCover.setImageBitmap(movie.getCover());
		} else {
			viewHolder.albumCover.setImageResource(R.drawable.img_cover);
			viewHolder.progressBar.setVisibility(View.VISIBLE);
			CoverLoaderTask imageLoader = new CoverLoaderTask(viewHolder, movie);
			viewHolder.setTask(imageLoader);
		}
		
		return convertView;
	}
	
	@Override
	public int getCount() {
		return filteredMoviesList.size();
	}
	
	
	@Override
	public Filter getFilter() {

		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				filteredMoviesList = (ArrayList<Movie>) results.values;
				notifyDataSetChanged();
			}

			@SuppressLint("DefaultLocale")
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				ArrayList<Movie> filteredMovies = new ArrayList<Movie>();

				constraint = constraint.toString().toLowerCase();
				for (int i = 0; i < moviesList.size(); i++) {
					Movie movie = moviesList.get(i);
					if (movie.getArtist().toLowerCase().contains(constraint.toString()) ||
							movie.getTitle().toLowerCase().contains(constraint.toString()))  {
						filteredMovies.add(movie);
					}
				}

				results.count = filteredMovies.size();
				results.values = filteredMovies;
				
				return results;
			}
		};

		return filter;
	}
	

	
	private class CoverLoaderTask extends AsyncTask<Void, Void, Void> {
		MovieViewHolder viewHolder;
		Movie movie;
		
	    public CoverLoaderTask(MovieViewHolder viewHolder, Movie movie) {
	        super();
	        this.viewHolder = viewHolder;
	        this.movie = movie;
	    }

		@Override
		protected Void doInBackground(Void... params) {
			if(movie.getCover() == null){
				ConnectionManager connectionManager = new ConnectionManager(activity, movie.getCoverURL());
			    byte[] imageByteArray = connectionManager.requestImage().buffer();
			    ByteArrayInputStream imageStream = new ByteArrayInputStream(imageByteArray);
			    Bitmap cover = BitmapFactory.decodeStream(imageStream);
				movie.setCover(cover);
				
				MovieManager movieManager = new MovieManager(activity, dbManager);
				movieManager.saveCover(movie, imageByteArray);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if(viewHolder.albumCover != null){
				viewHolder.progressBar.setVisibility(View.GONE);
				viewHolder.albumCover.setImageBitmap(movie.getCover());
			}
		}
		
		public Movie getMovie(){
			return this.movie;
		}
	}
	
}
