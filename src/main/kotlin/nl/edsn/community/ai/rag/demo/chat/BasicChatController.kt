package nl.edsn.community.ai.rag.demo.chat

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.model.ChatModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/chat")
class BasicChatController(
    private val model: ChatModel
) {

    private val log: Logger by lazy { LoggerFactory.getLogger(javaClass) }

    @GetMapping
    fun chat(@RequestParam("prompt") prompt: String): String =
        model.call(prompt)

}