package io.opengood.commons.spring

import app.TestApplication
import app.config.TestAppConfig
import app.config.TestWebClientConfig
import io.kotest.core.spec.style.WordSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.booleans.shouldBeFalse
import io.opengood.commons.spring.constant.SpringBean
import io.opengood.commons.spring.webclient.LoggingWebClientConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
    classes = [LoggingWebClientConfig::class, TestAppConfig::class, TestApplication::class, TestWebClientConfig::class],
    properties = [SpringBean.BEAN_OVERRIDE],
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("none")
class SpringCommonsNoConfigTest : WordSpec() {

    @Autowired
    lateinit var applicationContext: ApplicationContext

    override fun extensions() = listOf(SpringExtension)

    init {
        "Spring Commons configuration" should {
            "Not configure Spring beans when properties are not populated in application configuration" {
                applicationContext.containsBean("loggingHttpClient").shouldBeFalse()
                applicationContext.containsBean("loggingWebClientBuilder").shouldBeFalse()
            }
        }
    }
}
