package cham.simpleblog.service

import cham.simpleblog.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailService(
    private val userRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(email: String?): UserDetails? {
        return userRepository.findByEmail(email ?: throw IllegalArgumentException("Email not provided"))
    }
}