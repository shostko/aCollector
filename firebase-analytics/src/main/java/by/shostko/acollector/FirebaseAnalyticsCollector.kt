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

    override fun track(event: String, data: Bundle?) {
        firebase.logEvent(event, data)
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
}
