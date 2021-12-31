package io.opengood.commons.spring.webclient

import app.AppProperties
import app.TestApplication
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer
import org.springframework.boot.test.context.assertj.AssertableApplicationContext
import org.springframework.boot.test.context.runner.ApplicationContextRunner

class YamlPropertySourceFactoryTest : WordSpec() {

    init {
        val contextRunner = ApplicationContextRunner()
            .withUserConfiguration(TestApplication::class.java)
            .withInitializer(ConfigDataApplicationContextInitializer())

        "YAML property source factory" should {
            "Parse custom YAML file and load properties bean" {
                contextRunner
                    .run { context: AssertableApplicationContext ->
                        val appProperties = context.getBean(AppProperties::class.java)
                        with(appProperties) {
                            shouldNotBeNull()

                            properties shouldBe mapOf(
                                "foo" to "bar",
                                "baz" to "paz"
                            )
                        }
                    }
            }
        }
    }
}
