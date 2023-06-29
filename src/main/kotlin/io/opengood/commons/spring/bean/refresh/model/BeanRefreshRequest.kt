package io.opengood.commons.spring.bean.refresh.model

data class BeanRefreshRequest(
    val beanId: String,
    val classType: String,
)
