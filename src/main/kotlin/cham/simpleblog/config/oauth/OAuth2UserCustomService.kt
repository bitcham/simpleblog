package cham.simpleblog.config.oauth

import cham.simpleblog.domain.User
import cham.simpleblog.repository.UserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class OAuth2UserCustomService(
    private val userRepository: UserRepository
): DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oauth2User = super.loadUser(userRequest)
        saveOrUpdate(oauth2User)
        return oauth2User
    }

    private fun saveOrUpdate(oAuth2User: OAuth2User): User {
        val attributes = oAuth2User.attributes
        val email = attributes["email"] as String
        val username = attributes["name"] as String

        val user = userRepository.findByEmail(email)
            ?.apply { update(username) }
            ?: User.Companion.of(email, "OAUTH2_USER", username)

        return userRepository.save(user)
    }

}