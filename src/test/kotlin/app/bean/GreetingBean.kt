package app.bean

import app.model.Greeting
import org.springframework.stereotype.Component

@Component
class GreetingBean(
    val greeting: Greeting,
)
