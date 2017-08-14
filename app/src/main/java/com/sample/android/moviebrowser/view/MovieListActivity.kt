package com.sample.android.moviebrowser.view

import com.sample.android.moviebrowser.view.adapters.MovieListAdapter
import com.sample.android.moviebrowser.data.models.Movie

import android.os.Bundle
import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.sample.android.moviebrowser.MovieBrowserApplication
import com.sample.android.moviebrowser.R
import com.sample.android.moviebrowser.data.ITunesSearchService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_movie_list.*
import java.util.*
import javax.inject.Inject


class MovieListActivity : Activity() {

    @Inject
    lateinit var iTunesSearchService : ITunesSearchService

    private var listAdapter: MovieListAdapter? = null
    private var movieList = ArrayList<Movie>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        MovieBrowserApplication.dataServiceComponent.injectInto(this)

        listAdapter = MovieListAdapter(this, movieList)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.adapter = listAdapter

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(sequence: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            private var timer = Timer()
            private val DELAY: Long = 500

            override fun afterTextChanged(editable: Editable) {
                timer.cancel()
                timer = Timer()
                timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                if(editable.length > 3) {
                                    retrieveMovieList(editable.toString())
                                }
                            }
                        },
                        DELAY
                )
            }
        })
    }

    fun retrieveMovieList(query : String) {
        iTunesSearchService.search(query)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result -> displayMovieList(result.results)
                })
    }

    fun displayMovieList(list: ArrayList<Movie>) {
        movieList = list
        listAdapter!!.updateList(movieList)
        sortListByArtist()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.movie_list, menu)
        return true
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort_artist -> {
                sortListByArtist()
                return true
            }
            R.id.menu_sort_title -> {
                sortListByTitle()
                return true
            }
            R.id.menu_sort_price -> {
                sortListByPrice()
                return true
            }
            R.id.menu_sort_reverse -> {
                reverserCurrentSort()
                return true
            }
            else -> return super.onMenuItemSelected(featureId, item)
        }
    }

    fun sortListByArtist() {
        Collections.sort(movieList) { movie0, movie1 -> movie0.artistName!!.compareTo(movie1.artistName!!) }
        listAdapter!!.updateList(movieList, searchBox!!.text)
    }

    fun sortListByTitle() {
        Collections.sort(movieList) { movie0, movie1 -> movie0.trackName!!.compareTo(movie1.trackName!!) }
        listAdapter!!.updateList(movieList, searchBox!!.text)
    }

    fun sortListByPrice() {
        Collections.sort(movieList) { movie0, movie1 ->
            val priceCompare = movie0.trackPrice.compareTo(movie1.trackPrice)
            if (priceCompare != 0) {
                priceCompare
            } else {
                movie0.trackName!!.compareTo(movie1.trackName!!)
            }
        }
        listAdapter!!.updateList(movieList, searchBox!!.text)
    }

    fun reverserCurrentSort() {
        Collections.reverse(movieList)
        listAdapter!!.updateList(movieList, searchBox!!.text)
    }

}
