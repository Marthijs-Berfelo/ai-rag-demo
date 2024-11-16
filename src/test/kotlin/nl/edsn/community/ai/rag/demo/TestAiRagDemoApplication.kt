package nl.edsn.community.ai.rag.demo

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<AiRagDemoApplication>().with(TestcontainersConfiguration::class).run(*args)
}
