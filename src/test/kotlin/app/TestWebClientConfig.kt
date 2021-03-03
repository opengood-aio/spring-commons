package app

import io.opengood.commons.spring.function.logRequest
import io.opengood.commons.spring.function.logResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class TestWebClientConfig {

    @Bean
    fun webClient(@Value("\${api.base-uri:http://localhost:8080}") apiBaseUri: String): WebClient {
        return WebClient.builder()
            .baseUrl(apiBaseUri)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filters { exchangeFilterFunctions ->
                exchangeFilterFunctions.add(logRequest(log))
                exchangeFilterFunctions.add(logResponse(log))
            }
            .build()
    }

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}
