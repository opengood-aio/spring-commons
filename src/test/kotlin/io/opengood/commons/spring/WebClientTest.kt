package io.opengood.commons.spring

import app.Greeting
import app.Person
import app.TestApplication
import app.TestWebClientConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainAnyOf
import io.kotest.spring.SpringListener
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
    classes = [TestApplication::class],
    properties = [SpringBean.BEAN_OVERRIDE],
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = ["api.base-uri=http://localhost:\${wiremock.server.port}"])
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
class WebClientTest : WordSpec() {

    private val log = TestLoggerFactory.getTestLogger(TestWebClientConfig::class.java)

    @Value("\${wiremock.server.port}")
    var wireMockServerPort: Int = 0

    @Autowired
    lateinit var wireMockServer: WireMockServer

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    override fun listeners() = listOf(SpringListener)

    init {
        afterEach {
            TestLoggerFactory.clear()
        }

        "Service client accessing API endpoint" should {
            "Send request and service should call another API endpoint and log request and response data" {
                wireMockServer.stubFor(
                    get(urlPathEqualTo("/api/get"))
                        .withQueryParam("id", equalTo("1"))
                        .willReturn(
                            aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(objectMapper.writeValueAsString(Person(name = "John Smith")))
                        )
                )

                mockMvc.get("/greeting/greet/1")
                    .andDo { print() }
                    .andExpect {
                        status { isOk() }
                        content { contentType(MediaType.APPLICATION_JSON) }
                        content { json(objectMapper.writeValueAsString(Greeting(message = "Hello John Smith!"))) }
                    }

                log.loggingEvents.shouldContainAnyOf(listOf(
                    debug("WebClient Request: {} {}", "GET", "http://localhost:$wireMockServerPort/api/get?id=1"),
                    debug("WebClient Request Header: {}={}", "Content-Type", "application/json")
                ))
            }
        }
    }
}