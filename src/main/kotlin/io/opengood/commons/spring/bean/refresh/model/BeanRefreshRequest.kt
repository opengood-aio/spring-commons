package io.opengood.commons.spring.bean.refresh.model

data class BeanRefreshRequest(
    val beanName: String,
    val classType: String,
    val recreateBean: Boolean = false,
)
