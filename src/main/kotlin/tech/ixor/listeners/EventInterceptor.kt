package tech.ixor.listeners

import org.slf4j.LoggerFactory
import love.forte.di.annotation.Beans
import love.forte.simbot.Timestamp
import love.forte.simbot.event.EventProcessingInterceptor
import love.forte.simbot.event.EventProcessingResult
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@Beans
class EventInterceptor : EventProcessingInterceptor {
    companion object {
        private val logger = LoggerFactory.getLogger(javaClass)
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun intercept(context: EventProcessingInterceptor.Context): EventProcessingResult {
        val event = context.eventContext.event

        // Before event
        val eventTime = event.timestamp.second
        val currentTime = Timestamp.now().second
        val timeDiff = currentTime - eventTime
        if (timeDiff > 10) {
            logger.warn("Event ${event.id} is ${timeDiff}s old. Abort processing.")
            return EventProcessingResult.Empty
        }

        // Process event
        val timedValue = measureTimedValue {
            context.proceed()
        }

        // After event
        logger.info("Event ${event.id} processed in ${timedValue.duration}")
        return timedValue.value
    }
}
