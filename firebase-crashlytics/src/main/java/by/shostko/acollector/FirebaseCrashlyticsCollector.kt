package by.shostko.acollector

import android.app.Activity
import android.os.Bundle
import com.google.firebase.crashlytics.FirebaseCrashlytics

open class FirebaseCrashlyticsCollector(
    private val dataMapper: ((Bundle) -> CharSequence)? = null
) : Collector {

    companion object {
        private const val FORMAT_SHORT = "[EVENT] %s"
        private const val FORMAT_DETAILED = "[EVENT] %s:%s"
        private const val DATA_MAPPER = " -> "
        private const val DATA_SEPARATOR = "\n"
        private const val DATA_EMPTY = ""
        private const val KEY_SCREEN = "screen"
    }

    private val firebase = FirebaseCrashlytics.getInstance()

    override fun reset() {
        firebase.deleteUnsentReports()
    }

    override fun setEnabled(enabled: Boolean) {
        firebase.setCrashlyticsCollectionEnabled(enabled)
    }

    override fun track(event: String, data: Bundle?) {
        if (data == null || data.isEmpty) {
            firebase.log(String.format(FORMAT_SHORT, event))
        } else if (dataMapper != null) {
            firebase.log(String.format(FORMAT_DETAILED, event, dataMapper.invoke(data)))
        } else {
            val builder = StringBuilder()
            for (key in data.keySet()) {
                builder.append(DATA_SEPARATOR)
                    .append(key)
                    .append(DATA_MAPPER)
                    .append(data[key])
            }
            firebase.log(String.format(FORMAT_DETAILED, event, builder))
        }
    }

    override fun setScreen(activity: Activity, name: String?, clazz: Class<*>?) {
        if (clazz != null) {
            firebase.setCustomKey(KEY_SCREEN, clazz.simpleName)
        } else {
            firebase.setCustomKey(KEY_SCREEN, name ?: DATA_EMPTY)
        }
    }

    override fun setUser(identifier: String?) {
        firebase.setUserId(identifier ?: DATA_EMPTY)
    }

    override fun setProperty(key: String, value: String?) {
        firebase.setCustomKey(key, value ?: DATA_EMPTY)
    }

    override fun setProperty(key: String, value: Boolean?) {
        if (value == null) {
            firebase.setCustomKey(key, DATA_EMPTY)
        } else {
            firebase.setCustomKey(key, value)
        }
    }

    override fun setProperty(key: String, value: Double?) {
        if (value == null) {
            firebase.setCustomKey(key, DATA_EMPTY)
        } else {
            firebase.setCustomKey(key, value)
        }
    }

    override fun setProperty(key: String, value: Float?) {
        if (value == null) {
            firebase.setCustomKey(key, DATA_EMPTY)
        } else {
            firebase.setCustomKey(key, value)
        }
    }

    override fun setProperty(key: String, value: Int?) {
        if (value == null) {
            firebase.setCustomKey(key, DATA_EMPTY)
        } else {
            firebase.setCustomKey(key, value)
        }
    }

    override fun setProperty(key: String, value: Long?) {
        if (value == null) {
            firebase.setCustomKey(key, DATA_EMPTY)
        } else {
            firebase.setCustomKey(key, value)
        }
    }
}
