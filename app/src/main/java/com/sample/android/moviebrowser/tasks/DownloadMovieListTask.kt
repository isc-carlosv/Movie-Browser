package com.sample.android.moviebrowser.tasks

import java.util.ArrayList

import com.sample.android.moviebrowser.R
import com.sample.android.moviebrowser.MovieListActivity
import com.sample.android.moviebrowser.db.DBManager
import com.sample.android.moviebrowser.db.MovieManager
import com.sample.android.moviebrowser.net.ConnectionManager
import com.sample.android.moviebrowser.util.JsonParser
import android.os.AsyncTask

/**
 * @author Carlos Vasconcelos
 */
class DownloadMovieListTask(private val activity: MovieListActivity, private val dbManager: DBManager) : AsyncTask<Void, Void, Void>() {
    private var movieList: ArrayList<com.sample.android.moviebrowser.models.Movie>? = null

    private var connectionManager: ConnectionManager? = null

    init {
        movieList = ArrayList<com.sample.android.moviebrowser.models.Movie>()
    }

    override fun doInBackground(vararg params: Void): Void? {
        val parser = JsonParser()
        connectionManager = ConnectionManager(activity, activity.getString(R.string.service_url))
        movieList = parser.parseMovieList(connectionManager!!.requestJson())
        connectionManager!!.closeConnection()

        val movieManager = MovieManager(activity, dbManager)
        movieManager.saveMoviesList(movieList!!)

        return null
    }

    override fun onPostExecute(result: Void) {
        activity.updateMovieList(movieList!!)
    }


}
