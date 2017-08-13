package com.sample.android.moviebrowser.tasks

import java.util.ArrayList

import com.sample.android.moviebrowser.MovieListActivity
import com.sample.android.moviebrowser.db.DBManager
import com.sample.android.moviebrowser.db.MovieManager
import com.sample.android.moviebrowser.models.Movie

import android.os.AsyncTask

/**
 * @author Carlos Vasconcelos
 */
class ReadDBMovieListTask(private val activity: MovieListActivity, private val dbManager: DBManager) : AsyncTask<Void, Void, Void>() {
    private var movieList: ArrayList<Movie>? = null


    init {
        movieList = ArrayList<Movie>()
    }


    override fun doInBackground(vararg params: Void): Void? {
        val movieManager = MovieManager(activity, dbManager)
        movieList = movieManager.moviesList

        return null
    }

    override fun onPostExecute(result: Void) {
        activity.updateMovieList(movieList!!)
    }


}
