package test

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase

class TestAppender(
    val events: MutableList<ILoggingEvent> = mutableListOf(),
) : AppenderBase<ILoggingEvent>() {

    fun lastLoggedEvent(): ILoggingEvent? =
        events.lastOrNull()

    override fun append(eventObject: ILoggingEvent) {
        events.add(eventObject)
    }
}
