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