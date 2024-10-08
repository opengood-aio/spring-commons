package io.opengood.commons.spring.webclient.config

import app.TestApplication
import app.config.TestAppConfig
import app.config.TestWebClientConfig
import app.model.Person
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import io.kotest.core.spec.style.WordSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest(
    classes = [
        LoggingWebClientConfig::class,
        TestAppConfig::class,
        TestApplication::class,
        TestWebClientConfig::class,
        WireMockServer::class,
    ],
    webEnvironment = WebEnvironment.RANDOM_PORT,
)
@TestPropertySource(properties = ["api.base-uri=http://localhost:\${wiremock.server.port}"])
@AutoConfigureWireMock(port = 0)
class LoggingWebClientConfigTest : WordSpec() {
    @Autowired
    lateinit var wireMockServer: WireMockServer

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    @Qualifier("loggingWebClient")
    lateinit var webClient: WebClient

    override fun extensions() = listOf(SpringExtension)

    init {
        "Service client accessing API endpoint" should {
            "Send request to API endpoint and log request and response data" {
                val expected = Person(firstName = "John", lastName = "Smith")

                WireMock.configureFor("localhost", wireMockServer.port())

                wireMockServer.stubFor(
                    get(urlPathEqualTo("/greeting"))
                        .withQueryParam("firstName", equalTo("John"))
                        .willReturn(
                            aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(objectMapper.writeValueAsString(expected)),
                        ),
                )

                val response =
                    webClient
                        .get()
                        .uri { uriBuilder ->
                            with(uriBuilder) {
                                path("/greeting")
                                queryParam("firstName", "John")
                            }.build()
                        }.retrieve()
                        .bodyToMono(Person::class.java)
                        .block()

                response shouldBe expected

                verify(
                    getRequestedFor(urlPathEqualTo("/greeting"))
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)),
                )
            }
        }
    }
}
