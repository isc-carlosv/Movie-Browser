package com.sample.android.moviebrowser

import android.app.Application
import com.sample.android.moviebrowser.di.DaggerDataServiceComponent
import com.sample.android.moviebrowser.di.DataServiceComponent


class MovieBrowserApplication : Application() {

    companion object {
        lateinit var dataServiceComponent: DataServiceComponent
    }

    override fun onCreate() {
        super.onCreate()

        dataServiceComponent = DaggerDataServiceComponent.builder().build()
    }
}