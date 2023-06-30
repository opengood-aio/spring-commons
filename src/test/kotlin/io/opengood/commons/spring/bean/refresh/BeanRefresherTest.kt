package io.opengood.commons.spring.bean.refresh

import app.TestApplication
import app.bean.GreetingBean
import app.config.TestBeanConfig
import io.kotest.core.spec.style.WordSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.opengood.commons.spring.bean.refresh.model.BeanRefreshConfig
import io.opengood.commons.spring.constant.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest(
    classes = [
        BeanRefresher::class,
        GreetingBean::class,
        TestApplication::class,
        TestBeanConfig::class,
    ],
    properties = [SpringBean.BEAN_OVERRIDE],
)
class BeanRefresherTest : WordSpec() {

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Autowired
    lateinit var beanRefresher: BeanRefresher

    override fun extensions() = listOf(SpringExtension)

    init {
        "Bean refresher" should {
            "Refresh Spring bean with updated configuration" {
                val oldBean = applicationContext.getBean("greetingBean") as GreetingBean

                val config = BeanRefreshConfig(
                    beanName = "greetingBean",
                    classType = GreetingBean::class,
                )

                beanRefresher.refresh(config)

                val newBean = applicationContext.getBean("greetingBean") as GreetingBean

                newBean shouldNotBe oldBean
            }

            "Not refresh Spring bean when bean does not exist" {
                val oldBean = applicationContext.getBean("greetingBean") as GreetingBean

                val config = BeanRefreshConfig(
                    beanName = "greetingBean1",
                    classType = String::class,
                )

                beanRefresher.refresh(config)

                val newBean = applicationContext.getBean("greetingBean") as GreetingBean

                newBean shouldBe oldBean
            }

            "Not refresh Spring bean when new bean class type is not same as existing bean class type" {
                val oldBean = applicationContext.getBean("greetingBean")

                val config = BeanRefreshConfig(
                    beanName = "greetingBean",
                    classType = SpringBean::class,
                )

                beanRefresher.refresh(config)

                val newBean = applicationContext.getBean("greetingBean")

                newBean shouldBe oldBean
            }
        }
    }
}
