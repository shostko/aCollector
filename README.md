# aCollector

[ ![Download](https://api.bintray.com/packages/shostko/android/aCollector/images/download.svg) ](https://bintray.com/shostko/android/aCollector/_latestVersion)

Analytics Collector library for android

## How to use

First initialize ACollector in your Application:
```
ACollector.init {
    // register necessary Collector instances
    if (isDebug) {
        // to log all events in debug
        register(TimberCollector())
    } else {
        // to collect events into Analytics Services in release (see below for variants)
        register(FirebaseAnalyticsCollector(context))
        register(MixpanelCollector(context, "token"))
    }
    // you can specify how exactly some MyModel should be tracked
    bundle(MyModel::class.java) {
        putString("my_model_id", it.id())
    }
    // to enable all Collectors by default
    enable()
}
```

 Then you can use following methods to track events and other actions:

```
ACollector.track("event_name", eventData)
ACollector.setProperty("property_name", propertyValue)
ACollector.setUser("user_identifier")
ACollector.setScreen(activity, "screen_name", class)
```

And all this information will be delivered to analytics platform you have registered.

## Advanced usage

For more advanced ways to use the library please refer to the sample (TODO)

## Integration

As soon as it is still in development you should add to your project Gradle configuration:

```gradle
repositories {
    maven { url "https://dl.bintray.com/shostko/android" }
}
```

Base module integration:
```gradle
dependencies {
    implementation 'by.shostko:aCollector:0.+'
}
```

##  Collectors

Also you can add one or more ready to use implementations of Collectors:

### [Firebase Analytics](https://firebase.google.com/docs/analytics/get-started?platform=android)

```
implementation 'by.shostko:acollector-firebase-analytics:0.+'
implementation 'com.google.firebase:firebase-analytics:17.+'
```

### [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics/get-started?platform=android)

```
implementation 'by.shostko:acollector-firebase-crashlytics:0.+'
implementation 'com.google.firebase:firebase-crashlytics:17.+'
```

### [Mixpanel](https://developer.mixpanel.com/docs)

```
implementation 'by.shostko:acollector-mixpanel:0.+'
implementation 'com.mixpanel.android:mixpanel-android:5.+'
```

### [Timber](https://github.com/JakeWharton/timber)

Just logging all events, useful for testing.

```
implementation 'by.shostko:acollector-timber:0.+'
implementation 'com.jakewharton.timber:timber:4.+'
```

 ## Your own Collector

Or there is an option to create your own Collector, just implement [Collector interface](https://github.com/shostko/aCollector/blob/master/acollector/src/main/java/by/shostko/acollector/Collector.kt) and register your new instance.

## License

Released under the [Apache 2.0 license](LICENSE).

```
Copyright 2019 Sergey Shostko

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
