@file:Suppress("unused")

package by.shostko.acollector

import android.app.Activity
import java.util.*

open class CompositeCollector(
    private val collectors: List<Collector> = emptyList()
) : Collector {

    override fun reset() {
        collectors.forEach { it.reset() }
    }

    override fun setEnabled(enabled: Boolean) {
        collectors.forEach { it.setEnabled(enabled) }
    }

    override fun track(event: String, data: Map<String, Any?>?) {
        collectors.forEach { it.track(event, data) }
    }

    internal fun track(holder: EventHolder) {
        collectors.forEach { it.track(holder) }
    }

    override fun setScreen(activity: Activity, name: String?, clazz: Class<*>?) {
        collectors.forEach { it.setScreen(activity, name, clazz) }
    }

    override fun setUser(identifier: String?) {
        collectors.forEach { it.setUser(identifier) }
    }

    override fun setProperty(key: String, value: String?) {
        collectors.forEach { it.setProperty(key, value) }
    }

    override fun setProperty(key: String, value: Boolean?) {
        collectors.forEach { it.setProperty(key, value) }
    }

    override fun setProperty(key: String, value: Double?) {
        collectors.forEach { it.setProperty(key, value) }
    }

    override fun setProperty(key: String, value: Float?) {
        collectors.forEach { it.setProperty(key, value) }
    }

    override fun setProperty(key: String, value: Int?) {
        collectors.forEach { it.setProperty(key, value) }
    }

    override fun setProperty(key: String, value: Long?) {
        collectors.forEach { it.setProperty(key, value) }
    }

    internal class Internal(
        private val collectors: MutableList<Collector> = LinkedList()
    ) : CompositeCollector(collectors) {
        fun register(collector: Collector) = collectors.add(collector)
    }
}
