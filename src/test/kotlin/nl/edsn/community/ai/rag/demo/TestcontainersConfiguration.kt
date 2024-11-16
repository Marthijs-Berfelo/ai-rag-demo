package nl.edsn.community.ai.rag.demo

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.ollama.OllamaContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    fun ollamaContainer(): OllamaContainer {
        return OllamaContainer(DockerImageName.parse("ollama/ollama:latest"))
    }

    @Bean
    @ServiceConnection
    fun pgvectorContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer(DockerImageName.parse("pgvector/pgvector:pg16"))
    }

    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
    }

}
