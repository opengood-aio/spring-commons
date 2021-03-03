package app

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

@RestController
@RequestMapping("/greeting")
class Controller(
    val webClient: WebClient
) {

    @GetMapping("/greet/{id}")
    fun greeting(@PathVariable id: String): ResponseEntity<Greeting> {
        val response = webClient.get()
            .uri { uriBuilder ->
                with(uriBuilder) {
                    path("/api/get")
                        .queryParam("id", id)
                }.build()
            }
            .retrieve()
            .bodyToMono(Person::class.java)
            .block()
        return ResponseEntity.ok(Greeting(message = "Hello ${response?.name}!"))
    }
}
