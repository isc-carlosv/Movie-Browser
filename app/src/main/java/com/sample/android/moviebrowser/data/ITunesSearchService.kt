package com.sample.android.moviebrowser.data

import com.sample.android.moviebrowser.data.models.Result
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface ITunesSearchService {

    companion object {
        val ITUNES_SERVICES_URL = "https://itunes.apple.com/"
    }

    @GET("search")
    fun search(@Query("term") term: String,
               @Query("media") media: String = "movie",
               @Query("entity") entity: String = "movie",
               @Query("limit") limit: String = "50") : Observable<Result>
}