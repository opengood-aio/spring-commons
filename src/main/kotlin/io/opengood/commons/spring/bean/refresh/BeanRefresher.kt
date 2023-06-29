package io.opengood.commons.spring.bean.refresh

import io.opengood.commons.spring.bean.refresh.model.BeanRefreshConfig
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "spring-commons.bean.refresh", name = ["enabled"], havingValue = "true")
class BeanRefresher(private val applicationContext: ApplicationContext) {

    fun <T : Any> refresh(config: BeanRefreshConfig<T>) {
        val beanRegistry = applicationContext.autowireCapableBeanFactory as BeanDefinitionRegistry
        val oldBeanDefinition = beanRegistry.getBeanDefinition(config.beanId)
        val newBeanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(config.classType.java)
        oldBeanDefinition.constructorArgumentValues.indexedArgumentValues.forEach {
            it.value.name?.let { beanName -> newBeanDefinitionBuilder.addConstructorArgReference(beanName) }
            it.value.value?.let { arg -> newBeanDefinitionBuilder.addConstructorArgValue(arg) }
        }
        beanRegistry.removeBeanDefinition(config.beanId)
        beanRegistry.registerBeanDefinition(config.beanId, newBeanDefinitionBuilder.beanDefinition)
    }
}
