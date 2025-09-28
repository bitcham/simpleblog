package cham.simpleblog.controller

import cham.simpleblog.domain.Article
import cham.simpleblog.dto.AddArticleRequest
import cham.simpleblog.dto.ArticleResponse
import cham.simpleblog.dto.UpdateArticleRequest
import cham.simpleblog.service.BlogService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class BlogApiController(
    val blogService: BlogService
) {

    @PostMapping("/api/articles")
    fun addArticle(@RequestBody request: AddArticleRequest, principal: Principal): ResponseEntity<Article> {
        val savedArticle = blogService.save(request, principal.name)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle)
    }

    @GetMapping("/api/articles")
    fun findAllArticles(): ResponseEntity<List<ArticleResponse>> {
        val articles = blogService.findAll().map { ArticleResponse(it.title, it.content) }

        return ResponseEntity.ok().body(articles)
    }

    @GetMapping("/api/articles/{id}")
    fun findArticleById(@PathVariable id: Long): ResponseEntity<ArticleResponse> {
        val article = blogService.findById(id)
        return ResponseEntity.ok().body(ArticleResponse(article.title, article.content))
    }

    @DeleteMapping("/api/articles/{id}")
    fun deleteArticle(@PathVariable id: Long): ResponseEntity<Void> {
        blogService.delete(id)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/api/articles/{id}")
    fun updateArticle(
        @PathVariable id: Long,
        @RequestBody request: UpdateArticleRequest
    ): ResponseEntity<ArticleResponse> {
        val updatedArticle = blogService.update(id, request)
        return ResponseEntity.ok().body(ArticleResponse(updatedArticle.title, updatedArticle.content))
    }
}