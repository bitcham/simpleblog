package cham.simpleblog.service

import cham.simpleblog.domain.User
import cham.simpleblog.dto.AddUserRequest
import cham.simpleblog.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun save(addUserRequest: AddUserRequest): User {
        return userRepository.save(
            User.of(
                addUserRequest.email,
                passwordEncoder.encode(addUserRequest.password)
            ))
    }

    fun findById(userId: Long): User {
        return userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found with id: $userId") }
    }
}