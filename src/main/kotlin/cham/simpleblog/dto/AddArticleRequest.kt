package cham.simpleblog.dto

import cham.simpleblog.domain.Article

data class AddArticleRequest(
    val title: String,
    val content: String
) {
    fun toEntity() = Article.of(title, content)
}