package nl.edsn.community.ai.rag.demo.chat

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.ok
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/rag")
class RagController(
    private val publisher: ApplicationEventPublisher,
    model: ChatModel,
    vectorStore: VectorStore
) {

    private val log: Logger by lazy { LoggerFactory.getLogger(javaClass) }
    private val client = ChatClient
        .builder(model)
        .defaultAdvisors(
            QuestionAnswerAdvisor(vectorStore),
            LoggingAdvisor()
        )
        .build()

    @PostMapping("init")
    @Transactional
    fun initialize(@RequestBody request: InitializationRequest): ResponseEntity<Void> =
        request
            .also { log.atInfo().log { "Initializing RAG from resource directory: ${it.directory}"  } }
            .directory
            .let { EmbeddingRequested(it) }
            .let { publisher.publishEvent(it) }
            .let { noContent().build() }

    @GetMapping("chat")
    fun chat(@RequestParam("prompt") prompt: String): ResponseEntity<Flux<String>> =
        client
            .also { log.atInfo().log { "Received question: $prompt" } }
            .prompt()
            .user(prompt)
            .stream()
            .chatResponse()
            .doOnNext { log.atInfo().log { "Next response: $it" } }
            .map { it.result.output.content }
            .let { ok(it) }
}

data class InitializationRequest(
    val directory: String
)

data class EmbeddingRequested(val directory: String)