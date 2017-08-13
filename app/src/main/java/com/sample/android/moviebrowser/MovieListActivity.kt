package com.sample.android.moviebrowser

import java.util.ArrayList
import java.util.Collections

import com.sample.android.moviebrowser.adapters.MovieListAdapter
import com.sample.android.moviebrowser.db.DBManager
import com.sample.android.moviebrowser.data.models.Movie

import android.os.Bundle
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.LinearLayout
import com.sample.android.moviebrowser.data.DataModule
import com.sample.android.moviebrowser.data.ITunesSearchService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_movie_list.*


class MovieListActivity : Activity() {
    private var dbManager: DBManager? = null

    private var headerView: View? = null
    private var listAdapter: MovieListAdapter? = null
    private var movieList = ArrayList<Movie>()

    private var loadDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        dbManager = DBManager(this)

        //headerView = layoutInflater.inflate(R.layout.movie_list_header, null, true)

        movieList = ArrayList<Movie>()
        listAdapter = MovieListAdapter(this, movieList)
        //recyclerView.addHeaderView(headerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.adapter = listAdapter
        //registerForContextMenu(listView)

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(sequence: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                listAdapter!!.filter.filter(sequence)
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun afterTextChanged(arg0: Editable) {}
        })

        loadDialog = ProgressDialog(this)
    }


    override fun onStart() {
        super.onStart()

        retrieveMovieList()
        //displaySelectionDialog()
    }


    fun displaySelectionDialog() {
//        val builder = AlertDialog.Builder(this)
//        builder.setMessage(R.string.dialog_load_question)
//                .setPositiveButton(R.string.dialog_load_yes) { dialog, id -> retrieveMovieListFromDB() }
//                .setNegativeButton(R.string.dialog_load_download) { dialog, id -> retrieveMovieList() }
//
//        val dialog = builder.create()
//        dialog.show()
    }

    fun retrieveMovieList() {
        loadDialog = ProgressDialog.show(this, "", resources.getString(R.string.dialog_loading), true, true)
        //listAdapter.clear()

        ITunesSearchService.Factory.create().search("Rock", "movie", "movie", "50")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result ->updateMovieList(result.results)
                })

    }

//    fun retrieveMovieListFromDB() {
//        loadDialog = ProgressDialog.show(this, "", resources.getString(R.string.dialog_loading), true, true)
//        listAdapter!!.clear()
//        val fetchTask = ReadDBMovieListTask(this, dbManager!!)
//        fetchTask.execute()
//    }

    fun updateMovieList(list: ArrayList<Movie>) {
        movieList = list
        listAdapter!!.updateList(movieList!!)
        sortListByArtist()
        loadDialog!!.dismiss()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.movie_list, menu)
        return true
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                displaySelectionDialog()
                return true
            }
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

    override fun onCreateContextMenu(contextMenu: ContextMenu, view: View, menuInfo: ContextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, menuInfo)

        val info = menuInfo as AdapterContextMenuInfo
        if (info.targetView === headerView) {
            return
        }

        menuInflater.inflate(R.menu.movie_list_context_menu, contextMenu)
//        val movieTitle = (info.targetView.findViewById(R.id.movie_title) as TextView).text.toString()
//        contextMenu.setHeaderTitle(getString(R.string.context_menu_title_delete) + movieTitle +
//                getString(R.string.context_menu_title_delete2))
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        when (item.itemId) {
            R.id.context_menu_delete -> {
                deleteMovie(info.id)
                return true
            }

            else -> return super.onContextItemSelected(item)
        }
    }

    fun deleteMovie(viewID: Long) {
        movieList!!.removeAt(viewID.toInt())
        listAdapter!!.updateList(movieList!!)
    }

    fun sortListByArtist() {
        Collections.sort(movieList!!) { movie0, movie1 -> movie0.artistName!!.compareTo(movie1.artistName!!) }
        listAdapter!!.updateList(movieList!!, searchBox!!.text)
    }

    fun sortListByTitle() {
        Collections.sort(movieList!!) { movie0, movie1 -> movie0.trackName!!.compareTo(movie1.trackName!!) }
        listAdapter!!.updateList(movieList!!, searchBox!!.text)
    }

    fun sortListByPrice() {
        Collections.sort(movieList!!) { movie0, movie1 ->
            val priceCompare = movie0.trackPrice.toDouble().compareTo(movie1.trackPrice.toDouble())
            if (priceCompare != 0) {
                priceCompare
            } else {
                movie0.trackName!!.compareTo(movie1.trackName!!)
            }
        }
        listAdapter!!.updateList(movieList!!, searchBox!!.text)
    }

    fun reverserCurrentSort() {
        Collections.reverse(movieList!!)
        listAdapter!!.updateList(movieList!!, searchBox!!.text)
    }


}
