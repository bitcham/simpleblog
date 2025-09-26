package cham.simpleblog.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class RefreshToken(
    @Column(name = "user_id", nullable = false, unique = true)
    val userId: Long,

    @Column(name = "refresh_token", nullable = false)
    var refreshToken: String,
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    var id: Long? = null
            protected set

    fun update(newRefreshToken: String): RefreshToken {
        this.refreshToken = newRefreshToken
        return this
    }
}