package io.opengood.commons.spring

import app.TestApplication
import app.config.TestAppConfig
import app.config.TestWebClientConfig
import io.kotest.core.spec.style.WordSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.booleans.shouldBeTrue
import io.opengood.commons.spring.bean.refresh.BeanRefresher
import io.opengood.commons.spring.bean.refresh.controller.BeanRefreshController
import io.opengood.commons.spring.webclient.config.LoggingWebClientConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.context.ApplicationContext

@SpringBootTest(
    classes = [
        BeanRefresher::class,
        BeanRefreshController::class,
        LoggingWebClientConfig::class,
        TestAppConfig::class,
        TestApplication::class,
        TestWebClientConfig::class,
    ],
    webEnvironment = WebEnvironment.RANDOM_PORT,
)
class SpringCommonsConfigTest : WordSpec() {
    @Autowired
    lateinit var applicationContext: ApplicationContext

    override fun extensions() = listOf(SpringExtension)

    init {
        "Spring Commons configuration" should {
            "Configure Spring beans when properties are populated in application configuration" {
                applicationContext.containsBean("beanRefreshController").shouldBeTrue()
                applicationContext.containsBean("beanRefresher").shouldBeTrue()
                applicationContext.containsBean("loggingHttpClient").shouldBeTrue()
                applicationContext.containsBean("loggingWebClientBuilder").shouldBeTrue()
            }
        }
    }
}
