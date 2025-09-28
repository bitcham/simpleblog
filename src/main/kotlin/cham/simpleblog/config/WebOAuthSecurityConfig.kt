package cham.simpleblog.config

import cham.simpleblog.config.jwt.TokenProvider
import cham.simpleblog.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository
import cham.simpleblog.config.oauth.OAuth2SuccessHandler
import cham.simpleblog.repository.RefreshTokenRepository
import cham.simpleblog.config.oauth.OAuth2UserCustomService
import cham.simpleblog.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
class WebOAuthSecurityConfig (
    private val oAuth2UserCustomService: OAuth2UserCustomService,
    private val tokenProvider: TokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userService: UserService
){
    @Bean
    fun configure(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/img/**", "/css/**", "/js/**")
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { csrf -> csrf.disable() }
            .httpBasic { httpBasic -> httpBasic.disable() }
            .formLogin { formLogin -> formLogin.disable() }
            .logout { logout ->
                logout
                    .logoutSuccessUrl("/login")
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/api/token").permitAll()
                    .requestMatchers("/articles").authenticated()
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().permitAll()
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .loginPage("/login")
                    .authorizationEndpoint { authorization ->
                        authorization.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                    }
                    .successHandler(oAuth2SuccessHandler())
                    .userInfoEndpoint { userInfo ->
                        userInfo.userService(oAuth2UserCustomService)
                    }
            }
            .exceptionHandling { exception ->
                exception
                    .defaultAuthenticationEntryPointFor(
                        LoginUrlAuthenticationEntryPoint("/login"),
                        { request -> request.requestURI.startsWith("/articles") }
                    )
                    .defaultAuthenticationEntryPointFor(
                        HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        { request -> request.requestURI.startsWith("/api") }
                    )
            }
            .build()
    }

    @Bean
    fun oAuth2SuccessHandler(): OAuth2SuccessHandler {
        return OAuth2SuccessHandler(
            tokenProvider,
            refreshTokenRepository,
            oAuth2AuthorizationRequestBasedOnCookieRepository(),
            userService
        )
    }

    @Bean
    fun tokenAuthenticationFilter(): TokenAuthenticationFilter {
        return TokenAuthenticationFilter(tokenProvider)
    }

    @Bean
    fun oAuth2AuthorizationRequestBasedOnCookieRepository(): OAuth2AuthorizationRequestBasedOnCookieRepository {
        return OAuth2AuthorizationRequestBasedOnCookieRepository()
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}