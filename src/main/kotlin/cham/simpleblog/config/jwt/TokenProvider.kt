package cham.simpleblog.config.jwt

import cham.simpleblog.config.JwtProperties
import cham.simpleblog.domain.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.Collections
import java.util.Date
import javax.crypto.SecretKey

@Service
class TokenProvider(
    val jwtProperties: JwtProperties
) {

    private val secretKey: SecretKey by lazy {
        val secretBytes = jwtProperties.secret.toByteArray()

        require(secretBytes.size >= 32) {
            "JWT secret must be at least 256 bits (32 bytes)"
        }

        Keys.hmacShaKeyFor(secretBytes)
    }

    fun generateToken(user: User, expiredAt: Duration): String{
        val now = Date()
        return makeToken(Date(now.time + expiredAt.toMillis()), user)
    }

    private fun makeToken(expiry: Date, user: User): String {
        val now = Date()

        return Jwts.builder()
            .issuer(jwtProperties.issuer)
            .issuedAt(now)
            .expiration(expiry)
            .subject(user.getUsername())
            .claim("userId", user.id)
            .signWith(secretKey)
            .compact()
    }

    fun validToken(token: String): Boolean {
        return try {
            val claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parse(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)

        val authorities = Collections.singleton(SimpleGrantedAuthority("ROLE_USER"))

        return UsernamePasswordAuthenticationToken(
            org.springframework.security.core.userdetails.User(
                claims.subject,
                "",
                authorities),
            token,
            authorities
        )
    }

    fun getUserId(token: String): Long {
        val claims = getClaims(token)
        return (claims["userId"] as Number).toLong()
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}