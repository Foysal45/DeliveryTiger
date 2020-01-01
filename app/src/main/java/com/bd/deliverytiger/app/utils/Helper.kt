package com.bd.deliverytiger.app.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R

fun Context.showToast(msg: String) =
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Activity.showToast(msg: String) =
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

/*fun Activity.hideKeyboard() {
    hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
}*/

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.setStatusBarColor(@ColorRes colorCode: Int = R.color.colorPrimaryDark) {
    window?.statusBarColor = ActivityCompat.getColor(this, colorCode)
}