package com.example.newsapp.data.dataSource.provider

import android.net.Uri

/**
 * BookmarkContract — The public API documentation for our Content Provider.
 *
 * Any app that wants to read our bookmarks needs to know:
 *   1. The AUTHORITY — unique name to find our provider
 *   2. The CONTENT_URI — address to query
 *   3. The COLUMN names — what fields exist
 *
 * This is like Retrofit endpoint definitions, but for Content Providers.
 * Just like ContactsContract told US how to query contacts,
 * this contract tells OTHER APPS how to query our bookmarks.
 */
object BookmarkContract {

    // Must match android:authorities in AndroidManifest.xml
    const val AUTHORITY = "com.example.newsapp.provider"

    val BASE_URI: Uri = Uri.parse("content://$AUTHORITY")

    object BookmarkEntry {
        const val TABLE_NAME = "bookmarks"

        // content://com.example.newsapp.provider/bookmarks
        val CONTENT_URI: Uri = Uri.withAppendedPath(BASE_URI, TABLE_NAME)

        // MIME types — tells Android what kind of data this URI returns
        // Directory = multiple rows, Item = single row
        const val CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd.$AUTHORITY.$TABLE_NAME"
        const val CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/vnd.$AUTHORITY.$TABLE_NAME"

        // Column names — map directly to SQLite columns
        const val COL_ID = "_id"
        const val COL_ARTICLE_ID = "article_id"    // links to Article.id from the API
        const val COL_TITLE = "title"
        const val COL_AUTHOR = "author"
        const val COL_URL = "url"
        const val COL_IMAGE_URL = "image_url"
        const val COL_NEWS_SITE = "news_site"
        const val COL_SUMMARY = "summary"
        const val COL_PUBLISHED_AT = "published_at"
        const val COL_BOOKMARKED_AT = "bookmarked_at"
    }
}