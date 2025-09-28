package cham.simpleblog.controller

import cham.simpleblog.domain.Article
import cham.simpleblog.domain.User
import cham.simpleblog.dto.AddArticleRequest
import cham.simpleblog.dto.UpdateArticleRequest
import cham.simpleblog.repository.BlogRepository
import cham.simpleblog.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.security.Principal

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val context: WebApplicationContext,
    private val blogRepository: BlogRepository,
    private val userRepository: UserRepository
) : FunSpec({

    lateinit var testMockMvc: MockMvc
    lateinit var user: User

    beforeSpec {
        testMockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build()
    }

    beforeEach {
        blogRepository.deleteAll()
        userRepository.deleteAll()

        user = userRepository.save(
            User.of(
                email = "user@gmail.com",
                password = "test",
                nickname = "test"))

        val securityContext = SecurityContextHolder.getContext()
        securityContext.authentication = UsernamePasswordAuthenticationToken(
            user,
            user.password,
            user.authorities
        )
    }

    context("Article API Tests") {

        test("should successfully add a new article") {
            // given
            val url = "/api/articles"
            val title = "title"
            val content = "content"
            val request = AddArticleRequest(title = title, content = content)
            val requestBody = objectMapper.writeValueAsString(request)

            val principal = mockk<Principal>()
            every { principal.name } returns "username"

            // when
            val result = mockMvc.perform(
                post(url)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .principal(principal)
                    .content(requestBody)
            )

            // then
            result.andExpect(status().isCreated)

            val articles = blogRepository.findAll()
            articles shouldHaveSize 1
            articles[0].title shouldBe title
            articles[0].content shouldBe content
        }

        test("should successfully retrieve all articles") {
            // given
            val url = "/api/articles"
            val savedArticle = createDefaultArticle(user, blogRepository)

            // when
            val resultActions = mockMvc.perform(
                get(url)
                    .accept(MediaType.APPLICATION_JSON)
            )

            // then
            resultActions
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].content").value(savedArticle.content))
                .andExpect(jsonPath("$[0].title").value(savedArticle.title))
        }

        test("should successfully retrieve a single article by ID") {
            // given
            val url = "/api/articles/{id}"
            val savedArticle = createDefaultArticle(user, blogRepository)

            // when
            val resultActions = mockMvc.perform(
                get(url, savedArticle.id)
            )

            // then
            resultActions
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content").value(savedArticle.content))
                .andExpect(jsonPath("$.title").value(savedArticle.title))
        }

        test("should successfully delete an article") {
            // given
            val url = "/api/articles/{id}"
            val savedArticle = createDefaultArticle(user, blogRepository)

            // when
            mockMvc.perform(
                delete(url, savedArticle.id)
            ).andExpect(status().isOk)

            // then
            val articles = blogRepository.findAll()
            articles.shouldBeEmpty()
        }

        test("should successfully update an existing article") {
            // given
            val url = "/api/articles/{id}"
            val savedArticle = createDefaultArticle(user, blogRepository)

            val newTitle = "new title"
            val newContent = "new content"
            val request = UpdateArticleRequest(newTitle, newContent)

            // when
            val result = mockMvc.perform(
                put(url, savedArticle.id)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request))
            )

            // then
            result.andExpect(status().isOk)

            val article = blogRepository.findById(savedArticle.id!!).get()
            article.title shouldBe newTitle
            article.content shouldBe newContent
        }
    }
})

// Helper function to create default article
private fun createDefaultArticle(user: User, blogRepository: BlogRepository): Article {
    return blogRepository.save(
        Article.of(
            author = user.username,
            title = "title",
            content = "content"
        )
    )
}