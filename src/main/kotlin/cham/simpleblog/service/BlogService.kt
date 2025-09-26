package cham.simpleblog.service

import cham.simpleblog.domain.Article
import cham.simpleblog.dto.AddArticleRequest
import cham.simpleblog.dto.UpdateArticleRequest
import cham.simpleblog.repository.BlogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class BlogService(
    private val blogRepository: BlogRepository
) {
    fun save(request: AddArticleRequest): Article {
        return blogRepository.save(request.toEntity())
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
        blogRepository.deleteById(id)
    }

    fun update(id: Long, request: UpdateArticleRequest): Article {
        val article = blogRepository.findById(id).orElseThrow { IllegalArgumentException("Article not found with id: $id") }

        article.update(request.title, request.content)

        return article
    }


}