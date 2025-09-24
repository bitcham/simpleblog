package cham.simpleblog.dto

data class AddUserRequest(
    val email: String,
    val password: String
) {
}