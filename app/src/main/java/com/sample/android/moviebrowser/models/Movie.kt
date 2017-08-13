package com.sample.android.moviebrowser.models

import android.graphics.Bitmap


/**
 * @author Carlos Vasconcelos
 */
class Movie {
    var id: Long = 0
    var coverURL: String? = null
    var artist: String? = null
    var title: String? = null
    var price: Double = 0.toDouble()

    @set:Synchronized var cover: Bitmap? = null

}
