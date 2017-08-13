package com.sample.android.moviebrowser.db

import com.sample.android.moviebrowser.R

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DBManager(private val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    init {

        testDB()
    }

    private fun testDB() {
        val db = readableDatabase
        db.close()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(context.getString(R.string.sql_createtable_movie))
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(context.getString(R.string.sql_droptable_movie))
        onCreate(db)
    }

    fun openDB(db: SQLiteDatabase) {
        var db = db
        if (!db.isOpen) {
            db = context.openOrCreateDatabase(context.getString(R.string.sql_db_directory),
                    SQLiteDatabase.OPEN_READWRITE, null)
        }
    }

    companion object {
        private val DB_NAME = "movieBrowser"
        private val DB_VERSION = 1
    }

}
