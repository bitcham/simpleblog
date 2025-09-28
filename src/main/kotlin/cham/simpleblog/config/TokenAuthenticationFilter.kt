package cham.simpleblog.config

import cham.simpleblog.config.jwt.TokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.WebUtils


class TokenAuthenticationFilter(
    private val tokenProvider: TokenProvider,
): OncePerRequestFilter() {

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val TOKEN_PREFIX = "Bearer "
        private const val ACCESS_TOKEN_COOKIE_NAME = "access_token"
    }


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader(HEADER_AUTHORIZATION)
        var token = getAccessTokenFromHeader(authorizationHeader)

        if (token == null) {
            token = getAccessTokenFromCookie(request)
        }

        if(tokenProvider.validToken(token)){
            val authentication = tokenProvider.getAuthentication(token!!)
            SecurityContextHolder.getContext().setAuthentication(authentication)
        }

        filterChain.doFilter(request, response)
    }

    private fun getAccessTokenFromHeader(authorizationHeader: String?): String? {
        return if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            authorizationHeader.substring(TOKEN_PREFIX.length)
        } else{
            null
        }
    }

    private fun getAccessTokenFromCookie(request: HttpServletRequest): String? {
        val cookie = WebUtils.getCookie(request, ACCESS_TOKEN_COOKIE_NAME)
        return cookie?.value
    }
}