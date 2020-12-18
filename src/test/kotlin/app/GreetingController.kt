package app

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

@RestController
@RequestMapping("/greeting")
class GreetingController(
    val webClient: WebClient
) {

    @GetMapping("/greet/{name}")
    fun greeting(@PathVariable name: String): ResponseEntity<Greeting> {

        return ResponseEntity.ok(Greeting(message = "Hello $name!"))
    }
}
