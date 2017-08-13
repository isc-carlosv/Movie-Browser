package com.sample.android.moviebrowser.data

import android.content.Context
import android.net.ConnectivityManager


class DataModule(private val context: Context) {

    init {

    }

    private val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

    fun request() {

    }

}
