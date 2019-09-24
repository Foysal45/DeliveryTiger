package com.bd.deliverytiger.app.utils

import android.util.Log
import com.bd.deliverytiger.app.BuildConfig

object Timber {

    fun d(tag: String = "appLog", msg: String) {
        if (BuildConfig.DEBUG)
            Log.d(tag, msg)
    }

    fun e(tag: String = "appLog", msg: String) {
        if (BuildConfig.DEBUG)
            Log.e(tag, msg)
    }

    fun i(tag: String = "appLog", msg: String) {
        if (BuildConfig.DEBUG)
            Log.i(tag, msg)
    }

    fun w(tag: String = "appLog", msg: String) {
        if (BuildConfig.DEBUG)
            Log.w(tag, msg)
    }

}