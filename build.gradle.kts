import io.opengood.gradle.enumeration.ProjectType

plugins {
    id("io.opengood.gradle.config")
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

configurations.forEach { config ->
    with(config) {
        exclude("ch.qos.logback")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:_")
    implementation("org.springframework.boot:spring-boot-starter-webflux:_")

    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:_")
    testImplementation("com.github.tomakehurst:wiremock:_")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner:_")
    testImplementation("uk.org.lidalia:slf4j-test:_")
}
