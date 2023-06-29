package io.opengood.commons.spring.bean.refresh

import app.TestApplication
import app.config.TestBeanConfig
import io.kotest.core.spec.style.WordSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldNotBe
import io.opengood.commons.spring.bean.refresh.model.BeanRefreshConfig
import io.opengood.commons.spring.constant.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest(
    classes = [
        BeanRefresher::class,
        TestBeanConfig::class,
        TestApplication::class,
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
                val oldBean = applicationContext.getBean("testBeanConfig") as TestBeanConfig

                val config = BeanRefreshConfig(
                    beanId = "testBeanConfig",
                    classType = TestBeanConfig::class,
                )

                beanRefresher.refresh(config)

                val newBean = applicationContext.getBean("testBeanConfig") as TestBeanConfig

                newBean.greeting() shouldNotBe oldBean.greeting()
            }
        }
    }
}
