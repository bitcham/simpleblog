package cham.simpleblog.service

import cham.simpleblog.domain.RefreshToken
import cham.simpleblog.repository.RefreshTokenRepository
import org.springframework.stereotype.Service

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository
) {

    fun findByRefreshToken(refreshToken: String): RefreshToken {
        return  refreshTokenRepository.findByRefreshToken(refreshToken)
            ?: throw IllegalArgumentException("Refresh token not found: $refreshToken")
    }
}