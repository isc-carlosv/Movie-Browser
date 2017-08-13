package com.sample.android.moviebrowser.net

import java.io.BufferedInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

import org.apache.http.util.ByteArrayBuffer

import android.content.Context
import android.net.ConnectivityManager
import android.util.JsonReader


/**
 * @author Carlos Vasconcelos
 */
class ConnectionManager(private val context: Context, requestURL: String) {
    private var httpConnection: HttpURLConnection? = null
    private var url: URL? = null
    private var iStream: InputStream? = null
    private var jsonReader: JsonReader? = null


    init {

        try {
            url = URL(requestURL)

        } catch (e: MalformedURLException) {
            //TODO Handle malformed url
        }

    }

    private val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }


    fun requestJson(): JsonReader? {
        try {
            jsonReader = JsonReader(InputStreamReader(request(), ENCODING_UTF))
        } catch (e: UnsupportedEncodingException) {
            //TODO Handle wrong encoding
        }

        return jsonReader
    }

    fun request(): InputStream? {
        if (!isNetworkAvailable) {
            //TODO Handle Network not available
        }

        try {
            httpConnection = url!!.openConnection() as HttpURLConnection

            val responseCode = httpConnection!!.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                iStream = httpConnection!!.inputStream

            } else {
                //TODO Handle error response from service
            }

        } catch (ex: Exception) {
            //TODO Handle error in connection
        }

        return iStream
    }

    fun closeConnection() {
        try {
            if (iStream != null) {
                iStream!!.close()
            }
            if (httpConnection != null) {
                httpConnection!!.disconnect()
            }
        } catch (e: Exception) {
            //TODO Handle error closing inputStream
        }

    }


    fun requestImage(): ByteArrayBuffer? {
        var httpConnection: HttpURLConnection? = null
        val baf = ByteArrayBuffer(1024)
        var bis: BufferedInputStream? = null

        if (!isNetworkAvailable) {
            return null
        }

        try {
            httpConnection = url!!.openConnection() as HttpURLConnection

            val responseCode = httpConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                bis = BufferedInputStream(httpConnection.inputStream, 1024)

                var current = 0
                current = bis.read()
                while (current != -1) {
                    baf.append(current.toByte())
                    current = bis.read()
                }

            }

        } catch (ex: Exception) {

        } finally {
            try {
                if (bis != null) {
                    bis.close()
                }
            } catch (e: Exception) {
            }

            if (httpConnection != null) {
                httpConnection.disconnect()
            }
        }
        return baf
    }

    companion object {
        val ENCODING_UTF = "UTF-8"
    }

}
