package com.aditya.reactivepresenterarchitecture

import android.app.Application
import leakcanary.AppWatcher

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppWatcher.manualInstall(this)
    }
}