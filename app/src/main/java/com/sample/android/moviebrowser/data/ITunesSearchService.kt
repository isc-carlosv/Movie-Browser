package com.sample.android.moviebrowser.data

import com.sample.android.moviebrowser.data.models.Result
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit


interface ITunesSearchService {
    @GET("search")
    fun search(@Query("term") term: String,
               @Query("media") media: String,
               @Query("entity") entity: String,
               @Query("limit") limit: String) : Observable<Result>

    companion object Factory {

        fun create(): ITunesSearchService =
                Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://itunes.apple.com/")
                    .build().create<ITunesSearchService>(ITunesSearchService::class.java)
    }
}