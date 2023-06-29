package io.opengood.commons.spring.webclient.config

import io.netty.handler.logging.LogLevel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat

@AutoConfiguration
@ConditionalOnProperty(prefix = "spring-commons.web-client.logging", name = ["enabled"], havingValue = "true")
class LoggingWebClientConfig {

    @Bean("loggingHttpClient")
    fun loggingHttpClient(): HttpClient {
        log.info("Setup logging Netty HTTP client")
        return HttpClient.create()
            .wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
    }

    @Bean("loggingWebClientBuilder")
    fun loggingWebClientBuilder(@Qualifier("loggingHttpClient") loggingHttpClient: HttpClient): WebClient.Builder {
        log.info("Setup logging web client builder")
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(loggingHttpClient))
    }

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}
