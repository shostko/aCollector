@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package by.shostko.acollector

import android.app.Activity
import android.os.Bundle
import java.util.*
import kotlin.collections.HashMap

object ACollector : Collector {

    private const val VALUE = "value"
    private const val NULL = "null"
    private const val CLASS = "class"
    private const val THROWABLE = "throwable"

    private val instance: CompositeCollector = CompositeCollector()
    private val mappers: LinkedList<Mapper<*>> = LinkedList()
    private val interceptors: LinkedList<Interceptor> = LinkedList()

    fun init(initializer: ACollector.() -> Unit) = this.initializer()

    fun enable(initializer: ACollector.() -> Unit) {
        init(initializer)
        enable()
    }

    fun register(collector: Collector) = instance.register(collector)

    fun intercept(interceptor: Interceptor) {
        interceptors.addLast(interceptor)
    }

    fun <T : Any> map(clazz: Class<T>, mapper: MutableMap<String, Any?>.(T) -> Unit) {
        mappers.addLast(Mapper(clazz, mapper))
    }

    override fun reset() = instance.reset()
    override fun setEnabled(enabled: Boolean) = instance.setEnabled(enabled)
    override fun track(event: String, data: Map<String, Any?>?) = track(EventHolder.create(event, data))
    override fun setScreen(activity: Activity, name: String?, clazz: Class<*>?) = instance.setScreen(activity, name, clazz)
    override fun setUser(identifier: String?) = instance.setUser(identifier)
    override fun setProperty(key: String, value: String?) = instance.setProperty(key, value)
    override fun setProperty(key: String, value: Boolean?) = instance.setProperty(key, value)
    override fun setProperty(key: String, value: Double?) = instance.setProperty(key, value)
    override fun setProperty(key: String, value: Float?) = instance.setProperty(key, value)
    override fun setProperty(key: String, value: Int?) = instance.setProperty(key, value)
    override fun setProperty(key: String, value: Long?) = instance.setProperty(key, value)

    fun enable() = instance.setEnabled(true)
    fun disable() = instance.setEnabled(false)
    fun track(event: String) = track(EventHolder.create(event))
    fun track(event: String, vararg data: Any?) = track(EventHolder.create(event, data))
    fun track(event: Collector.Event) = track(EventHolder.create(event))
    fun track(event: Collector.Event, data: Map<String, Any?>?) = track(EventHolder.create(event, data))
    fun track(event: Collector.Event, vararg data: Any?) = track(EventHolder.create(event, data))
    fun setProperty(property: Collector.Property, value: String?) = instance.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Boolean?) = instance.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Double?) = instance.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Float?) = instance.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Int?) = instance.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Long?) = instance.setProperty(property.name, value)

    private fun track(holder: EventHolder) {
        if (interceptors.isEmpty()) {
            instance.track(holder.event.name, holder.data?.asMap())
        } else {
            var temp: EventHolder? = holder
            for (interceptor in interceptors) {
                temp = temp?.let { interceptor.intercept(it) }
                if (temp == null) {
                    break
                }
            }
            if (temp != null) {
                instance.track(temp.event.name, temp.data?.asMap())
            }
        }
    }

    private fun Array<out Any?>.asMap(): Map<String, Any?> = HashMap<String, Any?>().also {
        forEach { obj -> it.bundle(obj) }
    }

    private fun MutableMap<String, Any?>.bundle(obj: Any?) {
        if (obj == null) {
            put(VALUE, NULL)
        } else {
            val mapper = mappers.firstOrNull { it.canMap(obj) }
            if (mapper != null) {
                mapper.invoke(this, obj)
            } else {
                when (obj) {
                    is String -> put(VALUE, obj)
                    is Boolean -> put(VALUE, obj)
                    is Int -> put(VALUE, obj)
                    is Long -> put(VALUE, obj)
                    is Float -> put(VALUE, obj)
                    is Double -> put(VALUE, obj)
                    is Class<*> -> put(CLASS, obj.simpleName)
                    is Pair<*, *> -> put(obj.first?.toString() ?: NULL, obj.second?.toString() ?: NULL)
                    is Throwable -> put(THROWABLE, obj.message)
                    is Map<*, *> -> putMap(obj)
                    is Bundle -> putBundle(obj)
                    else -> put(obj.javaClass.simpleName, obj.toString())
                }
            }
        }
    }

    private fun MutableMap<String, Any?>.putMap(map: Map<*, *>) {
        for ((key, value) in map.entries) {
            val keyStr = key?.toString() ?: NULL
            when (value) {
                null -> put(keyStr, NULL)
                is String -> put(keyStr, value)
                is Boolean -> put(keyStr, value)
                is Int -> put(keyStr, value)
                is Long -> put(keyStr, value)
                is Float -> put(keyStr, value)
                is Double -> put(keyStr, value)
                else -> put(keyStr, value.toString())
            }
        }
    }

    private fun MutableMap<String, Any?>.putBundle(bundle: Bundle) {
        for (key in bundle.keySet()) {
            val keyStr = key.toString()
            when (val value = bundle.get(key)) {
                null -> put(keyStr, NULL)
                is String -> put(keyStr, value)
                is Boolean -> put(keyStr, value)
                is Int -> put(keyStr, value)
                is Long -> put(keyStr, value)
                is Float -> put(keyStr, value)
                is Double -> put(keyStr, value)
                else -> put(keyStr, value.toString())
            }
        }
    }

    private class Mapper<T>(
        private val clazz: Class<T>,
        private val function: MutableMap<String, Any?>.(T) -> Unit
    ) : (MutableMap<String, Any?>, Any) -> Unit {

        fun canMap(obj: Any): Boolean = clazz.isInstance(obj)

        @Suppress("UNCHECKED_CAST")
        override fun invoke(bundle: MutableMap<String, Any?>, obj: Any) {
            bundle.function(obj as T)
        }
    }
}
