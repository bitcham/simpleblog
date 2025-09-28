package cham.simpleblog.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


@Table(name = "users")
@Entity
class User(
    @field:Column(nullable = false, unique = true)
    private var email: String,

    @field:Column(nullable = false)
    private var password: String,

    @field:Column(unique = true)
    var nickname: String
): UserDetails{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object{
        fun of(email: String, password: String, nickname: String): User{
            return User(email, password, nickname)
        }
    }

    override fun getAuthorities() = listOf(SimpleGrantedAuthority("ROLE_USER"))

    override fun getPassword(): String = password

    override fun getUsername(): String = email

    override fun isAccountNonExpired() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true

    fun update(nickname: String): User{
        this.nickname = nickname
        return this
    }

}