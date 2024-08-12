package io.opengood.commons.spring.bean.refresh.controller

import app.TestApplication
import app.bean.GreetingBean
import app.config.TestAppConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.WordSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import io.opengood.commons.spring.bean.refresh.BeanRefresher
import io.opengood.commons.spring.bean.refresh.model.BeanRefreshConfig
import io.opengood.commons.spring.bean.refresh.model.BeanRefreshRequest
import io.opengood.commons.spring.bean.refresh.model.BeanRefreshResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(
    classes = [
        BeanRefreshController::class,
        TestAppConfig::class,
        TestApplication::class,
    ],
    webEnvironment = WebEnvironment.RANDOM_PORT,
)
@AutoConfigureMockMvc
class BeanRefreshControllerTest : WordSpec() {
    @MockkBean
    lateinit var beanRefresher: BeanRefresher

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    override fun extensions() = listOf(SpringExtension)

    init {
        "Service client accessing API endpoint" should {
            "Send request to API endpoint and refresh Spring bean and return successful response" {
                justRun { beanRefresher.refresh(any<BeanRefreshConfig<GreetingBean>>()) }

                mockMvc
                    .post("/spring-commons/bean/refresh") {
                        contentType = MediaType.APPLICATION_JSON
                        content =
                            objectMapper.writeValueAsString(
                                BeanRefreshRequest(
                                    beanName = "greetingBean",
                                    classType = GreetingBean::class.java.canonicalName,
                                ),
                            )
                        accept = MediaType.APPLICATION_JSON
                    }.andDo { print() }
                    .andExpect {
                        status { isOk() }
                        content { contentType(MediaType.APPLICATION_JSON) }
                        content {
                            json(
                                objectMapper.writeValueAsString(
                                    BeanRefreshResponse(message = "Successfully refreshed bean 'greetingBean'"),
                                ),
                            )
                        }
                    }

                verify { beanRefresher.refresh(any<BeanRefreshConfig<GreetingBean>>()) }
            }

            "Send request to API endpoint and not refresh Spring bean when exception is thrown and return error response" {
                every { beanRefresher.refresh(any<BeanRefreshConfig<GreetingBean>>()) } throws Exception("Error occurred")

                mockMvc
                    .post("/spring-commons/bean/refresh") {
                        contentType = MediaType.APPLICATION_JSON
                        content =
                            objectMapper.writeValueAsString(
                                BeanRefreshRequest(
                                    beanName = "greetingBean",
                                    classType = GreetingBean::class.java.canonicalName,
                                ),
                            )
                        accept = MediaType.APPLICATION_JSON
                    }.andDo { print() }
                    .andExpect {
                        status { isBadRequest() }
                        content { contentType(MediaType.APPLICATION_JSON) }
                        content {
                            json(
                                objectMapper.writeValueAsString(
                                    BeanRefreshResponse(message = "Unable to refresh bean 'greetingBean'"),
                                ),
                            )
                        }
                    }

                verify { beanRefresher.refresh(any<BeanRefreshConfig<GreetingBean>>()) }
            }
        }
    }
}
