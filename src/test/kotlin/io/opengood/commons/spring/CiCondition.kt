package io.opengood.commons.spring

import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

class CiCondition : EnabledCondition {

    override fun enabled(kclass: KClass<out Spec>): Boolean =
        System.getenv()["CI"] == "true"
}
