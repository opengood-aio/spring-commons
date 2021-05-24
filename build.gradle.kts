import io.opengood.gradle.enumeration.ProjectType

plugins {
    id("io.opengood.gradle.config") version "1.22.0"
}

group = "io.opengood.commons"

opengood {
    main {
        projectType = ProjectType.LIB
    }
    artifact {
        description = "Commons library containing reusable patterns, extensions, properties, beans, and objects for Spring and Spring Boot"
    }
}

object Versions {
    const val JACKSON_KOTLIN = "2.12.1"
    const val SLF4J_TEST = "1.2.0"
    const val SPRING_CLOUD_CONTRACT_STUB_RUNNER = "3.0.2"
    const val WIREMOCK = "2.27.2"
}

configurations.forEach { config ->
    with(config) {
        exclude("ch.qos.logback")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON_KOTLIN}")
    testImplementation("com.github.tomakehurst:wiremock:${Versions.WIREMOCK}")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner:${Versions.SPRING_CLOUD_CONTRACT_STUB_RUNNER}")
    testImplementation("uk.org.lidalia:slf4j-test:${Versions.SLF4J_TEST}")
}
