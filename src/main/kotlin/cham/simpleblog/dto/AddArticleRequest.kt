package cham.simpleblog.dto

import cham.simpleblog.domain.Article

data class AddArticleRequest(
    val author: String  = "",
    val title: String = "",
    val content: String = ""
) {
    fun toEntity(author: String): Article {
        return Article(
            author = author,
            title = title,
            content = content
        )
    }
}