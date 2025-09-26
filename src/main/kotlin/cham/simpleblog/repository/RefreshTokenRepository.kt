package cham.simpleblog.repository

import cham.simpleblog.domain.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository: JpaRepository<RefreshToken, Long> {
    fun findByUserId(userId: Long): RefreshToken?
    fun findByRefreshToken(refreshToken: String): RefreshToken?
}