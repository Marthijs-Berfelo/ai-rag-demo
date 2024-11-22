package nl.edsn.community.ai.rag.demo.embedding

import nl.edsn.community.ai.rag.demo.chat.EmbeddingRequested
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.ResourcePatternUtils
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service

@Service
class EmbeddingService(
    private val embeddingStore: VectorStore,
    loader: ResourceLoader
) {

    private val log: Logger by lazy { LoggerFactory.getLogger(javaClass) }

    private val resourceLoader = ResourcePatternUtils
        .getResourcePatternResolver(loader)
    private val splitter = TokenTextSplitter()

    @ApplicationModuleListener
    fun on(event: EmbeddingRequested) {
        event
            .also { log.atInfo().log { "Embedding requested from: ${it.directory}" } }
            .directory
            .let(::readDocumentsFromDirectory)
            .map(::createChunks)
            .map(::embed)
            .also { log.atInfo().log { "Finished embedding ${it.size} files from: ${event.directory}" } }
    }

    private fun readDocumentsFromDirectory(directory: String): List<TikaDocumentReader> =
        directory
            .let { "classpath*:$it/*.pdf" }
            .let { resourceLoader.getResources(it) }
            .map { TikaDocumentReader(it) }
            .also { log.atInfo().log { "Found ${it.size} files to embed" } }

    private fun createChunks(document: TikaDocumentReader): List<Document> =
        document
            .get()
            .also { log.atInfo().log { "Splitting documents ..." } }
            .let { splitter.apply(it) }

    private fun embed(documents: List<Document>) {
        documents
            .also { log.atInfo().log { "Embedding documents ..." } }
            .let { embeddingStore.add(it) }
    }
}