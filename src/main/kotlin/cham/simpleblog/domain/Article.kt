package cham.simpleblog.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Article(
    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var content: String
){
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object{
        fun of(title: String, content: String): Article{
            return Article(title, content)
        }
    }
}