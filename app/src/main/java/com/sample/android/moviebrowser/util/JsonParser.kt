package com.sample.android.moviebrowser.util

import java.io.IOException
import java.util.ArrayList

import android.util.JsonReader
import android.util.JsonToken

import com.sample.android.moviebrowser.models.Movie


/**
 * @author Carlos Vasconcelos
 */
class JsonParser {

    fun parseMovieList(jsonReader: JsonReader): ArrayList<Movie> {
        val movieList = ArrayList<Movie>()

        try {
            jsonReader.beginObject()
            while (jsonReader.hasNext()) {
                val name = jsonReader.nextName()
                if (name == JSON_KEY_RESULTS && jsonReader.peek() != JsonToken.NULL) {

                    jsonReader.beginArray()
                    while (jsonReader.hasNext()) {
                        val movie = parseMovie(jsonReader)
                        if (movie != null) {
                            movieList.add(movie)
                        }
                    }
                    jsonReader.endArray()

                } else {
                    jsonReader.skipValue()
                }
            }
            jsonReader.endObject()

        } catch (e: IOException) {
            //TODO Handle error parsing result
        }

        return movieList
    }

    fun parseMovie(jsonReader: JsonReader): Movie? {
        var movie: Movie? = Movie()

        try {
            jsonReader.beginObject()
            while (jsonReader.hasNext()) {
                val name = jsonReader.nextName()
                if (name == JSON_KEY_TRACKID) {
                    movie!!.id = jsonReader.nextLong()
                } else if (name == JSON_KEY_ARTISTNAME) {
                    movie!!.artist = jsonReader.nextString()
                } else if (name == JSON_KEY_TRACKNAME) {
                    movie!!.title = jsonReader.nextString()
                } else if (name == JSON_KEY_TRACKPRICE) {
                    movie!!.price = jsonReader.nextDouble()
                } else if (name == JSON_KEY_TRACKCOVER) {
                    movie!!.coverURL = jsonReader.nextString()
                } else {
                    jsonReader.skipValue()
                }
            }
            jsonReader.endObject()

        } catch (e: IOException) {
            //TODO Handle error parsing object
            movie = null
        }

        return movie
    }

    companion object {
        val JSON_KEY_RESULTCOUNT = "resultCount"
        val JSON_KEY_RESULTS = "results"
        val JSON_KEY_TRACKID = "trackId"
        val JSON_KEY_ARTISTNAME = "artistName"
        val JSON_KEY_TRACKNAME = "trackName"
        val JSON_KEY_TRACKPRICE = "trackPrice"
        val JSON_KEY_TRACKCOVER = "artworkUrl100"
    }

}
