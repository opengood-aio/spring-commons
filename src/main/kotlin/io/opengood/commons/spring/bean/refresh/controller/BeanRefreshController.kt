package io.opengood.commons.spring.bean.refresh.controller

import io.opengood.commons.spring.bean.refresh.BeanRefresher
import io.opengood.commons.spring.bean.refresh.model.BeanRefreshConfig
import io.opengood.commons.spring.bean.refresh.model.BeanRefreshRequest
import io.opengood.commons.spring.bean.refresh.model.BeanRefreshResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/spring-commons/bean")
@ConditionalOnProperty(prefix = "spring-commons.bean.refresh.controller", name = ["enabled"], havingValue = "true")
class BeanRefreshController(
    private val beanRefresher: BeanRefresher,
) {

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: BeanRefreshRequest): ResponseEntity<BeanRefreshResponse> {
        return try {
            beanRefresher.refresh(
                BeanRefreshConfig(
                    beanName = request.beanName,
                    classType = Class.forName(request.classType),
                ),
            )
            ResponseEntity.ok(BeanRefreshResponse(message = "Successfully refreshed bean '${request.beanName}'"))
        } catch (e: Exception) {
            val message = "Unable to refresh bean '${request.beanName}'"
            log.error(message, e)
            ResponseEntity.badRequest().body(BeanRefreshResponse(message = message))
        }
    }

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}
