package com.example.newsapp.data.dataSource.provider


import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.newsapp.data.dataSource.provider.BookmarkContract.BookmarkEntry
import com.example.newsapp.data.dataSource.provider.database.BookmarkDbHelper


/**
 * BookmarkContentProvider — The gatekeeper for our bookmarks data.
 *
 * THIS is the Content Provider side. When you built the contacts feature,
 * you were the CLIENT using ContentResolver to talk to someone else's provider.
 * Now YOU are the provider. Other apps will use ContentResolver to talk to YOU.
 *
 * How it works:
 *   Other app calls: contentResolver.query(
 *       "content://com.example.newsapp.provider/bookmarks", ...
 *   )
 *       ↓
 *   Android sees authority "com.example.newsapp.provider"
 *       ↓
 *   Android routes the call to THIS class
 *       ↓
 *   UriMatcher figures out: bookmarks table? single row?
 *       ↓
 *   We query our SQLite database and return a Cursor
 *
 * You control everything:
 *   - Which tables to expose (only bookmarks, not internal app data)
 *   - Which operations to allow (query/insert/delete/update)
 *   - Who can access (exported=true/false in manifest)
 */
class BookmarkContentProvider : ContentProvider() {

    private lateinit var dbHelper: BookmarkDbHelper

    companion object {
        // URI codes — the router uses these to identify what was requested
        private const val BOOKMARKS = 100       // content://.../bookmarks
        private const val BOOKMARK_ID = 101     // content://.../bookmarks/42

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(BookmarkContract.AUTHORITY, BookmarkEntry.TABLE_NAME, BOOKMARKS)
            addURI(BookmarkContract.AUTHORITY, "${BookmarkEntry.TABLE_NAME}/#", BOOKMARK_ID)
        }
    }

    // ════════════════════════════════════════════
    //  LIFECYCLE
    // ════════════════════════════════════════════

    override fun onCreate(): Boolean {
        dbHelper = BookmarkDbHelper(context!!)
        return true
    }

    // ════════════════════════════════════════════
    //  QUERY — other apps read our bookmarks
    // ════════════════════════════════════════════

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        val proj = projection?.map { it }?.toTypedArray()
        val args = selectionArgs?.map { it }?.toTypedArray()

        val cursor = when (uriMatcher.match(uri)) {
            BOOKMARKS -> db.query(
                BookmarkEntry.TABLE_NAME,
                proj, selection, args, null, null,
                sortOrder ?: "${BookmarkEntry.COL_BOOKMARKED_AT} DESC"
            )
            BOOKMARK_ID -> {
                val id = ContentUris.parseId(uri)
                db.query(
                    BookmarkEntry.TABLE_NAME,
                    proj,
                    "${BookmarkEntry.COL_ID} = ?",
                    arrayOf(id.toString()),
                    null, null, sortOrder
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        // Tell the cursor to watch for changes so UI auto-refreshes
        cursor?.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    // ════════════════════════════════════════════
    //  INSERT — save a new bookmark
    // ════════════════════════════════════════════

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (uriMatcher.match(uri) != BOOKMARKS) {
            throw IllegalArgumentException("Insert not supported for: $uri")
        }

        val db = dbHelper.writableDatabase
        val id = db.insert(BookmarkEntry.TABLE_NAME, null, values)
        if (id == -1L) return null

        // Notify anyone watching this URI that data changed
        context!!.contentResolver.notifyChange(uri, null)
        return ContentUris.withAppendedId(BookmarkEntry.CONTENT_URI, id)
    }

    // ════════════════════════════════════════════
    //  DELETE — remove a bookmark
    // ════════════════════════════════════════════

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val db = dbHelper.writableDatabase
        val args = selectionArgs?.map { it }?.toTypedArray()

        val rowsDeleted = when (uriMatcher.match(uri)) {
            BOOKMARKS -> db.delete(BookmarkEntry.TABLE_NAME, selection, args)
            BOOKMARK_ID -> {
                val id = ContentUris.parseId(uri)
                db.delete(
                    BookmarkEntry.TABLE_NAME,
                    "${BookmarkEntry.COL_ID} = ?",
                    arrayOf(id.toString())
                )
            }
            else -> throw IllegalArgumentException("Delete not supported for: $uri")
        }

        if (rowsDeleted > 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return rowsDeleted
    }

    // ════════════════════════════════════════════
    //  UPDATE — modify a bookmark
    // ════════════════════════════════════════════

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val db = dbHelper.writableDatabase
        val args = selectionArgs?.map { it }?.toTypedArray()

        val rowsUpdated = when (uriMatcher.match(uri)) {
            BOOKMARKS -> db.update(BookmarkEntry.TABLE_NAME, values, selection, args)
            BOOKMARK_ID -> {
                val id = ContentUris.parseId(uri)
                db.update(
                    BookmarkEntry.TABLE_NAME, values,
                    "${BookmarkEntry.COL_ID} = ?",
                    arrayOf(id.toString())
                )
            }
            else -> throw IllegalArgumentException("Update not supported for: $uri")
        }

        if (rowsUpdated > 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return rowsUpdated
    }

    // ════════════════════════════════════════════
    //  GET TYPE — return MIME type for a URI
    // ════════════════════════════════════════════

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            BOOKMARKS -> BookmarkEntry.CONTENT_TYPE
            BOOKMARK_ID -> BookmarkEntry.CONTENT_ITEM_TYPE
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}