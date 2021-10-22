package io.opengood.commons.spring.constant

class SpringPropertyPlaceholders {

    companion object {
        const val APPLICATION_NAME = "${'$'}{spring.application.name}"
    }
}
