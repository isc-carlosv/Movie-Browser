package com.sample.android.moviebrowser.adapters

import java.io.ByteArrayInputStream
import java.util.ArrayList

import com.sample.android.moviebrowser.MovieListActivity
import com.sample.android.moviebrowser.R
import com.sample.android.moviebrowser.db.DBManager
import com.sample.android.moviebrowser.db.MovieManager
import com.sample.android.moviebrowser.net.ConnectionManager
import com.sample.android.moviebrowser.models.Movie

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

/**
 * @author Carlos Vasconcelos
 */
class MovieListArrayAdapter(private val activity: MovieListActivity, private var moviesList: ArrayList<Movie>?, private val dbManager: DBManager) : ArrayAdapter<Movie>(activity, R.layout.movie_list_row, moviesList), Filterable {
    private var filteredMoviesList: ArrayList<Movie>? = null

    init {
        this.filteredMoviesList = moviesList
    }

    fun updateList(movies: ArrayList<Movie>) {
        this.moviesList = movies
        this.filteredMoviesList!!.clear()
        this.filteredMoviesList!!.addAll(movies)
        this.notifyDataSetChanged()
    }

    fun updateList(movies: ArrayList<Movie>, sequence: Editable) {
        this.moviesList = movies
        this.filteredMoviesList!!.clear()
        this.filteredMoviesList!!.addAll(movies)
        this.filter.filter(sequence)
    }

    internal class MovieViewHolder {
        var albumCover: ImageView? = null
        var movieName: TextView? = null
        var movieArtist: TextView? = null
        var moviePrice: TextView? = null

        var progressBar: ProgressBar? = null

        var task: CoverLoaderTask? = null

        fun cancelTask() {
            if (task != null) {
                task!!.cancel(false)
            }
            this.task = null
        }

        fun setCurrentTask(newTask: CoverLoaderTask) {
            if (task != null) {
                if (newTask.movie.id != task!!.movie.id) {
                    cancelTask()
                    this.task = newTask
                    this.task!!.execute()
                }
            } else {
                this.task = newTask
                this.task!!.execute()
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: MovieViewHolder

        if (convertView == null) {
            val inflater = activity.layoutInflater
            convertView = inflater.inflate(R.layout.movie_list_row, parent, false)

            viewHolder = MovieViewHolder()
            viewHolder.albumCover = convertView!!.findViewById(R.id.album_cover) as ImageView
            viewHolder.movieName = convertView.findViewById(R.id.movie_title) as TextView
            viewHolder.movieArtist = convertView.findViewById(R.id.movie_artist) as TextView
            viewHolder.moviePrice = convertView.findViewById(R.id.movie_price) as TextView
            viewHolder.progressBar = convertView.findViewById(R.id.progress_bar) as ProgressBar
            convertView.tag = viewHolder

        } else {
            viewHolder = convertView.tag as MovieViewHolder
        }

        val movie = filteredMoviesList!![position]

        viewHolder.movieName!!.text = movie.title
        viewHolder.movieArtist!!.text = movie.artist
        viewHolder.moviePrice!!.text = activity.getString(R.string.activity_table_price_currency) + movie.price.toString()

        if (movie.cover != null) {
            viewHolder.albumCover!!.setImageBitmap(movie.cover)
        } else {
            viewHolder.albumCover!!.setImageResource(R.drawable.img_cover)
            viewHolder.progressBar!!.visibility = View.VISIBLE
            val imageLoader = CoverLoaderTask(viewHolder, movie)
            viewHolder.setCurrentTask(imageLoader)
        }

        return convertView
    }

    override fun getCount(): Int {
        return filteredMoviesList!!.size
    }


    override fun getFilter(): Filter {

        val filter = object : Filter() {

            override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
                filteredMoviesList = results.values as ArrayList<Movie>
                notifyDataSetChanged()
            }

            @SuppressLint("DefaultLocale")
            override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
                var constraint = constraint
                val results = Filter.FilterResults()
                val filteredMovies = ArrayList<Movie>()

                constraint = constraint.toString().toLowerCase()
                for (i in moviesList!!.indices) {
                    val movie = moviesList!![i]
                    if (movie.artist!!.toLowerCase().contains(constraint.toString()) || movie.title!!.toLowerCase().contains(constraint.toString())) {
                        filteredMovies.add(movie)
                    }
                }

                results.count = filteredMovies.size
                results.values = filteredMovies

                return results
            }
        }

        return filter
    }


    inner class CoverLoaderTask internal constructor(internal var viewHolder: MovieViewHolder, movie: Movie) : AsyncTask<Void, Void, Void>() {
        var movie: Movie
            internal set

        init {
            this.movie = movie
        }

        override fun doInBackground(vararg params: Void): Void? {
            if (movie.cover == null) {
                val connectionManager = ConnectionManager(activity, movie.coverURL!!)
                val imageByteArray = connectionManager.requestImage()!!.buffer()
                val imageStream = ByteArrayInputStream(imageByteArray)
                val cover = BitmapFactory.decodeStream(imageStream)
                movie.cover = cover

                val movieManager = MovieManager(activity, dbManager)
                movieManager.saveCover(movie, imageByteArray)
            }

            return null
        }

        override fun onPostExecute(result: Void) {
            if (viewHolder.albumCover != null) {
                viewHolder.progressBar!!.visibility = View.GONE
                viewHolder.albumCover!!.setImageBitmap(movie.cover)
            }
        }
    }

}
