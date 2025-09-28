package cham.simpleblog.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.oauth2.client.jackson2.OAuth2ClientJackson2Module
import java.util.Base64

class CookieUtil {

    companion object {
        private val objectMapper = ObjectMapper().apply {
            registerKotlinModule()
            registerModule(OAuth2ClientJackson2Module())
        }

        fun addCookie(response: HttpServletResponse, name: String, value: String, maxAge: Int) {
            val cookie = Cookie(name, value)
            cookie.setPath("/")
            cookie.setMaxAge(maxAge)
            response.addCookie(cookie)
        }

        fun deleteCookie(request: HttpServletRequest, response: HttpServletResponse, name: String) {
            val cookies = request.cookies

            if(cookies == null){
                return
            }

            for(cookie in cookies) {
                if(name.equals(cookie.name)){
                    cookie.setPath("/")
                    cookie.setValue("")
                    cookie.setMaxAge(0)
                    response.addCookie(cookie)
                }
            }
        }

        fun serialize(any: Any): String{
            return Base64.getUrlEncoder().encodeToString(objectMapper.writeValueAsBytes(any))
        }

        fun <T> deserialize(cookie: Cookie?, cls: Class<T>): T{
            if (cookie?.value == null) {
                throw IllegalArgumentException("Cookie value cannot be null")
            }
            val decodedBytes = Base64.getUrlDecoder().decode(cookie.value)
            return objectMapper.readValue(decodedBytes, cls)
        }
    }
}