package io.opengood.commons.spring.constant

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SpringBeanTest : StringSpec({

    "SpringBean constant should return bean override property name" {
        SpringBean.BEAN_OVERRIDE shouldBe "spring.main.allow-bean-definition-overriding=true"
    }
})
