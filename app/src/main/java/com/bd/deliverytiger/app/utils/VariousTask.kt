package com.bd.deliverytiger.app.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object VariousTask {

    fun getCurrentDateTime(): String? {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val date = Date()
        return dateFormat.format(date)
    }

    fun getPreviousDateTime(month: Int): String? {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.MONTH, -month)
        val result: Date = cal.time
        return dateFormat.format(result)
    }

    // show toast method
    fun showLongToast(context: Context?, message: String) {
        if (context != null) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            //toast.setGravity(Gravity.BOTTOM, 0, 0)
            toast.show()
        }
    }

    // show short toast method
    fun showShortToast(context: Context?, message: String) {
        if (context != null) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
            //toast.setGravity(Gravity.BOTTOM, 0, 0)
            toast.show()
        }
    }

    // clear focus if payment lay blinking
    fun editTextEnableOrDisable(et: EditText) {
        et.isSelected = false
        et.isFocusable = false
        et.isFocusableInTouchMode = true
    }

    fun hideSoftKeyBoard(activity: Activity) {
        try {  // hide keyboard if its open
            val inputMethodManager = activity!!.getSystemService(
                Activity.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                activity!!.currentFocus!!.windowToken, 0
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}