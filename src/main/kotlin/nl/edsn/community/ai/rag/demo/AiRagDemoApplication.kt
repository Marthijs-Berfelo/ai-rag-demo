package nl.edsn.community.ai.rag.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@EnableWebMvc
@SpringBootApplication
class AiRagDemoApplication

fun main(args: Array<String>) {
    runApplication<AiRagDemoApplication>(*args)
}
