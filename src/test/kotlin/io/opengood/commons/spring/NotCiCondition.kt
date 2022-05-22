package io.opengood.commons.spring

import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

class NotCiCondition : EnabledCondition {

    override fun enabled(kclass: KClass<out Spec>): Boolean =
        !System.getenv().containsKey(CI) || System.getenv()[CI] != "true"

    companion object {
        const val CI = "CI"
    }
}
