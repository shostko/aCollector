@file:Suppress("unused")

package by.shostko.acollector

import android.app.Activity
import android.os.Bundle

interface Collector {
    fun reset()
    fun setEnabled(enabled: Boolean)
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

        fun set(value: String?) = ACollector.setProperty(this, value)
        fun set(value: Boolean?) = ACollector.setProperty(this, value)
        fun set(value: Double?) = ACollector.setProperty(this, value)
        fun set(value: Float?) = ACollector.setProperty(this, value)
        fun set(value: Int?) = ACollector.setProperty(this, value)
        fun set(value: Long?) = ACollector.setProperty(this, value)
    }

    interface Event {
        val name: String

        fun track(data: Bundle? = null) = ACollector.track(this, data)
        fun track(vararg objs: Any?) = ACollector.track(this, *objs)
    }
}