@file:Suppress("unused")

package by.shostko.acollector

import java.util.*

fun interface Interceptor {
    fun intercept(input: EventHolder): EventHolder?
}

class SimpleInterceptor(
    private val event: Collector.Event,
    private val mapper: (EventHolder) -> EventHolder?
): Interceptor {
    override fun intercept(input: EventHolder): EventHolder? {
        return if (input.event == event) mapper(input) else input
    }
}

class InterceptedCollector(
    private val original: Collector
) : Collector by original {
    private val interceptors: LinkedList<Interceptor> = LinkedList()
    internal fun addInterceptor(interceptor: Interceptor) = this.interceptors.addLast(interceptor)
    internal fun addInterceptors(interceptors: List<Interceptor>) = this.interceptors.addAll(interceptors)
    internal fun track(holder: EventHolder) = interceptors.handle(original, holder)
}

fun Collector.intercept(event: Collector.Event, mapper: (EventHolder) -> EventHolder?) = intercept(SimpleInterceptor(event, mapper))

fun Collector.intercept(interceptor: Interceptor): InterceptedCollector {
    val result = (this as? InterceptedCollector) ?: InterceptedCollector(this)
    result.addInterceptor(interceptor)
    return result
}

fun Collector.intercept(vararg interceptors: Interceptor): InterceptedCollector {
    val result = (this as? InterceptedCollector) ?: InterceptedCollector(this)
    result.addInterceptors(interceptors.asList())
    return result
}

internal fun Collector.track(holder: EventHolder) = when (this) {
    is InterceptedCollector -> track(holder)
    is CompositeCollector -> track(holder)
    else -> track(holder.eventName, holder.dataAsMap)
}

internal fun List<Interceptor>.handle(collector: Collector, holder: EventHolder) {
    if (isEmpty()) {
        collector.track(holder)
    } else {
        var temp: EventHolder? = holder
        for (interceptor in this) {
            temp = temp?.let { interceptor.intercept(it) }
            if (temp == null) {
                break
            }
        }
        if (temp != null) {
            collector.track(temp)
        }
    }
}
