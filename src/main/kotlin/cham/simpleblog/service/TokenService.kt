package cham.simpleblog.service

import cham.simpleblog.config.jwt.TokenProvider
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TokenService(
    private val tokenProvider: TokenProvider,
    private val refreshTokenService: RefreshTokenService,
    private val userService: UserService
) {
    fun createNewAccessToken(refreshToken: String): String{
        if(!tokenProvider.validToken(refreshToken)){
            throw IllegalArgumentException("Invalid refresh token: $refreshToken")
        }

        val userId = refreshTokenService.findByRefreshToken(refreshToken).userId
        val user = userService.findById(userId)

        return tokenProvider.generateToken(user, Duration.ofHours(2))
    }

}