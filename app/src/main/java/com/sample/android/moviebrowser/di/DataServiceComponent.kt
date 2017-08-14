package com.sample.android.moviebrowser.di

import com.sample.android.moviebrowser.data.DataModule
import com.sample.android.moviebrowser.view.MovieListActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(DataModule::class))
interface DataServiceComponent {
    fun injectInto(movieListActivity: MovieListActivity)
}