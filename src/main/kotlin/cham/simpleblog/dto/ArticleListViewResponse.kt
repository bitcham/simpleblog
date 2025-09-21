package cham.simpleblog.dto

import java.time.LocalDateTime

data class ArticleListViewResponse(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime
) {
}