package cham.simpleblog.config.oauth

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest

data class OAuth2AuthorizationRequestDto(
    val authorizationUri: String = "",
    val authorizationGrantType: String = "",
    val responseType: String = "",
    val clientId: String = "",
    val redirectUri: String? = null,
    val scopes: List<String> = emptyList(),
    val state: String? = null,
    val additionalParameters: Map<String, String> = emptyMap(),
    val attributes: Map<String, String> = emptyMap()
) {
    companion object {
        fun from(request: OAuth2AuthorizationRequest): OAuth2AuthorizationRequestDto {
            return OAuth2AuthorizationRequestDto(
                authorizationUri = request.authorizationUri,
                authorizationGrantType = request.grantType.value,
                responseType = request.responseType.value,
                clientId = request.clientId,
                redirectUri = request.redirectUri,
                scopes = request.scopes.toList(),
                state = request.state,
                additionalParameters = request.additionalParameters.mapValues { it.value.toString() },
                attributes = request.attributes.mapValues { it.value.toString() }
            )
        }
    }

    fun toOAuth2AuthorizationRequest(): OAuth2AuthorizationRequest {
        return OAuth2AuthorizationRequest
            .authorizationCode()
            .authorizationUri(authorizationUri)
            .clientId(clientId)
            .redirectUri(redirectUri)
            .scopes(scopes.toSet())
            .state(state)
            .additionalParameters(additionalParameters)
            .attributes(attributes)
            .build()
    }
}