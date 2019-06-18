package com.nomadworks.crtest

import android.app.Application
import timber.log.Timber

class MainApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        initLogging()
        Timber.d("[ktcr] It's initialised")
    }

    private fun initLogging() {
        Timber.plant(Timber.DebugTree())
    }
}