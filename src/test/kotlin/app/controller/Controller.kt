package app.controller

import app.model.Greeting
import app.model.Person
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

@RestController
@RequestMapping("/greeting")
class Controller(
    @Qualifier("logExchangeFiltersWebClient") val webClient: WebClient,
) {
    @GetMapping("/{firstName}")
    fun greeting(
        @PathVariable firstName: String,
    ): ResponseEntity<Greeting> {
        val response =
            webClient
                .get()
                .uri { uriBuilder ->
                    with(uriBuilder) {
                        path("/api/person")
                            .queryParam("firstName", firstName)
                    }.build()
                }.retrieve()
                .bodyToMono(Person::class.java)
                .block()
        return ResponseEntity.ok(Greeting(message = "Hello ${response?.firstName} ${response?.lastName}!"))
    }
}
