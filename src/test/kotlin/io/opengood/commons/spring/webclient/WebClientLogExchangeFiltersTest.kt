package io.opengood.commons.spring.webclient

import app.TestApplication
import app.config.TestAppConfig
import app.config.TestWebClientConfig
import app.model.Greeting
import app.model.Person
import ch.qos.logback.classic.Logger
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.WordSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainAnyOf
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import test.TestAppender

@SpringBootTest(
    classes = [
        TestAppConfig::class,
        TestApplication::class,
        TestWebClientConfig::class,
        WireMockServer::class,
    ],
    webEnvironment = WebEnvironment.RANDOM_PORT,
)
@TestPropertySource(properties = ["api.base-uri=http://localhost:\${wiremock.server.port}"])
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
class WebClientLogExchangeFiltersTest : WordSpec() {
    @Value("\${wiremock.server.port}")
    var wireMockServerPort: Int = 0

    @Autowired
    lateinit var wireMockServer: WireMockServer

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    override fun extensions() = listOf(SpringExtension)

    init {
        "Service client accessing API endpoint" should {
            "Send request to API endpoint and log request and response data" {
                val testAppender = TestAppender()
                log.addAppender(testAppender)
                testAppender.start()

                WireMock.configureFor("localhost", wireMockServer.port())

                wireMockServer.stubFor(
                    get(urlPathEqualTo("/api/person"))
                        .withQueryParam("firstName", equalTo("John"))
                        .willReturn(
                            aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(
                                    objectMapper.writeValueAsString(
                                        Person(
                                            firstName = "John",
                                            lastName = "Smith",
                                        ),
                                    ),
                                ),
                        ),
                )

                mockMvc
                    .get("/greeting/John")
                    .andDo { print() }
                    .andExpect {
                        status { isOk() }
                        content { contentType(MediaType.APPLICATION_JSON) }
                        content { json(objectMapper.writeValueAsString(Greeting(message = "Hello John Smith!"))) }
                    }

                val lastLoggedEvent = testAppender.lastLoggedEvent()
                require(lastLoggedEvent != null) {
                    "There are no log events appended"
                }

                assertSoftly {
                    with(testAppender) {
                        events.map { it.formattedMessage } shouldContainAnyOf
                            listOf(
                                "WebClient Request: GET http://localhost:$wireMockServerPort/api/person?firstName=John",
                                "WebClient Request Header: Content-Type=application/json",
                                "WebClient Response Status Code: 200 OK",
                                "WebClient Response Header: Content-Type=application/json",
                            )
                    }
                }

                verify(
                    getRequestedFor(urlPathEqualTo("/api/person"))
                        .withQueryParam("firstName", equalTo("John"))
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)),
                )
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    }
}
