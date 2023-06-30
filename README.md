# Spring Commons Library

[![Build](https://github.com/opengoodio/spring-commons/workflows/build/badge.svg)](https://github.com/opengoodio/spring-commons/actions?query=workflow%3Abuild)
[![Release](https://github.com/opengoodio/spring-commons/workflows/release/badge.svg)](https://github.com/opengoodio/spring-commons/actions?query=workflow%3Arelease)
[![Codecov](https://codecov.io/gh/opengoodio/spring-commons/branch/main/graph/badge.svg?token=AEEYTGK87F)](https://codecov.io/gh/opengoodio/spring-commons)
[![Release Version](https://img.shields.io/github/release/opengoodio/spring-commons.svg)](https://github.com/opengoodio/spring-commons/releases/latest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.opengood.commons/spring-commons/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.opengood.commons/spring-commons)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/opengoodio/spring-commons/master/LICENSE)
[![FOSSA](https://app.fossa.com/api/projects/custom%2B22161%2Fgithub.com%2Fopengoodio%2Fspring-commons.svg?type=small)](https://app.fossa.com/projects/custom%2B22161%2Fgithub.com%2Fopengoodio%2Fspring-commons?ref=badge_small)
[![Sonatype Lift](https://lift.sonatype.com/api/badge/github.com/opengoodio/spring-commons)]

Commons library containing reusable patterns, extensions, properties, beans, and
objects for Spring and Spring Boot

## Compatibility

* Java 17
* Spring Boot 3

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

### Reusable Spring Properties

Common Spring properties are often referenced in code for importing
configuration values. Rather than defining these constantly, simply refer to
them as constants.

| Constant                            | Spring Property           |
|-------------------------------------|---------------------------|
| `SpringProperties.APPLICATION_NAME` | `spring.application.name` |

---

### Reusable Spring Property Placeholders

Similarly, when using `@Value` to import Spring property values, one needs to
wrap `${}` around the property. These are also provided as constants one can
simply refer.

| Constant                                      | Spring Property Placeholder  |
|-----------------------------------------------|------------------------------|
| `SpringPropertyPlaceholders.APPLICATION_NAME` | `${spring.application.name}` |

Example:

```kotlin
import io.opengood.commons.spring.constant.SpringPropertyPlaceholders

@Configuration
class AppConfig {

    @Bean
    fun bean(@Value(SpringPropertyPlaceholders.APPLICATION_NAME) value: String): String {
        // configure bean
    }
}
```

---

### Reusable Spring Bean Properties

Sometimes one needs to override Spring Beans and remembering the specific
property is hard. A constant is provided to simplify this:

| Constant                   | Spring Bean Property                                |
|----------------------------|-----------------------------------------------------|
| `SpringBean.BEAN_OVERRIDE` | `spring.main.allow-bean-definition-overriding=true` |

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

---

### Bean Refresher

**EXPERIMENTAL**

Sometimes one needs to dynamically refresh a Spring bean at runtime without
restarting the application container. For example, a Spring bean may need to
fetch updated data from a database and cache the results inside a bean.

The `BeanRefresher` component allows one to specify a bean name and its fully
qualified class type to trigger a refresh of a bean without restarting the
application container. On the next request for dependencies that use the bean,
the updated instance will be injected.

A REST controller endpoint is provided to allow programmatic refresh of Spring
beans:

**Request:**

```http request
POST http://localhost:8080/spring-commons/bean/refresh
Accept: application/json
Content-Type: application/json

{"beanName":"greetingBean", "classType":"app.bean.GreetingBean"}
```

**Response:**

```http request
{"message":"Successfully refreshed bean 'greetingBean'"}
```

---

### WebClient Request and Response Loggers

By default, using `WebClient` does not provide logging of request and response
information. Functions are provided to simplify this:

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
import io.opengood.commons.spring.webclient.logRequest
import io.opengood.commons.spring.webclient.logResponse

@Configuration
class WebClientConfig {

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .baseUrl("http://localhost:8080")
            .defaultHeader(
                HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE
            )
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

---

### Logging Web Client Builder

When one needs to log details of Spring `WebClient` requests and responses, one
can configure the Netty `HttpClient` to wiretap and log request and response
details at level `DEBUG`.

A `loggingWebClientBuilder` Spring bean is provided that is auto configured when
the following configuration is added to `application.yml`:

```yaml
spring-commons:
  web-client:
    logging:
      enabled: true
```

Then simply create a `WebClient` bean using the builder:

```kotlin
@Configuration
class TestWebClientConfig {

    fun webClient(
        @Qualifier("loggingWebClientBuilder") webClientBuilder: WebClient.Builder
    ) =
        webClientBuilder
            .baseUrl("http://localhost:8080")
            .defaultHeader(
                HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE
            )
            .build()
}
```

In order for Netty to write logs, its logger much be configured at level `DEBUG`
in `application.yml`:

```yaml
logging:
  level:
    reactor.netty.http.client.HttpClient: DEBUG
```

Any `WebClient` requests and responses used by the bean will be logged similar
to as follows:

**Request:**

```http request
15:27:02.060 [qtp1001320766-66] DEBUG org.eclipse.jetty.server.HttpChannel MDC= - REQUEST for //localhost:11729/greeting?firstName=John on HttpChannelOverHttp@34998901{s=HttpChannelState@22052be6{s=IDLE rs=BLOCKING os=OPEN is=IDLE awp=false se=false i=true al=0},r=1,c=false/false,a=IDLE,uri=//localhost:11729/greeting?firstName=John,age=0}
GET //localhost:11729/greeting?firstName=John HTTP/1.1
User-Agent: ReactorNetty/1.0.14
Host: localhost:11729
Accept: */*
Content-Type: application/json
```

**Response:**

```http request
15:27:02.152 [reactor-http-nio-2] DEBUG i.o.c.s.w.LoggingWebClientConfig$$EnhancerBySpringCGLIB$$498409ee MDC= - [affd975a-1, L:/127.0.0.1:63371 - R:localhost/127.0.0.1:11729] READ: 252B HTTP/1.1 200 OK
Content-Type: application/json
Matched-Stub-Id: 62600bcd-4064-40a7-8547-bdc3af2577b5
Vary: Accept-Encoding, User-Agent
Transfer-Encoding: chunked
Server: Jetty(9.4.44.v20210927)

27
{"firstName":"John","lastName":"Smith"}
0
```

**Note:** This data should only be used for diagnostic purposes and should be
disabled in production.

---

### YAML Property Source Factory

Spring supports turning YAML configuration files into properties beans but only
for `application*.yml` files. Custom YAML files are not supported. To enable
this, use the `YamlPropertySourceFactory` on `@ConfigurtionProperties` bean
classes:

Example:

```kotlin
import io.opengood.commons.spring.property.YamlPropertySourceFactory

@Configuration
@ConfigurationProperties(prefix = "app")
@ConstructorBinding
@PropertySource(
    value = ["classpath:app-properties.yml"],
    factory = YamlPropertySourceFactory::class
)
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
