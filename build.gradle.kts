import io.opengood.gradle.enumeration.ProjectType

plugins {
    id("io.opengood.gradle.config") version "1.4.0"
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
    const val SPRING_CLOUD_CONTRACT_STUB_RUNNER = "2.2.5.RELEASE"
    const val WIREMOCK = "2.27.2"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner:${Versions.SPRING_CLOUD_CONTRACT_STUB_RUNNER}")
    testImplementation("com.github.tomakehurst:wiremock:${Versions.WIREMOCK}")
}