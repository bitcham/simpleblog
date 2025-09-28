package cham.simpleblog.config.oauth

import cham.simpleblog.util.CookieUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.web.util.WebUtils

class OAuth2AuthorizationRequestBasedOnCookieRepository: AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request"
        const val COOKIE_EXPIRE_SECONDS = 18000
    }

    override fun loadAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        val cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
        return cookie?.let {
            try {
                logger.debug("Loading OAuth2AuthorizationRequest from cookie")
                val dto = CookieUtil.deserialize(cookie, OAuth2AuthorizationRequestDto::class.java)
                val authRequest = dto.toOAuth2AuthorizationRequest()
                logger.debug("Successfully loaded OAuth2AuthorizationRequest: $authRequest")
                authRequest
            } catch (e: Exception) {
                logger.error("Failed to deserialize OAuth2AuthorizationRequest from cookie", e)
                null
            }
        }
    }

    override fun saveAuthorizationRequest(
        authorizationRequest: OAuth2AuthorizationRequest?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ){
        if(authorizationRequest == null){
            removeAuthorizationRequestCookies(request, response)
            return
        }

        try {
            logger.debug("Saving OAuth2AuthorizationRequest: $authorizationRequest")
            val dto = OAuth2AuthorizationRequestDto.from(authorizationRequest)
            logger.debug("Created DTO: $dto")

            val serialized = CookieUtil.serialize(dto)
            CookieUtil.addCookie(
                response,
                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                serialized,
                COOKIE_EXPIRE_SECONDS
            )
            logger.debug("Successfully saved OAuth2AuthorizationRequest to cookie")
        } catch (e: Exception) {
            logger.error("Failed to save OAuth2AuthorizationRequest to cookie", e)
            throw e
        }
    }

    override fun removeAuthorizationRequest(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): OAuth2AuthorizationRequest? {
        return this.loadAuthorizationRequest(request)
    }


    fun removeAuthorizationRequestCookies(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
    }




}