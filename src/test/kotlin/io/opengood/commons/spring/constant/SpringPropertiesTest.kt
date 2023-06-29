package io.opengood.commons.spring.constant

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SpringPropertiesTest : StringSpec({

    "SpringProperties constant should return spring application property name" {
        SpringProperties.APPLICATION_NAME shouldBe "spring.application.name"
    }
})
