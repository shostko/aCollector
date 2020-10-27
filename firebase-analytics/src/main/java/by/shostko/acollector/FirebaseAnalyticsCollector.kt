package by.shostko.acollector

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

open class FirebaseAnalyticsCollector(context: Context) : Collector {

    private val firebase: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun reset() {
        firebase.resetAnalyticsData()
    }

    override fun setEnabled(enabled: Boolean) {
        firebase.setAnalyticsCollectionEnabled(enabled)
    }

    override fun track(event: String, data: Map<String, Any?>?) {
        firebase.logEvent(event, data?.let { Bundle().apply { putMap(it) } })
    }

    override fun setScreen(activity: Activity, name: String?, clazz: Class<*>?) {
        firebase.setCurrentScreen(activity, name, clazz?.simpleName)
    }

    override fun setUser(identifier: String?) {
        firebase.setUserId(identifier)
    }

    override fun setProperty(key: String, value: String?) {
        firebase.setUserProperty(key, value)
    }

    override fun setProperty(key: String, value: Boolean?) {
        setProperty(key, value?.toString())
    }

    override fun setProperty(key: String, value: Double?) {
        setProperty(key, value?.toString())
    }

    override fun setProperty(key: String, value: Float?) {
        setProperty(key, value?.toString())
    }

    override fun setProperty(key: String, value: Int?) {
        setProperty(key, value?.toString())
    }

    override fun setProperty(key: String, value: Long?) {
        setProperty(key, value?.toString())
    }

    private fun Bundle.putMap(map: Map<String, Any?>) {
        for ((key, value) in map.entries) {
            when (value) {
                null -> putString(key, NULL)
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Double -> putDouble(key, value)
                else -> putString(key, value.toString())
            }
        }
    }

    companion object {
        private const val NULL = "null"
    }
}
