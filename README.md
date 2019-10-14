# ActiveLook SDK for Android

[![Build Status](https://app.bitrise.io/app/85a5186418ed1ff0/status.svg?token=yHb_DJn1lfDG3fUGZzZ6Ig&branch=master)](https://app.bitrise.io/app/85a5186418ed1ff0) [ ![Download](https://api.bintray.com/packages/activelookteam/activelook-android-sdk/activelook-android-sdk/images/download.svg) ](https://bintray.com/activelookteam/activelook-android-sdk/activelook-android-sdk/_latestVersion)

----

# About ActiveLook

Check our documentation [here](https://github.com/ActiveLook/sdk-doc).

# Getting started !

### Import the Android library


```groovy
implementation 'net.activelook.sdk:0.1.0'
```

### Initialize the library

```kotlin
class App : Application {
    override fun onCreate() {
        val sdkInstance = ActiveLookSdk.getInstance(appContext)
    }
}

```

### Pair your ActiveLook Development Kit

**The ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission need to be granted before to start scanning.**

```kotlin
private val scanCallback = object : ActiveLookSdk.ScanningCallback {

    override fun foundDevices(devices: List<BluetoothDevice>) {
        // Find the device you want
    }

    override fun scanError(err: ActiveLookSdk.ScanningCallback.Error.ScanFailed) {
        // Display an error
    }
}

private val connectionListener = object : ActiveLookSdk.ConnectionListener {
    override fun activeLookConnectionEstablished() {
        // The device is connected
    }

    override fun activeLookConnectionTerminated(reason: GattClosedReason) {
        // The device is disconnected
    }
}

private fun startScanning() {
    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_ACCESS_COARSE_LOCATION)
    } else {
        sdkInstance.connectionListener = connectionListener
        sdkInstance.startScanning(scanCallback)
    }
}
```



### Display your first screen

```kotlin
val screen = Screen.Builder(yourJsonString)
    .build()

sdkInstance.enqueueOperation(ActiveLookOperation.AddScreen(screen))

sdkInstance.enqueueOperation(ActiveLookOperation.DisplayScreen(screen.id))
```

ℹ️ See full example [here](https://github.com/ActiveLook/sdk-android/tree/master/example).

# Give feedback

Please contact us at developer@activelook.net

# Licence
```
Copyright 2019 Microoled

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