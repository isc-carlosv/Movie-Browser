package com.sample.android.moviebrowser.data

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class DataModule {

    @Provides
    @Singleton
    fun initITunesSearchService() : ITunesSearchService =
        Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(ITunesSearchService.ITUNES_SERVICES_URL)
            .build().create<ITunesSearchService>(ITunesSearchService::class.java)

}
