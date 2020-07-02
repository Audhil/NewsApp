package com.example.newsapp.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HeadLinePojo(
    @field:Json(name = "status")
    var status: String? = null,
    @field:Json(name = "totalResults")
    val totalResults: Long? = null,
    @field:Json(name = "articles")
    val articles: List<Article>? = null
) {
    override fun toString(): String {
        return "HeadLinePojo(status=$status, totalResults=$totalResults, articles=$articles)"
    }
}

@JsonClass(generateAdapter = true)
data class Article(
    @field:Json(name = "author")
    var author: String? = null,
    @field:Json(name = "title")
    var title: String? = null,
    @field:Json(name = "description")
    var description: String? = null,
    @field:Json(name = "url")
    var url: String? = null,
    @field:Json(name = "urlToImage")
    var urlToImage: String? = null,
    @field:Json(name = "publishedAt")
    var publishedAt: String? = null,
    @field:Json(name = "content")
    var content: String? = null
) {
    override fun toString(): String {
        return "Article(author=$author, title=$title, description=$description, url=$url, urlToImage=$urlToImage, publishedAt=$publishedAt, content=$content)"
    }
}