package com.sample.android.moviebrowser.data.models

import java.util.ArrayList

class Movie {
    var trackId: Long = 0
    var artworkUrl100: String? = null
    var artistName: String? = null
    var trackName: String? = null
    var trackPrice: Double = 0.toDouble()
}

data class Result (val resultCount: Int, val results: ArrayList<Movie>)
