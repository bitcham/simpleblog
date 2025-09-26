package cham.simpleblog.controller

import cham.simpleblog.dto.CreateAccessTokenRequest
import cham.simpleblog.dto.CreateAccessTokenResponse
import cham.simpleblog.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TokenApiController(
    private val tokenService: TokenService
) {
    @PostMapping("/api/token")
    fun createNewAccessToken(@RequestBody request: CreateAccessTokenRequest): ResponseEntity<CreateAccessTokenResponse> {
        val newAccessToken = tokenService.createNewAccessToken(request.refreshToken)

        return ResponseEntity.status(HttpStatus.CREATED).body(CreateAccessTokenResponse(newAccessToken))
    }
}