package io.opengood.commons.spring.webclient

import app.TestApplication
import app.config.TestAppConfig
import app.config.TestWebClientConfig
import app.model.Greeting
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
import io.kotest.matchers.collections.shouldContainAnyOf
import io.opengood.commons.spring.constant.SpringBean
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
import uk.org.lidalia.slf4jtest.LoggingEvent.debug
import uk.org.lidalia.slf4jtest.TestLoggerFactory

@SpringBootTest(
    classes = [TestAppConfig::class, TestApplication::class, TestWebClientConfig::class, WireMockServer::class],
    properties = [SpringBean.BEAN_OVERRIDE],
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = ["api.base-uri=http://localhost:\${wiremock.server.port}"])
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
class WebClientLogExchangeFiltersTest : WordSpec() {

    private val log = TestLoggerFactory.getTestLogger(TestWebClientConfig::class.java)

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
        afterEach {
            TestLoggerFactory.clear()
        }

        "Service client accessing API endpoint" should {
            "Send request and service should call another API endpoint and log request and response data" {
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
                                            lastName = "Smith"
                                        )
                                    )
                                )
                        )
                )

                mockMvc.get("/greeting/John")
                    .andDo { print() }
                    .andExpect {
                        status { isOk() }
                        content { contentType(MediaType.APPLICATION_JSON) }
                        content { json(objectMapper.writeValueAsString(Greeting(message = "Hello John Smith!"))) }
                    }

                log.loggingEvents.shouldContainAnyOf(
                    listOf(
                        debug(
                            "WebClient Request: {} {}",
                            "GET",
                            "http://localhost:$wireMockServerPort/get?firstName=John"
                        ),
                        debug("WebClient Request Header: {}={}", "Content-Type", "application/json")
                    )
                )

                verify(
                    getRequestedFor(urlPathEqualTo("/api/person"))
                        .withQueryParam("firstName", equalTo("John"))
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                )
            }
        }
    }
}
