package cham.simpleblog.controller

import cham.simpleblog.dto.ArticleListViewResponse
import cham.simpleblog.service.BlogService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class BlogViewController(
    val blogService: BlogService
) {
    @GetMapping("/articles")
    fun getArticles(model: Model): String{
        val articles = blogService.findAll().map{ ArticleListViewResponse.from(it) }

        model.addAttribute("articles", articles)

        return "articleList"
    }

    @GetMapping("/articles/{id}")
    fun getArticle(@PathVariable id:Long, model: Model): String{
        val article = blogService.findById(id)

        model.addAttribute("article", ArticleListViewResponse.from(article))

        return "article"
    }

    @GetMapping("/new-article")
    fun newArticle(@RequestParam(required = false) id:Long?, model: Model): String{
        val article = if(id == null){
            ArticleListViewResponse()
        } else{
            val existingArticle = blogService.findById(id)
            ArticleListViewResponse.from(existingArticle)
        }

        model.addAttribute("article", article)

        return "newArticle"
    }
}