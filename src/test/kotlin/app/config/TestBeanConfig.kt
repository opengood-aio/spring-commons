package app.config

import app.model.Greeting
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.random.Random

@Configuration
class TestBeanConfig {

    @Bean
    fun greeting() =
        Greeting("Hello John Smith! Your number is ${Random.nextInt(0, 100)}")
}
