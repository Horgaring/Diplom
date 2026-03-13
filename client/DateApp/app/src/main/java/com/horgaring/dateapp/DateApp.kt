package com.horgaring.dateapp

import android.app.Application

class DateApp : Application() {

    companion object {
        lateinit var instance: DateApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
