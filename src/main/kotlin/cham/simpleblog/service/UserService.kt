package cham.simpleblog.service

import cham.simpleblog.domain.User
import cham.simpleblog.dto.AddUserRequest
import cham.simpleblog.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder
) {
    fun save(addUserRequest: AddUserRequest): User {
        return userRepository.save(
            User.of(
                addUserRequest.email,
                passwordEncoder.encode(addUserRequest.password)
            ))
    }
}