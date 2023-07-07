package io.opengood.commons.spring.bean.refresh.model

data class BeanRefreshConfig<T : Any>(
    val beanName: String,
    val classType: Class<T>,
)
