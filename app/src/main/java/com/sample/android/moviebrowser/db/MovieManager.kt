package com.sample.android.moviebrowser.db


import java.io.ByteArrayInputStream
import java.util.ArrayList
import com.sample.android.moviebrowser.R
import com.sample.android.moviebrowser.models.Movie

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.graphics.Bitmap
import android.graphics.BitmapFactory


/**
 * @author Carlos Vasconcelos
 */
class MovieManager(private val context: Context, private val dbManager: DBManager) {


    val moviesList: ArrayList<Movie>
        @Synchronized get() {
            val list = ArrayList<Movie>()

            val builder = SQLiteQueryBuilder()
            builder.tables = context.getString(R.string.sql_table_movie)

            var db: SQLiteDatabase? = dbManager.readableDatabase ?: return list
            if (!db!!.isOpen) {
                dbManager.openDB(db!!)
                db = dbManager.readableDatabase
            }

            val values = arrayOf<String>()
            val cursor = db!!.rawQuery(context.getString(R.string.sql_select_all_movies), values)

            if (cursor == null) {
                if (db != null && db.isOpen) {
                    db.close()
                }
                return list
            } else if (!cursor.moveToFirst()) {
                cursor.close()
                if (db != null && db.isOpen) {
                    db.close()
                }
                return list
            }

            val idMovieIndex = cursor.getColumnIndex(context.getString(R.string.sql_column_movie_idmovie))
            val titleIndex = cursor.getColumnIndex(context.getString(R.string.sql_column_movie_title))
            val artistIndex = cursor.getColumnIndex(context.getString(R.string.sql_column_movie_artist))
            val priceIndex = cursor.getColumnIndex(context.getString(R.string.sql_column_movie_price))
            val coverURLIndex = cursor.getColumnIndex(context.getString(R.string.sql_column_movie_coverURL))
            val coverIndex = cursor.getColumnIndex(context.getString(R.string.sql_column_movie_cover))

            do {
                val movie = Movie()
                movie.id = cursor.getLong(idMovieIndex)
                movie.title = cursor.getString(titleIndex)
                movie.artist = cursor.getString(artistIndex)
                movie.price = cursor.getDouble(priceIndex)
                movie.coverURL = cursor.getString(coverURLIndex)
                val imageByteArray = cursor.getBlob(coverIndex)

                var image: Bitmap? = null
                if (imageByteArray != null) {
                    var imageStream: ByteArrayInputStream? = ByteArrayInputStream(imageByteArray)
                    image = BitmapFactory.decodeStream(imageStream)
                    imageStream = null
                }
                movie.cover = image

                list.add(movie)
            } while (cursor.moveToNext())

            cursor.close()
            if (db != null && db.isOpen) {
                db.close()
            }

            return list
        }

    @Synchronized fun deleteMoviesList() {
        var db: SQLiteDatabase? = dbManager.readableDatabase ?: return
        if (!db!!.isOpen) {
            dbManager.openDB(db!!)
            db = dbManager.readableDatabase
        }

        val values = arrayOf<String>()
        db!!.delete(context.getString(R.string.sql_table_movie), "", values)

        if (db != null && db.isOpen) {
            db.close()
        }
    }


    fun saveMoviesList(list: ArrayList<Movie>) {
        deleteMoviesList()
        for (movie in list) {
            saveMovie(movie)
        }
    }

    @Synchronized private fun saveMovie(movie: Movie) {
        var db: SQLiteDatabase? = dbManager.writableDatabase ?: return
        val dataToInsert = ContentValues()
        dataToInsert.put(context.getString(R.string.sql_column_movie_idmovie), movie.id)
        dataToInsert.put(context.getString(R.string.sql_column_movie_title), movie.title)
        dataToInsert.put(context.getString(R.string.sql_column_movie_artist), movie.artist)
        dataToInsert.put(context.getString(R.string.sql_column_movie_price), movie.price)
        dataToInsert.put(context.getString(R.string.sql_column_movie_coverURL), movie.coverURL)

        if (!db!!.isOpen) {
            dbManager.openDB(db!!)
            db = dbManager.writableDatabase
        }
        if (db == null) {
            return
        }
        db.insertWithOnConflict(context.getString(R.string.sql_table_movie), null, dataToInsert,
                SQLiteDatabase.CONFLICT_IGNORE)
        if (db != null && db.isOpen) {
            db.close()
        }
    }

    @Synchronized fun saveCover(movie: Movie, imageByteArray: ByteArray) {
        var db: SQLiteDatabase? = dbManager.writableDatabase ?: return
        val dataToInsert = ContentValues()
        dataToInsert.put(context.getString(R.string.sql_column_movie_cover), imageByteArray)
        val whereValues = arrayOf(movie.id.toString())

        if (!db!!.isOpen) {
            dbManager.openDB(db!!)
            db = dbManager.writableDatabase
        }
        if (db == null) {
            return
        }
        db.update(context.getString(R.string.sql_table_movie), dataToInsert,
                context.getString(R.string.sql_update_movie_cover_whereclause), whereValues)
        if (db != null && db.isOpen) {
            db.close()
        }
    }
}
