package com.bd.deliverytiger.app.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import java.util.regex.Matcher
import java.util.regex.Pattern

object Validator {
    val VALID_EMAIL_ADDRESS_REGEX: Pattern = Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
        Pattern.CASE_INSENSITIVE
    )
    val VALID_MOBILE_NUMBER_REGEX: Pattern = Pattern.compile(
        "^(01|০১)?[0-9০১২৩৪৫৬৭৮৯]{9}$",
        Pattern.CASE_INSENSITIVE
    )
    val VALID_MOBILE_NUMBER_REGEXAgain: Pattern = Pattern.compile(
        "(8801|৮৮০১)?[0-9০১২৩৪৫৬৭৮৯]{9}$",
        Pattern.CASE_INSENSITIVE
    )

    fun isValidEmail(email: String?): Boolean {
        val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email)
        return matcher.find()
    }

    fun isValidMobileNumber(mobileNumber: String?): Boolean {
        val matcher: Matcher =
            VALID_MOBILE_NUMBER_REGEX.matcher(mobileNumber)
        return matcher.find()
    }

    fun isValidMobileNumberAgain(mobileNumber1: String?): Boolean {
        val matcher: Matcher =
            VALID_MOBILE_NUMBER_REGEXAgain.matcher(mobileNumber1)
        return matcher.find()
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
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                activity!!.currentFocus!!.windowToken, 0)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}