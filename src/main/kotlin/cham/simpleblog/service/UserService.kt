package cham.simpleblog.service

import cham.simpleblog.domain.User
import cham.simpleblog.dto.AddUserRequest
import cham.simpleblog.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun save(addUserRequest: AddUserRequest): User {
        val passwordEncoder = BCryptPasswordEncoder()

        return userRepository.save(
            User.of(
                addUserRequest.email,
                passwordEncoder.encode(addUserRequest.password),
                addUserRequest.nickname
            ))
    }

    fun findById(userId: Long): User {
        return userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found with id: $userId") }
    }

    fun findByEmail(email: String): User{
        return userRepository.findByEmail(email) ?: throw IllegalArgumentException("User not found with email: $email")
    }
}