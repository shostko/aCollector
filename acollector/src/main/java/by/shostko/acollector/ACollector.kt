package by.shostko.acollector

import android.app.Activity
import android.os.Bundle
import java.util.*
import kotlin.collections.HashMap

object ACollector : Collector {

    private val instance: CompositeCollector = CompositeCollector()
    internal val bundlers: MutableMap<Class<*>, Bundle.(Any) -> Unit> = HashMap()

    fun init(initializer: ACollector.() -> Unit) = this.initializer()

    fun register(collector: Collector) = instance.register(collector)

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> bundle(clazz: Class<T>, bundler: Bundle.(T) -> Unit) {
        bundlers[clazz] = bundler as Bundle.(Any) -> Unit
    }

    override fun reset() = instance.reset()
    override fun setEnabled(enabled: Boolean) = instance.setEnabled(enabled)
    override fun track(event: String, data: Bundle?) = instance.track(event, data)
    override fun setScreen(activity: Activity, name: String?, clazz: Class<*>?) = instance.setScreen(activity, name, clazz)
    override fun setUser(identifier: String?) = instance.setUser(identifier)
    override fun setProperty(key: String, value: String?) = instance.setProperty(key, value)
    override fun setProperty(key: String, value: Boolean?) = instance.setProperty(key, value)
    override fun setProperty(key: String, value: Double?) = instance.setProperty(key, value)
    override fun setProperty(key: String, value: Float?) = instance.setProperty(key, value)
    override fun setProperty(key: String, value: Int?) = instance.setProperty(key, value)
    override fun setProperty(key: String, value: Long?) = instance.setProperty(key, value)
}

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
