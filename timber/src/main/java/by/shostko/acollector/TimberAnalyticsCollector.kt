package by.shostko.acollector

import android.app.Activity
import android.os.Bundle
import timber.log.Timber

class TimberAnalyticsCollector(
    private val tag: String = TAG,
    private val dataMapper: ((Bundle) -> CharSequence)? = null
) : Collector {

    companion object {
        private const val TAG = "ACollector"
        private const val DATA_MAPPER = " -> "
        private const val DATA_SEPARATOR = "\n"
    }

    private var enabled: Boolean = true

    override fun reset() {
        Timber.tag(tag).v("reset")
    }

    override fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
        Timber.tag(tag).v(if (enabled) "enabled" else "disabled")
    }

    override fun track(event: String, data: Bundle?) {
        if (enabled) {
            if (data == null || data.isEmpty) {
                Timber.tag(tag).v(event)
            } else if (dataMapper != null) {
                Timber.tag(tag).v("%s:%s", event, dataMapper.invoke(data))
            } else {
                val builder = StringBuilder()
                for (key in data.keySet()) {
                    builder.append(DATA_SEPARATOR)
                        .append(key)
                        .append(DATA_MAPPER)
                        .append(data[key])
                }
                Timber.tag(tag).v("%s:%s", event, builder)
            }
        }
    }

    override fun setScreen(activity: Activity, name: String?, clazz: Class<*>?) {
        if (enabled) {
            Timber.tag(tag).v("screen -> %s: %s (%s)", activity.javaClass.simpleName, name, clazz?.simpleName)
        }
    }

    override fun setUser(identifier: String?) {
        if (enabled) {
            Timber.tag(tag).v("userId -> %s", identifier)
        }
    }

    override fun setProperty(key: String, value: String?) {
        if (enabled) {
            Timber.tag(tag).v("property: %s -> %s", key, value)
        }
    }

    override fun setProperty(key: String, value: Boolean?) {
        if (enabled) {
            Timber.tag(tag).v("property: %s -> %s", key, value)
        }
    }

    override fun setProperty(key: String, value: Double?) {
        if (enabled) {
            Timber.tag(tag).v("property: %s -> %s", key, value)
        }
    }

    override fun setProperty(key: String, value: Float?) {
        if (enabled) {
            Timber.tag(tag).v("property: %s -> %s", key, value)
        }
    }

    override fun setProperty(key: String, value: Int?) {
        if (enabled) {
            Timber.tag(tag).v("property: %s -> %s", key, value)
        }
    }

    override fun setProperty(key: String, value: Long?) {
        if (enabled) {
            Timber.tag(tag).v("property: %s -> %s", key, value)
        }
    }
}
