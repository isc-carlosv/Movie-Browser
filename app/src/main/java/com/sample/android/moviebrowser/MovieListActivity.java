package com.sample.android.moviebrowser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.sample.android.moviebrowser.adapters.MovieListArrayAdapter;
import com.sample.android.moviebrowser.db.DBManager;
import com.sample.android.moviebrowser.models.Movie;
import com.sample.android.moviebrowser.tasks.DownloadMovieListTask;
import com.sample.android.moviebrowser.tasks.ReadDBMovieListTask;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Carlos Vasconcelos
 *
 */
public class MovieListActivity extends Activity {
	private DBManager dbManager;
	
	private View headerView;
	private ListView listView;
	private MovieListArrayAdapter listAdapter;
	private ArrayList<Movie> movieList;
	
	private ProgressDialog loadDialog;
	
	private EditText searchBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_list);

		dbManager = new DBManager(this);
		
		headerView = getLayoutInflater().inflate(R.layout.movie_list_header, null, true);
		
		movieList = new ArrayList<Movie>();
		listView = (ListView) findViewById(R.id.list_view);
		listAdapter = new MovieListArrayAdapter(this, movieList, dbManager);
		listView.addHeaderView(headerView);
		listView.setAdapter(listAdapter);
		registerForContextMenu(listView);
		
		searchBox = (EditText)findViewById(R.id.search_box);
		searchBox.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence sequence, int arg1, int arg2, int arg3) {
				listAdapter.getFilter().filter(sequence);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) { }

			@Override
			public void afterTextChanged(Editable arg0) {}
		});
		
		loadDialog = new ProgressDialog(this);
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		displaySelectionDialog();
	}
	
	
	public void displaySelectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_load_question)
               .setPositiveButton(R.string.dialog_load_yes, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   retrieveMovieListFromDB();
                   }
               })
               .setNegativeButton(R.string.dialog_load_download, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   retrieveMovieList();
                   }
               });

		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public void retrieveMovieList(){
		loadDialog = ProgressDialog.show(this, "", getResources().getString(R.string.dialog_loading), true, true);
		listAdapter.clear();
		DownloadMovieListTask fetchTask = new DownloadMovieListTask(this, dbManager);
		fetchTask.execute();
	}
	
	public void retrieveMovieListFromDB(){
		loadDialog = ProgressDialog.show(this, "", getResources().getString(R.string.dialog_loading), true, true);
		listAdapter.clear();
		ReadDBMovieListTask fetchTask = new ReadDBMovieListTask(this, dbManager);
		fetchTask.execute();
	}
	
	public void updateMovieList(ArrayList<Movie> list){
		movieList = list;
		listAdapter.updateList(movieList);
		sortListByArtist();
		loadDialog.dismiss();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.movie_list, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_refresh :
				displaySelectionDialog();
				return true;
			case R.id.menu_sort_artist :
				sortListByArtist();
				return true;
			case R.id.menu_sort_title :
				sortListByTitle();
				return true;
			case R.id.menu_sort_price :
				sortListByPrice();
				return true;
			case R.id.menu_sort_reverse :
				reverserCurrentSort();
				return true;
				
			default :
				return super.onMenuItemSelected(featureId, item);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(contextMenu, view, menuInfo);
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		if(info.targetView == headerView){
			return;
		}
		
		getMenuInflater().inflate(R.menu.movie_list_context_menu, contextMenu);
		String movieTitle = ((TextView)info.targetView.findViewById(R.id.movie_title)).getText().toString();
		contextMenu.setHeaderTitle(getString(R.string.context_menu_title_delete) + movieTitle +
				getString(R.string.context_menu_title_delete2));
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.context_menu_delete:
	            deleteMovie(info.id);
	            return true;

	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	public void deleteMovie(long viewID){
		movieList.remove((int) viewID);
		listAdapter.updateList(movieList);
	}

	public void sortListByArtist(){
		Collections.sort(movieList, new Comparator<Movie>() {
		    public int compare(Movie movie0, Movie movie1) {
		        return movie0.getArtist().compareTo(movie1.getArtist());
		    }
		});
		listAdapter.updateList(movieList, searchBox.getText());
	}
	
	public void sortListByTitle(){
		Collections.sort(movieList, new Comparator<Movie>() {
		    public int compare(Movie movie0, Movie movie1) {
		        return movie0.getTitle().compareTo(movie1.getTitle());
		    }
		});
		listAdapter.updateList(movieList, searchBox.getText());
	}
	
	public void sortListByPrice(){
		Collections.sort(movieList, new Comparator<Movie>() {
		    public int compare(Movie movie0, Movie movie1) {
		    	int priceCompare = ((Double) movie0.getPrice()).compareTo((Double) movie1.getPrice());
		    	if(priceCompare != 0){
		    		return priceCompare;
		    	} else{
		    		return movie0.getTitle().compareTo(movie1.getTitle());
		    	}
		    }
		});
		listAdapter.updateList(movieList, searchBox.getText());
	}
	
	public void reverserCurrentSort(){
		Collections.reverse(movieList);
		listAdapter.updateList(movieList, searchBox.getText());
	}

	
}
