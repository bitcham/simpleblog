package cham.simpleblog.config.jwt

import cham.simpleblog.config.JwtProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.time.Duration
import java.util.Date

data class JwtFactory(
    val subject: String = "test@email.com",
    val issuedAt: Date = Date(),
    val expiration: Date = Date(Date().time + Duration.ofDays(14).toMillis()),
    val claims: Map<String, Any> = emptyMap()
) {
    fun createToken(jwtProperties: JwtProperties): String {
        val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

        return Jwts.builder()
            .subject(subject)
            .issuer(jwtProperties.issuer)
            .issuedAt(issuedAt)
            .expiration(expiration)
            .claims(claims)
            .signWith(secretKey)
            .compact()
    }

    companion object {
        fun withDefaultValues() = JwtFactory()

        fun of(
            subject: String? = null,
            issuedAt: Date? = null,
            expiration: Date? = null,
            claims: Map<String, Any>? = null
        ) = JwtFactory(
            subject = subject ?: "test@email.com",
            issuedAt = issuedAt ?: Date(),
            expiration = expiration ?: Date(Date().time + Duration.ofDays(14).toMillis()),
            claims = claims ?: emptyMap()
        )
    }
}