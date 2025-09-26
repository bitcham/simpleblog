package cham.simpleblog.dto

data class CreateAccessTokenRequest(
    val refreshToken: String
) {
}