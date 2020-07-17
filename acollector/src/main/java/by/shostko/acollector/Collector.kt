@file:Suppress("unused")

package by.shostko.acollector

import android.app.Activity
import android.os.Bundle

interface Collector {
    fun reset()
    fun setEnabled(enabled: Boolean)
    fun enable() = setEnabled(true)
    fun disable() = setEnabled(false)
    fun track(event: String, data: Bundle?)
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

        fun set(value: String?) = ACollector.setProperty(name, value)
        fun set(value: Boolean?) = ACollector.setProperty(name, value)
        fun set(value: Double?) = ACollector.setProperty(name, value)
        fun set(value: Float?) = ACollector.setProperty(name, value)
        fun set(value: Int?) = ACollector.setProperty(name, value)
        fun set(value: Long?) = ACollector.setProperty(name, value)
    }

    interface Event {
        val name: String

        fun track(data: Bundle? = null) {
            ACollector.track(name, data)
        }

        fun track(vararg objs: Any?) {
            if (objs.isNullOrEmpty()) {
                ACollector.track(name, null)
            } else {
                val data = Bundle()
                objs.forEach { data.bundle(it) }
                ACollector.track(name, data)
            }
        }

        private fun Bundle.bundle(obj: Any?) {
            if (obj == null) {
                putString(VALUE, NULL)
            } else {
                val clazz = obj.javaClass
                if (ACollector.bundlers.containsKey(clazz)) {
                    ACollector.bundlers[clazz]?.invoke(this, obj)
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
                        is Throwable -> putString(ERROR, obj.message)
                        is Map<*, *> -> add(obj)
                        is Bundle -> putAll(obj as Bundle?)
                        else -> putString(clazz.simpleName, obj.toString())
                    }
                }
            }
        }

        private fun Bundle.add(map: Map<*, *>) {
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

        companion object {
            private const val VALUE = "value"
            private const val NULL = "null"
            private const val CLASS = "class"
            private const val ERROR = "error"
        }
    }
}