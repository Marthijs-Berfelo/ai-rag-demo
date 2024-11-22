package nl.edsn.community.ai.rag.demo.chat

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.advisor.api.*
import org.springframework.ai.chat.model.MessageAggregator
import reactor.core.publisher.Flux

class LoggingAdvisor : CallAroundAdvisor, StreamAroundAdvisor {
    private val log: Logger by lazy { LoggerFactory.getLogger(javaClass) }

    override fun getOrder(): Int = 0

    override fun getName(): String = javaClass.simpleName

    override fun aroundStream(advisedRequest: AdvisedRequest, chain: StreamAroundAdvisorChain): Flux<AdvisedResponse> =
        advisedRequest
            .also { log.atInfo().log { "BEFORE STREAM: $it" } }
            .let { chain.nextAroundStream(it) }
            .let {
                MessageAggregator()
                    .aggregateAdvisedResponse(it)
                    { log.atInfo().log { "AFTER STREAM: $it" } }
            }

    override fun aroundCall(advisedRequest: AdvisedRequest, chain: CallAroundAdvisorChain): AdvisedResponse =
        advisedRequest
            .also { log.atInfo().log { "BEFORE CALL: $it" } }
            .let { chain.nextAroundCall(it) }
            .also { log.atInfo().log { "AFTER CALL: $it" } }
}