package io.opengood.commons.spring.property

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.core.io.support.EncodedResource
import org.springframework.core.io.support.PropertySourceFactory
import java.io.IOException

class YamlPropertySourceFactory : PropertySourceFactory {
    @Throws(IOException::class)
    override fun createPropertySource(
        name: String?,
        resource: EncodedResource,
    ): PropertySource<*> {
        val factory =
            YamlPropertiesFactoryBean().apply {
                setResources(resource.resource)
                afterPropertiesSet()
            }
        val properties = factory.getObject()!!
        return PropertiesPropertySource(getResourceName(name, resource), properties)
    }

    private fun getResourceName(
        name: String?,
        resource: EncodedResource,
    ) = if (!name.isNullOrBlank()) name else resource.resource.filename!!
}
