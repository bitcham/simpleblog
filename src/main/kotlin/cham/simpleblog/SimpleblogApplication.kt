package cham.simpleblog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing


@EnableJpaAuditing
@SpringBootApplication
class SimpleblogApplication

fun main(args: Array<String>) {
    runApplication<SimpleblogApplication>(*args)
}
