package cham.simpleblog.config.jwt

import cham.simpleblog.config.jwt.JwtProperties
import cham.simpleblog.domain.User
import cham.simpleblog.repository.UserRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration

@SpringBootTest
class TokenProviderTest(
    val userRepository: UserRepository,
    val jwtProperties: JwtProperties
) : FunSpec({

    extensions(SpringExtension)

    val tokenProvider = TokenProvider(jwtProperties)

    test("generateToken(): generate token passing user info and expired time"){
        val testUser = userRepository.save(User.of(
            email = "user@email.com",
            password = "test",
            nickname = "test"))

        val token = tokenProvider.generateToken(testUser, Duration.ofDays(14))

        val userId = tokenProvider.getUserId(token)

        userId shouldBe testUser.id
    }

    test("validToken(): fail when token is expired"){
        val token = JwtFactory.of(
            expiration = java.sql.Date(System.currentTimeMillis() - Duration.ofDays(1).toMillis())
        ).createToken(jwtProperties)

        val result = tokenProvider.validToken(token)

        result shouldBe false
    }

    test("validToken(): success when token is valid"){
        val token = JwtFactory.withDefaultValues().createToken(jwtProperties)

        val result = tokenProvider.validToken(token)

        result shouldBe true
    }

    test("getAuthentication(): get Authentication object from token"){
        val userEmail = "user@email.com"
        val token = JwtFactory.of(subject =userEmail).createToken(jwtProperties)

        val authentication = tokenProvider.getAuthentication(token)
        val user =  authentication.principal as org.springframework.security.core.userdetails.User

        user.username  shouldBe userEmail
    }

    test("getUserId(): get userId from token") {
        val token = JwtFactory.of(claims = mapOf("userId" to 1L)).createToken(jwtProperties)

        val userIdByToken = tokenProvider.getUserId(token)

        userIdByToken shouldBe 1L
    }


})