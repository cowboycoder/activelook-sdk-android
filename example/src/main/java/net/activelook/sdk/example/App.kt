package net.activelook.sdk.example

import android.app.Application
import net.activelook.sdk.ActiveLookSdk

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        ActiveLookSdk.getInstance(this)
    }

}