package io.opengood.commons.spring.bean.refresh

import io.opengood.commons.spring.bean.refresh.model.BeanRefreshConfig
import jdk.jfr.Experimental
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "spring-commons.bean.refresh", name = ["enabled"], havingValue = "true")
@Experimental
class BeanRefresher(private val applicationContext: ApplicationContext) {

    fun <T : Any> refresh(config: BeanRefreshConfig<T>) {
        val beanRegistry = applicationContext.autowireCapableBeanFactory as BeanDefinitionRegistry
        if (beanRegistry.containsBeanDefinition(config.beanName)) {
            val oldBeanDefinition = beanRegistry.getBeanDefinition(config.beanName)
            try {
                val newBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(config.classType).beanDefinition
                if (oldBeanDefinition.resolvableType == newBeanDefinition.resolvableType) {
                    beanRegistry.removeBeanDefinition(config.beanName)
                    beanRegistry.registerBeanDefinition(config.beanName, newBeanDefinition)
                }
            } catch (e: Exception) {
                if (!beanRegistry.containsBeanDefinition(config.beanName)) {
                    beanRegistry.registerBeanDefinition(config.beanName, oldBeanDefinition)
                }
                log.error("Unable to refresh bean '${config.beanName}'", e)
            }
        }
    }

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}
