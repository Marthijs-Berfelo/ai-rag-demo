package nl.edsn.community.ai.rag.demo.chat

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.model.ChatModel
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/basic")
class BasicChatController(
    private val model: ChatModel
) {

    private val log: Logger by lazy { LoggerFactory.getLogger(javaClass) }

    @GetMapping("chat")
    fun chat(@RequestParam("prompt") prompt: String): ResponseEntity<Flux<String>> =
        prompt
            .also { log.atInfo().log { "Received question: $it" } }
            .let { model.stream(it) }
            .doOnNext { log.atInfo().log { "Next response: $it" } }
            .let { ok(it) }

}