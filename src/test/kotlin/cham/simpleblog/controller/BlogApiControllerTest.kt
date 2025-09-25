package cham.simpleblog.controller

import cham.simpleblog.domain.Article
import cham.simpleblog.dto.AddArticleRequest
import cham.simpleblog.dto.UpdateArticleRequest
import cham.simpleblog.repository.BlogRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest(
    val mockMvc: MockMvc,
     val blogRepository: BlogRepository,
     val objectMapper: ObjectMapper) : FunSpec({

    extensions(SpringExtension)

    beforeTest {
        blogRepository.deleteAll()
    }

    test("addArticle: success add article") {
        val url = "/api/articles"
        val title = "title"
        val content = "content"
        val userRequest = AddArticleRequest(title, content)

        val requestBody = objectMapper.writeValueAsString(userRequest)

        mockMvc.perform(
        post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
        ).andExpect(status().isCreated())

        val articles = blogRepository.findAll()

        articles.size shouldBe 1
        articles[0].title shouldBe title
        articles[0].content shouldBe content
    }

    test("findAllArticles: success find all articles") {
        val url = "/api/articles"
        val title = "title"
        val content = "content"

        blogRepository.save(Article(title, content))

        mockMvc.perform(
            get(url)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
         .andExpect(jsonPath("$[0].title").value(title))
         .andExpect(jsonPath("$[0].content").value(content))
    }

    test("findArticleById: success find article by id") {
        val title = "title"
        val content = "content"
        val savedArticle = blogRepository.save(Article(title, content))
        val url = "/api/articles/${savedArticle.id}"

        mockMvc.perform(
            get(url)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
         .andExpect(jsonPath("$.title").value(title))
         .andExpect(jsonPath("$.content").value(content))
    }

    test("deleteArticle: success delete article by id") {
        val title = "title"
        val content = "content"
        val savedArticle = blogRepository.save(Article(title, content))
        val url = "/api/articles/${savedArticle.id}"


        mockMvc.perform(
            delete(url, savedArticle.id))
                .andExpect(status().isOk)

        blogRepository.findById(savedArticle.id!!).isEmpty shouldBe true
    }

    test("updateArticle: success update article by id") {
        val title = "title"
        val content = "content"
        val savedArticle = blogRepository.save(Article(title, content))
        val url = "/api/articles/${savedArticle.id}"

        val newTitle = "new title"
        val newContent = "new content"
        val updateRequest = UpdateArticleRequest(newTitle, newContent)
        val requestBody = objectMapper.writeValueAsString(updateRequest)

        mockMvc.perform(
            put(url, savedArticle.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        ).andExpect(status().isOk)
         .andExpect(jsonPath("$.title").value(newTitle))
         .andExpect(jsonPath("$.content").value(newContent))

        val updatedArticle = blogRepository.findById(savedArticle.id!!).get()
        updatedArticle.title shouldBe newTitle
        updatedArticle.content shouldBe newContent
    }


})