@file:Suppress("unused")

package by.shostko.acollector

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

    internal val eventName: String
        get() = event.name

    internal val dataAsMap: Map<String, Any?>?
        get() = ACollector.dataAsMap(data)

    fun replaceEvent(event: String) = copy(event = Collector.Event.create(event))

    fun replaceEvent(event: Collector.Event) = copy(event = event)

    fun replaceData(vararg data: Any?) = if (data.isNullOrEmpty()) {
        copy(data = null)
    } else {
        copy(data = data)
    }

    fun appendData(vararg data: Any?) = if (this.data.isNullOrEmpty()) {
        copy(data = data)
    } else {
        copy(data = concatenate(this.data, data))
    }

    private fun concatenate(a: Array<out Any?>, b: Array<out Any?>) = Array(a.size + b.size) {
        if (it < a.size) a[it] else b[it - a.size]
    }

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