package app

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestAppConfig {

    @Bean
    fun objectMapper(): ObjectMapper =
        jacksonObjectMapper()
}