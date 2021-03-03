# Spring Commons Library

[![Build](https://github.com/opengoodio/spring-commons/workflows/build/badge.svg)](https://github.com/opengoodio/spring-commons/actions?query=workflow%3Abuild)
[![Release](https://github.com/opengoodio/spring-commons/workflows/release/badge.svg)](https://github.com/opengoodio/spring-commons/actions?query=workflow%3Arelease)
[![Codecov](https://codecov.io/gh/opengoodio/spring-commons/branch/main/graph/badge.svg?token=AEEYTGK87F)](https://codecov.io/gh/opengoodio/spring-commons)
[![Release Version](https://img.shields.io/github/release/opengoodio/spring-commons.svg)](https://github.com/opengoodio/spring-commons/releases/latest)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/opengoodio/spring-commons/master/LICENSE)
[![FOSSA](https://app.fossa.com/api/projects/custom%2B22161%2Fgithub.com%2Fopengoodio%2Fspring-commons.svg?type=small)](https://app.fossa.com/projects/custom%2B22161%2Fgithub.com%2Fopengoodio%2Fspring-commons?ref=badge_small)

Commons library containing reusable patterns, extensions, properties,
beans, and objects for Spring and Spring Boot

## Setup

### Add Dependency

#### Gradle

```groovy
implementation("io.opengood.commons:spring-commons:VERSION")
```

#### Maven

```xml
<dependency>
    <groupId>io.opengood.commons</groupId>
    <artifactId>spring-commons</artifactId>
    <version>VERSION</version>
</dependency>
```

**Note:** See *Release* version badge above for latest version.

## Features

**Note:** All examples are provided in Kotlin

### Reusable Spring Bean Properties

Sometimes one needs to override Spring Beans and remembering the
specific property is hard. A constant is provided to simplify this:

`SpringBean.BEAN_OVERRIDE`

Example:

```kotlin
import io.opengood.commons.spring.constant.SpringBean

@SpringBootTest(
    classes = [TestApplication::class],
    properties = [SpringBean.BEAN_OVERRIDE],
    webEnvironment = WebEnvironment.RANDOM_PORT
)
class ControllerTest : WordSpec() {
    // do stuff
}
```

### WebClient Request and Response Loggers

By default, using `WebClient` does not provide logging of request and
response information. Functions are provided to simplify this:

`logRequest` will log `debug`:
* Request
  * Method
  * URI
  * Headers

`logResponse` will log `debug`:
* Response
  * Status Code
  * Headers

Example:

```kotlin
import io.opengood.commons.spring.function.logRequest
import io.opengood.commons.spring.function.logResponse

@Configuration
class WebClientConfig {

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .baseUrl("http://localhost:8080")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filters { exchangeFilterFunctions ->
                exchangeFilterFunctions.add(logRequest(log))
                exchangeFilterFunctions.add(logResponse(log))
            }
            .build()
    }

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}
```

### YAML Property Source Factory

Spring supports turning YAML configuration files into properties beans
but only for `application*.yml` files. Custom YAML files are not
supported. To enable this, use the `YamlPropertySourceFactory` on
`@ConfigurtionProperties` bean classes:

Example:

```kotlin
import io.opengood.commons.spring.property.YamlPropertySourceFactory

@Configuration
@ConfigurationProperties(prefix = "app")
@ConstructorBinding
@PropertySource(value = ["classpath:app-properties.yml"], factory = YamlPropertySourceFactory::class)
data class AppProperties(
    val properties: Map<String, String> = HashMap()
)
```

YAML file `app-properties.yml`:

```yaml
app:
  properties:
    foo: bar
    baz: paz
```
