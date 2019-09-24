package com.bd.deliverytiger.app.utils

import android.app.Application

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }
}