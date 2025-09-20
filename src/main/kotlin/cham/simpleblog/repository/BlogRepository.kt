package cham.simpleblog.repository

import cham.simpleblog.domain.Article
import org.springframework.data.jpa.repository.JpaRepository

interface BlogRepository: JpaRepository<Article, Long> {

}