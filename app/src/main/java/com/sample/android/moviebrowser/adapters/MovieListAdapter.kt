package com.sample.android.moviebrowser.adapters

import java.util.ArrayList

import com.sample.android.moviebrowser.MovieListActivity
import com.sample.android.moviebrowser.R
import com.sample.android.moviebrowser.data.models.Movie

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.movie_list_row.view.*


class MovieListAdapter(val activity: MovieListActivity, private var moviesList: ArrayList<Movie>)
    : RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>(), Filterable {

    private var filteredMoviesList: ArrayList<Movie>

    init {
        this.filteredMoviesList = moviesList
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.movie_list_row, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieListAdapter.MovieViewHolder?, position: Int) {
        holder?.onBind(filteredMoviesList[position])
    }

    override fun getItemCount(): Int = filteredMoviesList.size

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(movie : Movie){
            itemView.movieName?.text = movie.trackName
            itemView.movieArtist?.text = movie.artistName
            itemView.moviePrice?.text = itemView.context.getString(R.string.activity_table_price_currency) + movie.trackPrice.toString()

            Picasso.with(itemView.context)
                    .load(movie.artworkUrl100)
                    .placeholder(R.drawable.img_cover)
                    .into(itemView.artwork, object : Callback {
                        override fun onSuccess() {
                            itemView.progressBar?.visibility = View.GONE
                        }
                        override fun onError() {}
                    })
        }
    }

    fun updateList(movies: ArrayList<Movie>) {
        this.moviesList = movies
        this.filteredMoviesList.clear()
        this.filteredMoviesList.addAll(movies)
        this.notifyDataSetChanged()
    }

    fun updateList(movies: ArrayList<Movie>, sequence: Editable) {
        this.moviesList = movies
        this.filteredMoviesList.clear()
        this.filteredMoviesList.addAll(movies)
        this.filter.filter(sequence)
    }

    override fun getFilter(): Filter {

        val filter = object : Filter() {

            override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
                filteredMoviesList = results.values as ArrayList<Movie>
                notifyDataSetChanged()
            }

            @SuppressLint("DefaultLocale")
            override fun performFiltering(constraint : CharSequence): Filter.FilterResults {
                val results = Filter.FilterResults()

                val constraint = constraint.toString().toLowerCase()
                val filteredMovies = moviesList.indices
                        .map { moviesList[it] }
                        .filterTo(ArrayList<Movie>()) {
                            it.artistName!!.toLowerCase().contains(constraint) ||
                            it.trackName!!.toLowerCase().contains(constraint)
                        }

                results.count = filteredMovies.size
                results.values = filteredMovies

                return results
            }
        }

        return filter
    }
}
