package cham.simpleblog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SimpleblogApplication

fun main(args: Array<String>) {
    runApplication<SimpleblogApplication>(*args)
}
