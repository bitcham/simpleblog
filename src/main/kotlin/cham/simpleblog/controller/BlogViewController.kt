package cham.simpleblog.controller

import cham.simpleblog.dto.ArticleListViewResponse
import cham.simpleblog.service.BlogService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class BlogViewController(
    val blogService: BlogService
) {
    @GetMapping("/articles")
    fun getArticles(model: Model): String{
        val articles = blogService.findAll().map{ ArticleListViewResponse(it.id!!, it.title, it.content, it.createdAt) }

        model.addAttribute("articles", articles)

        return "articleList"
    }

    @GetMapping("/articles/{id}")
    fun getArticle(@PathVariable id:Long, model: Model): String{
        val article = blogService.findById(id)
        val articleListViewResponse = ArticleListViewResponse(article.id!!, article.title, article.content, article.createdAt)

        model.addAttribute("article", articleListViewResponse)

        return "article"
    }
}