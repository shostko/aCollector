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

    private val composition: CompositeCollector = CompositeCollector()
    private val mappers: LinkedList<Mapper<*>> = LinkedList()
    private val interceptors: LinkedList<Interceptor> = LinkedList()

    fun init(initializer: ACollector.() -> Unit) = this.initializer()

    fun enable(initializer: ACollector.() -> Unit) {
        init(initializer)
        enable()
    }

    fun register(collector: Collector) = composition.register(collector)

    fun intercept(interceptor: Interceptor) {
        interceptors.addLast(interceptor)
    }

    fun <T : Any> map(clazz: Class<T>, mapper: MutableMap<String, Any?>.(T) -> Unit) {
        mappers.addLast(Mapper(clazz, mapper))
    }

    override fun reset() = composition.reset()
    override fun setEnabled(enabled: Boolean) = composition.setEnabled(enabled)
    override fun track(event: String, data: Map<String, Any?>?) = track(EventHolder.create(event, data))
    override fun setScreen(activity: Activity, name: String?, clazz: Class<*>?) = composition.setScreen(activity, name, clazz)
    override fun setUser(identifier: String?) = composition.setUser(identifier)
    override fun setProperty(key: String, value: String?) = composition.setProperty(key, value)
    override fun setProperty(key: String, value: Boolean?) = composition.setProperty(key, value)
    override fun setProperty(key: String, value: Double?) = composition.setProperty(key, value)
    override fun setProperty(key: String, value: Float?) = composition.setProperty(key, value)
    override fun setProperty(key: String, value: Int?) = composition.setProperty(key, value)
    override fun setProperty(key: String, value: Long?) = composition.setProperty(key, value)

    fun enable() = composition.setEnabled(true)
    fun disable() = composition.setEnabled(false)
    fun track(event: String) = track(EventHolder.create(event))
    fun track(event: String, vararg data: Any?) = track(EventHolder.create(event, data))
    fun track(event: Collector.Event) = track(EventHolder.create(event))
    fun track(event: Collector.Event, data: Map<String, Any?>?) = track(EventHolder.create(event, data))
    fun track(event: Collector.Event, vararg data: Any?) = track(EventHolder.create(event, data))
    fun setProperty(property: Collector.Property, value: String?) = composition.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Boolean?) = composition.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Double?) = composition.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Float?) = composition.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Int?) = composition.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Long?) = composition.setProperty(property.name, value)

    private fun track(holder: EventHolder) {
        if (interceptors.isEmpty()) {
            composition.track(holder.event.name, holder.data?.asMap())
        } else {
            var temp: EventHolder? = holder
            for (interceptor in interceptors) {
                temp = temp?.let { interceptor.intercept(it) }
                if (temp == null) {
                    break
                }
            }
            if (temp != null) {
                composition.track(temp.event.name, temp.data?.asMap())
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
