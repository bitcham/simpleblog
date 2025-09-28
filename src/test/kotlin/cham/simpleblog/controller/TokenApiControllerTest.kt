package cham.simpleblog.controller

import cham.simpleblog.config.jwt.JwtProperties
import cham.simpleblog.config.jwt.JwtFactory
import cham.simpleblog.domain.RefreshToken
import cham.simpleblog.domain.User
import cham.simpleblog.dto.CreateAccessTokenRequest
import cham.simpleblog.repository.RefreshTokenRepository
import cham.simpleblog.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@AutoConfigureMockMvc
class TokenApiControllerTest(
    val objectMapper: ObjectMapper,
    val context: WebApplicationContext,
    val jwtProperties: JwtProperties,
    val userRepository: UserRepository,
    val refreshTokenRepository: RefreshTokenRepository
): FunSpec({
    lateinit var mockMvc: MockMvc

    beforeTest {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
        userRepository.deleteAll()
    }

    test("createNewAccessToken - success") {
        val url = "/api/token"

        val testUser = userRepository.save(
            User.of("user@gmail.com", "test", "test"))

        val refreshToken = JwtFactory.of(
            claims = mapOf("userId" to testUser.id!!))
            .createToken(jwtProperties)

        refreshTokenRepository.save(RefreshToken(testUser.id!!, refreshToken))

        val request = CreateAccessTokenRequest(refreshToken)
        val requestBody = objectMapper.writeValueAsString(request)

        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
    }
})