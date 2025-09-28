package cham.simpleblog.config.oauth

import cham.simpleblog.config.jwt.TokenProvider
import cham.simpleblog.domain.RefreshToken
import cham.simpleblog.repository.RefreshTokenRepository
import cham.simpleblog.service.UserService
import cham.simpleblog.util.CookieUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration

@Component
class OAuth2SuccessHandler(
    private val tokenProvider: TokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authorizationRequestRepository: OAuth2AuthorizationRequestBasedOnCookieRepository,
    private val userService: UserService
): SimpleUrlAuthenticationSuccessHandler() {

    companion object {
        const val REFRESH_TOKEN_COOKIE_NAME = "refresh_token"
        const val ACCESS_TOKEN_COOKIE_NAME = "access_token"
        val REFRESH_TOKEN_DURATION: Duration = Duration.ofDays(14)
        val ACCESS_TOKEN_DURATION: Duration = Duration.ofHours(1)
        const val REDIRECT_PATH = "/articles"
    }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2User
        val user = userService.findByEmail(oAuth2User.attributes["email"] as String)

        val refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION)
        saveRefreshToken(user.id!!, refreshToken)
        addRefreshTokenToCookie(request, response, refreshToken)

        val accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION)
        addAccessTokenToCookie(request, response, accessToken)

        clearAuthenticationAttributes(request, response)

        redirectStrategy.sendRedirect(request, response, REDIRECT_PATH)
    }

    private fun saveRefreshToken(userId: Long, newRefreshToken: String) {
        val refreshToken = refreshTokenRepository.findByUserId(userId)
            ?.update(newRefreshToken)
            ?: RefreshToken(userId, newRefreshToken)

        refreshTokenRepository.save(refreshToken)
    }

    private fun addRefreshTokenToCookie(
        request: HttpServletRequest,
        response: HttpServletResponse,
        refreshToken: String
    ) {
        val cookieMaxAge = REFRESH_TOKEN_DURATION.toSeconds().toInt()

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME)
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge)
    }

    private fun addAccessTokenToCookie(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessToken: String
    ) {
        val cookieMaxAge = ACCESS_TOKEN_DURATION.toSeconds().toInt()

        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN_COOKIE_NAME)
        CookieUtil.addCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, cookieMaxAge)
    }

    private fun clearAuthenticationAttributes(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        super.clearAuthenticationAttributes(request)
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response)
    }

}