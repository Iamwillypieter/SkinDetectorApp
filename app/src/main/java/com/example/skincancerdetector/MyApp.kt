package com.example.skincancerdetector

// MyApp.kt

import android.app.Application
import com.example.skincancerdetector.data.LocalStorageHelper

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        LocalStorageHelper.init(this)
    }
}
