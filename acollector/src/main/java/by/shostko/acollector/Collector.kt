@file:Suppress("unused")

package by.shostko.acollector

import android.app.Activity

interface Collector {
    fun reset()
    fun setEnabled(enabled: Boolean)
    fun track(event: String, data: Map<String, Any?>?)
    fun setScreen(activity: Activity, name: String?, clazz: Class<*>?)
    fun setUser(identifier: String?)
    fun setProperty(key: String, value: String?)
    fun setProperty(key: String, value: Boolean?)
    fun setProperty(key: String, value: Double?)
    fun setProperty(key: String, value: Float?)
    fun setProperty(key: String, value: Int?)
    fun setProperty(key: String, value: Long?)

    interface Property {
        val name: String

        fun set(value: String?) = ACollector.setProperty(this, value)
        fun set(value: Boolean?) = ACollector.setProperty(this, value)
        fun set(value: Double?) = ACollector.setProperty(this, value)
        fun set(value: Float?) = ACollector.setProperty(this, value)
        fun set(value: Int?) = ACollector.setProperty(this, value)
        fun set(value: Long?) = ACollector.setProperty(this, value)
    }

    interface Event {
        val name: String

        fun track(vararg objs: Any?) = ACollector.track(this, *objs)

        companion object {
            private class Simple(override val name: String) : Event
            fun create(name: String): Event = Simple(name)
        }
    }
}

data class EventHolder(
    val event: Collector.Event,
    val data: Array<out Any?>?
) {
    companion object {
        fun create(event: String) = EventHolder(Collector.Event.create(event), null)
        fun create(event: String, vararg data: Any?) = EventHolder(Collector.Event.create(event), data)
        fun create(event: Collector.Event) = EventHolder(event, null)
        fun create(event: Collector.Event, vararg data: Any?) = EventHolder(event, data)
    }

    fun replaceEvent(event: String) = copy(event = Collector.Event.create(event))

    fun replaceEvent(event: Collector.Event) = copy(event = event)

    fun replaceData(vararg data: Any?) = copy(data = data)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventHolder

        if (event != other.event) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = event.hashCode()
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }
}