package cham.simpleblog.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
class Article(
    @field:Column(nullable = false)
    var author: String,

    @field:Column(nullable = false)
    var title: String,

    @field:Column(nullable = false)
    var content: String,

    @CreatedDate
    @field:Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @field:Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
){
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object{
        fun of(author: String, title: String, content: String): Article{
            return Article(author, title, content)
        }
    }

    fun update(title: String, content: String){
        this.title = title
        this.content = content
    }
}