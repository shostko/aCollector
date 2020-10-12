package by.shostko.acollector

import android.app.Activity
import android.os.Bundle
import java.util.*

internal class CompositeCollector : Collector {

    private val collectors: MutableList<Collector> = LinkedList()

    fun register(collector: Collector) = collectors.add(collector)

    override fun reset() {
        collectors.forEach { it.reset() }
    }

    override fun setEnabled(enabled: Boolean) {
        collectors.forEach { it.setEnabled(enabled) }
    }

    override fun track(event: String, data: Bundle?) {
        collectors.forEach { it.track(event, data) }
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
}