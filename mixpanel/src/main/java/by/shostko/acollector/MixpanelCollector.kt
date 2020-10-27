package by.shostko.acollector

import android.app.Activity
import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject

open class MixpanelCollector(
    context: Context,
    token: String,
    private val setScreenEvent: String = SCREEN
) : Collector {

    companion object {
        private const val SCREEN = "screen"
        private const val ACTIVITY = "activity"
        private const val NAME = "name"
        private const val CLASS = "class"
    }

    private val mixpanel = MixpanelAPI.getInstance(context, token)

    override fun reset() {
        mixpanel.reset()
    }

    override fun setEnabled(enabled: Boolean) {
        if (enabled) {
            mixpanel.optInTracking()
        } else {
            mixpanel.optOutTracking()
        }
    }

    override fun track(event: String, data: Map<String, Any?>?) {
        if (data.isNullOrEmpty()) {
            mixpanel.track(event)
        } else {
            val props = JSONObject()
            for ((key, value) in data.entries) {
                props.put(key, value)
            }
            mixpanel.track(event, props)
        }
    }

    override fun setScreen(activity: Activity, name: String?, clazz: Class<*>?) {
        mixpanel.track(setScreenEvent, JSONObject().apply {
            put(ACTIVITY, activity.javaClass.simpleName)
            put(NAME, name)
            put(CLASS, clazz?.simpleName)
        })
    }

    override fun setUser(identifier: String?) {
        mixpanel.identify(identifier)
        mixpanel.people.identify(identifier)
    }

    override fun setProperty(key: String, value: String?) {
        mixpanel.people.set(key, value)
    }

    override fun setProperty(key: String, value: Boolean?) {
        mixpanel.people.set(key, value)
    }

    override fun setProperty(key: String, value: Double?) {
        mixpanel.people.set(key, value)
    }

    override fun setProperty(key: String, value: Float?) {
        mixpanel.people.set(key, value)
    }

    override fun setProperty(key: String, value: Int?) {
        mixpanel.people.set(key, value)
    }

    override fun setProperty(key: String, value: Long?) {
        mixpanel.people.set(key, value)
    }
}
