@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package by.shostko.acollector

import android.app.Activity
import android.os.Bundle
import java.util.*

object ACollector : Collector {

    private const val VALUE = "value"
    private const val NULL = "null"
    private const val CLASS = "class"
    private const val THROWABLE = "throwable"

    private val instance: CompositeCollector = CompositeCollector()
    private val bundlers: LinkedList<Bundler<*>> = LinkedList()

    fun init(initializer: ACollector.() -> Unit) = this.initializer()

    fun enable(initializer: ACollector.() -> Unit) {
        init(initializer)
        enable()
    }

    fun register(collector: Collector) = instance.register(collector)

    fun <T : Any> bundle(clazz: Class<T>, bundler: Bundle.(T) -> Unit) {
        bundlers.addLast(Bundler(clazz, bundler))
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

    fun enable() = instance.setEnabled(true)
    fun disable() = instance.setEnabled(false)
    fun track(event: String) = instance.track(event, null)
    fun track(event: Collector.Event, data: Bundle? = null) = instance.track(event.name, data)
    fun track(event: String, vararg objs: Any?) = instance.track(event, objs.bundled())
    fun track(event: Collector.Event, vararg objs: Any?) = instance.track(event.name, objs.bundled())
    fun setProperty(property: Collector.Property, value: String?) = instance.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Boolean?) = instance.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Double?) = instance.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Float?) = instance.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Int?) = instance.setProperty(property.name, value)
    fun setProperty(property: Collector.Property, value: Long?) = instance.setProperty(property.name, value)

    private fun Array<out Any?>.bundled(): Bundle = Bundle().also {
        forEach { obj -> it.bundle(obj) }
    }

    private fun Bundle.bundle(obj: Any?) {
        if (obj == null) {
            putString(VALUE, NULL)
        } else {
            val bundler = bundlers.firstOrNull { it.canBundle(obj) }
            if (bundler != null) {
                bundler.invoke(this, obj)
            } else {
                when (obj) {
                    is String -> putString(VALUE, obj)
                    is Boolean -> putBoolean(VALUE, obj)
                    is Int -> putInt(VALUE, obj)
                    is Long -> putLong(VALUE, obj)
                    is Float -> putFloat(VALUE, obj)
                    is Double -> putDouble(VALUE, obj)
                    is Class<*> -> putString(CLASS, obj.simpleName)
                    is Pair<*, *> -> putString(obj.first?.toString() ?: NULL, obj.second?.toString() ?: NULL)
                    is Throwable -> putString(THROWABLE, obj.message)
                    is Map<*, *> -> putMap(obj)
                    is Bundle -> putAll(obj)
                    else -> putString(obj.javaClass.simpleName, obj.toString())
                }
            }
        }
    }

    private fun Bundle.putMap(map: Map<*, *>) {
        for ((key, value) in map.entries) {
            val keyStr = key?.toString() ?: NULL
            when (value) {
                null -> putString(keyStr, NULL)
                is String -> putString(keyStr, value)
                is Boolean -> putBoolean(keyStr, value)
                is Int -> putInt(keyStr, value)
                is Long -> putLong(keyStr, value)
                is Float -> putFloat(keyStr, value)
                is Double -> putDouble(keyStr, value)
                else -> putString(keyStr, value.toString())
            }
        }
    }

    private class Bundler<T>(
        private val clazz: Class<T>,
        private val function: Bundle.(T) -> Unit
    ) : (Bundle, Any) -> Unit {

        fun canBundle(obj: Any): Boolean = clazz.isInstance(obj)

        @Suppress("UNCHECKED_CAST")
        override fun invoke(bundle: Bundle, obj: Any) {
            bundle.function(obj as T)
        }
    }
}
