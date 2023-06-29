package io.opengood.commons.spring.constant

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SpringPropertyPlaceholdersTest : StringSpec({

    "SpringPropertyPlaceholders constant should return spring application property placeholder" {
        SpringPropertyPlaceholders.APPLICATION_NAME shouldBe "${'$'}{spring.application.name}"
    }
})
