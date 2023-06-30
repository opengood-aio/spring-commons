package io.opengood.commons.spring.bean.refresh.model

import kotlin.reflect.KClass

data class BeanRefreshConfig<T : Any>(
    val beanName: String,
    val classType: KClass<T>,
)
