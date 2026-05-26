package com.example.newsapp.data.repo


import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import com.example.newsapp.data.dataSource.provider.BookmarkContract.BookmarkEntry
import com.example.newsapp.domain.model.Bookmark
import com.example.newsapp.data.dto.BookmarkDto
import com.example.newsapp.data.mappers.toBookmarkList
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repo.BookmarkRepo

/**
 * Notice something interesting here?
 *
 * This repo uses ContentResolver — the exact same way you used it
 * for contacts. The difference is the URI points to OUR OWN provider
 * instead of the system contacts provider.
 *
 * From the client side, there's NO difference between reading your
 * own provider and reading someone else's. ContentResolver doesn't
 * care. It just routes based on the URI authority.
 *
 * This also means: if another app on the device knows our authority
 * ("com.example.newsapp.provider") and we set exported=true in the
 * manifest, they could run this exact same code to read our bookmarks.
 */
class BookmarkRepoImpl(
    private val contentResolver: ContentResolver
) : BookmarkRepo {

    override suspend fun getBookmarks(): List<Bookmark> {
        val dtos = mutableListOf<BookmarkDto>()

        contentResolver.query(
            BookmarkEntry.CONTENT_URI,
            null, null, null,
            "${BookmarkEntry.COL_BOOKMARKED_AT} DESC"
        )?.use { cursor ->
            val idIdx = cursor.getColumnIndexOrThrow(BookmarkEntry.COL_ID)
            val articleIdIdx = cursor.getColumnIndexOrThrow(BookmarkEntry.COL_ARTICLE_ID)
            val titleIdx = cursor.getColumnIndexOrThrow(BookmarkEntry.COL_TITLE)
            val authorIdx = cursor.getColumnIndexOrThrow(BookmarkEntry.COL_AUTHOR)
            val urlIdx = cursor.getColumnIndexOrThrow(BookmarkEntry.COL_URL)
            val imgIdx = cursor.getColumnIndexOrThrow(BookmarkEntry.COL_IMAGE_URL)
            val siteIdx = cursor.getColumnIndexOrThrow(BookmarkEntry.COL_NEWS_SITE)
            val summaryIdx = cursor.getColumnIndexOrThrow(BookmarkEntry.COL_SUMMARY)
            val pubIdx = cursor.getColumnIndexOrThrow(BookmarkEntry.COL_PUBLISHED_AT)
            val bookIdx = cursor.getColumnIndexOrThrow(BookmarkEntry.COL_BOOKMARKED_AT)

            while (cursor.moveToNext()) {
                dtos.add(
                    BookmarkDto(
                        id = cursor.getLong(idIdx),
                        articleId = cursor.getInt(articleIdIdx),
                        title = cursor.getString(titleIdx),
                        author = cursor.getString(authorIdx),
                        url = cursor.getString(urlIdx),
                        imageUrl = cursor.getString(imgIdx),
                        newsSite = cursor.getString(siteIdx),
                        summary = cursor.getString(summaryIdx),
                        publishedAt = cursor.getString(pubIdx),
                        bookmarkedAt = cursor.getLong(bookIdx)
                    )
                )
            }
        }

        return dtos.toBookmarkList()
    }

    override suspend fun addBookmark(article: Article) {
        val values = ContentValues().apply {
            put(BookmarkEntry.COL_ARTICLE_ID, article.id)
            put(BookmarkEntry.COL_TITLE, article.title)
            put(BookmarkEntry.COL_AUTHOR, article.author)
            put(BookmarkEntry.COL_URL, article.url)
            put(BookmarkEntry.COL_IMAGE_URL, article.imageUrl)
            put(BookmarkEntry.COL_NEWS_SITE, article.newsSite)
            put(BookmarkEntry.COL_SUMMARY, article.summary)
            put(BookmarkEntry.COL_PUBLISHED_AT, article.publishedAt)
            put(BookmarkEntry.COL_BOOKMARKED_AT, System.currentTimeMillis() / 1000)
        }
        contentResolver.insert(BookmarkEntry.CONTENT_URI, values)
    }

    override suspend fun removeBookmark(id: Long) {
        val uri = ContentUris.withAppendedId(BookmarkEntry.CONTENT_URI, id)
        contentResolver.delete(uri, null, null)
    }

    override suspend fun isBookmarked(articleId: Int): Boolean {
        val cursor = contentResolver.query(
            BookmarkEntry.CONTENT_URI,
            arrayOf(BookmarkEntry.COL_ID),
            "${BookmarkEntry.COL_ARTICLE_ID} = ?",
            arrayOf(articleId.toString()),
            null
        )
        return cursor?.use { it.count > 0 } ?: false
    }

    override suspend fun getBookmarkIdByArticleId(articleId: Int): Long? {
        val cursor = contentResolver.query(
            BookmarkEntry.CONTENT_URI,
            arrayOf(BookmarkEntry.COL_ID),
            "${BookmarkEntry.COL_ARTICLE_ID} = ?",
            arrayOf(articleId.toString()),
            null
        )
        return cursor?.use {
            if (it.moveToFirst()) it.getLong(it.getColumnIndexOrThrow(BookmarkEntry.COL_ID))
            else null
        }
    }
}