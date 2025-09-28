package cham.simpleblog.dto

import cham.simpleblog.domain.Article
import java.time.LocalDateTime

data class ArticleListViewResponse(
    val id: Long? = null,
    val title: String = "",
    val content: String = "",
    val createdAt: LocalDateTime? = null,
    val author: String = ""
) {
    companion object {
        fun from(article: Article) = ArticleListViewResponse(
            id = article.id,
            title = article.title,
            content = article.content,
            createdAt = article.createdAt,
            author = article.author
        )
    }
}