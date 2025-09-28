package cham.simpleblog.service

import cham.simpleblog.domain.Article
import cham.simpleblog.dto.AddArticleRequest
import cham.simpleblog.dto.UpdateArticleRequest
import cham.simpleblog.repository.BlogRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class BlogService(
    private val blogRepository: BlogRepository
) {
    fun save(request: AddArticleRequest, username: String): Article {
        return blogRepository.save(request.toEntity(username))
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Article> {
        return blogRepository.findAll()
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): Article {
        return blogRepository.findById(id).orElseThrow { IllegalArgumentException("Article not found with id: $id") }
    }

    fun delete(id: Long) {
        val article = blogRepository.findById(id).orElseThrow { IllegalArgumentException("Article not found with id: $id") }

        authorizeArticleAuthor(article)

        blogRepository.delete(article)
    }

    fun update(id: Long, request: UpdateArticleRequest): Article {
        val article = blogRepository.findById(id).orElseThrow { IllegalArgumentException("Article not found with id: $id") }

        authorizeArticleAuthor(article)
        article.update(request.title, request.content)

        return article
    }

    private fun authorizeArticleAuthor(article: Article) {
        val username = SecurityContextHolder.getContext().authentication.name

        if(!article.author.equals(username)){
            throw IllegalArgumentException("You are not authorized to modify this article.")
        }
    }



}