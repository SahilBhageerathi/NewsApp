package com.example.newsapp.data.mappers

import com.example.newsapp.data.dto.ArticleDto
import com.example.newsapp.domain.model.Article

fun ArticleDto.toArticle(): Article {
    return Article(
        id = id,
        title = title,
        author = authors.joinToString(", ") { it.name },
        url = url,
        imageUrl = imageUrl,
        newsSite = newsSite,
        summary = summary,
        publishedAt = publishedAt
    )
}

fun List<ArticleDto>.toArticleList(): List<Article> {
    return map { it.toArticle() }
}
