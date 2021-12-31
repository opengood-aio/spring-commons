package io.opengood.commons.spring.webclient

import org.slf4j.Logger
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import reactor.core.publisher.Mono

fun logRequest(log: Logger): ExchangeFilterFunction =
    ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
        log.debug("WebClient Request: {} {}", clientRequest.method(), clientRequest.url())
        clientRequest.headers().forEach { name, values ->
            values.forEach { value ->
                log.debug("WebClient Request Header: {}={}", name, value)
            }
        }
        Mono.just(clientRequest)
    }

fun logResponse(log: Logger): ExchangeFilterFunction =
    ExchangeFilterFunction.ofResponseProcessor { clientResponse ->
        log.debug("WebClient Response: {}", clientResponse.rawStatusCode())
        clientResponse.headers().asHttpHeaders().forEach { name, values ->
            values.forEach { value ->
                log.debug("WebClient Response Header: {}={}", name, value)
            }
        }
        Mono.just(clientResponse)
    }
