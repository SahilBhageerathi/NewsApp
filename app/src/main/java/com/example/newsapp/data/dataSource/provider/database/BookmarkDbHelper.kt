package com.example.newsapp.data.dataSource.provider.database


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.newsapp.data.dataSource.provider.BookmarkContract.BookmarkEntry


/**
 * Creates and manages the SQLite database file on disk.
 *
 * Data flow:
 *   ContentProvider → uses DbHelper → manages SQLiteDatabase file
 *
 * We use raw SQLite (not Room) because Content Providers work with
 * Cursors and ContentValues directly. Room adds an abstraction layer
 * that would fight against the provider pattern.
 */
class BookmarkDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        const val DATABASE_NAME = "bookmarks.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE ${BookmarkEntry.TABLE_NAME} (
                ${BookmarkEntry.COL_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${BookmarkEntry.COL_ARTICLE_ID} INTEGER NOT NULL,
                ${BookmarkEntry.COL_TITLE} TEXT NOT NULL,
                ${BookmarkEntry.COL_AUTHOR} TEXT,
                ${BookmarkEntry.COL_URL} TEXT NOT NULL,
                ${BookmarkEntry.COL_IMAGE_URL} TEXT,
                ${BookmarkEntry.COL_NEWS_SITE} TEXT,
                ${BookmarkEntry.COL_SUMMARY} TEXT,
                ${BookmarkEntry.COL_PUBLISHED_AT} TEXT,
                ${BookmarkEntry.COL_BOOKMARKED_AT} INTEGER NOT NULL DEFAULT (strftime('%s','now'))
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${BookmarkEntry.TABLE_NAME}")
        onCreate(db)
    }
}