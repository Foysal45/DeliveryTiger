package com.bd.deliverytiger.app.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast

object Helper {
    init {
        fun Context.showToast(msg: String) =
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        fun Activity.showToast(msg: String) =
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}