package com.bd.deliverytiger.app.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.io.File

fun Context.showToast(msg: String) =
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Activity.showToast(msg: String) =
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Activity.setStatusBarColor(@ColorRes colorCode: Int = R.color.colorPrimaryDark) {
    window?.statusBarColor = ActivityCompat.getColor(this, colorCode)
}

fun Activity.progressDialog(message: String = getString(R.string.waiting)): ProgressDialog {

    val dialog = ProgressDialog(this)
    with(dialog) {
        setMessage(message)
    }
    return dialog
}

fun Fragment.progressDialog(message: String = getString(R.string.waiting)): ProgressDialog {

    val dialog = ProgressDialog(requireContext())
    with(dialog) {
        setMessage(message)
    }
    return dialog
}

fun Fragment.alert(title: CharSequence? = null, message: CharSequence? = null, showCancel: Boolean = false, positiveButtonText: String = "ঠিক আছে", negativeButtonText: String = "ক্যানসেল", listener: ((type: Int) -> Unit)? = null): AlertDialog {

    val builder = MaterialAlertDialogBuilder(requireContext())
    builder.setTitle(title)
    // Display a message on alert dialog
    builder.setMessage(message)
    // Set a positive button and its click listener on alert dialog
    builder.setPositiveButton(positiveButtonText) { dialog, which ->
        dialog.dismiss()
        listener?.invoke(AlertDialog.BUTTON_POSITIVE)
    }
    // Display a negative button on alert dialog
    if (showCancel) {
        builder.setNegativeButton(negativeButtonText) { dialog, which ->
            dialog.dismiss()
            listener?.invoke(AlertDialog.BUTTON_NEGATIVE)
        }
    }

    val dialog = builder.create()
    val typeface = ResourcesCompat.getFont(requireContext(), R.font.solaiman)
    val textView = dialog.findViewById<TextView>(android.R.id.message)
    textView?.typeface = typeface
    return dialog
}

fun Activity.alert(title: CharSequence? = null, message: CharSequence? = null, showCancel: Boolean = false, positiveButtonText: String = "ঠিক আছে", negativeButtonText: String = "ক্যানসেল", listener: ((type: Int) -> Unit)? = null): AlertDialog {

    val builder = MaterialAlertDialogBuilder(this)
    builder.setTitle(title)
    // Display a message on alert dialog
    builder.setMessage(message)
    // Set a positive button and its click listener on alert dialog
    builder.setPositiveButton(positiveButtonText) { dialog, which ->
        dialog.dismiss()
        listener?.invoke(AlertDialog.BUTTON_POSITIVE)
    }
    // Display a negative button on alert dialog
    if (showCancel) {
        builder.setNegativeButton(negativeButtonText) { dialog, which ->
            dialog.dismiss()
            listener?.invoke(AlertDialog.BUTTON_NEGATIVE)
        }
    }

    val dialog = builder.create()
    val typeface = ResourcesCompat.getFont(this, R.font.solaiman)
    val textView = dialog.findViewById<TextView>(android.R.id.message)
    textView?.typeface = typeface
    return dialog
}


fun Fragment.customAlert(titleText: String, descriptionText: String, noBtnText: String, yesBtnText: String, listener: ((type: Int) -> Unit)? = null) {

    val builder = MaterialAlertDialogBuilder(requireContext())
    val view = layoutInflater.inflate(R.layout.dialog_instant_live, null)
    val title = view.findViewById<TextView>(R.id.title)
    val description = view.findViewById<TextView>(R.id.description)
    val noBtn = view.findViewById<MaterialButton>(R.id.noBtn)
    val yesBtn = view.findViewById<MaterialButton>(R.id.yesBtn)

    title.text = titleText
    description.text = descriptionText
    noBtn.text = noBtnText
    yesBtn.text = yesBtnText

    builder.setView(view)

    val dialog = builder.create()
    dialog.show()

    noBtn.setOnClickListener() {
        dialog.dismiss()
        listener?.invoke(2)
    }
    yesBtn.setOnClickListener() {
        dialog.dismiss()
        listener?.invoke(1)
    }
}


fun Context.toast(msg: String?, time: Int = Toast.LENGTH_SHORT) {
    if (!msg.isNullOrEmpty()) {
        val toast = Toast.makeText(this, msg, time)
        val view: View? = toast.view
        view?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black_100))
        val textView: TextView? = view?.findViewById(android.R.id.message)
        textView?.setTextColor(ContextCompat.getColor(this, R.color.white))
        toast.show()
    }
}

fun View.snackbar(message: String, length: Int = Snackbar.LENGTH_INDEFINITE){
    Snackbar.make(this, message, length).also { snackbar ->
        snackbar.setAction("ঠিক আছে") {
            snackbar.dismiss()
        }
    }.show()
}

fun View.snackbar(message: String, length: Int = Snackbar.LENGTH_INDEFINITE, actionName: String, onClick: ((view: View) -> Unit)? = null): Snackbar {
    return Snackbar.make(this, message, length).also { snackbar ->
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 5
        snackbar.setActionTextColor(Color.YELLOW)
        snackbar.setAction(actionName) {
            onClick?.invoke(it)
            snackbar.dismiss()
        }
    }
}

val <T> T.exhaustive: T
    get() = this

fun Context.dpToPx(value: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, this.resources.displayMetrics).toInt()
}

fun Fragment.callHelplineNumber(number: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    } catch (e: Exception) {
        requireContext().toast("Could not find an activity to place the call")
    }
}

fun Context.isConnectedToNetwork(): Boolean {
    var isConnected = false
    val connectivityManager = this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        connectivityManager?.let {
            val networkCapabilities = it.activeNetwork ?: return false
            val activeNetwork = it.getNetworkCapabilities(networkCapabilities) ?: return false
            isConnected = activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            /*activeNetwork.apply {
                isConnected = when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }*/
        }
    } else {
        isConnected = connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting == true
    }
    return isConnected
}

fun getFileContentType(filePath: String): String? {
    val file = File(filePath)
    val map = MimeTypeMap.getSingleton()
    val ext = MimeTypeMap.getFileExtensionFromUrl(file.name)
    var type = map.getMimeTypeFromExtension(ext)
    if (type == null) type = "*/*"
    return type
}

fun NestedScrollView.smoothScrollTo(view: View) {
    var distance = view.top
    var viewParent = view.parent
    //traverses 10 times
    for (i in 0..9) {
        if ((viewParent as View) === this) break
        distance += (viewParent as View).top
        viewParent = viewParent.getParent()
    }
    smoothScrollTo(0, distance)
}

fun Bundle.bundleToString(): String {
    return this.keySet().joinToString(", ", "{", "}") { key ->
        "$key=${this[key]}"
    }
}

fun isIntRange(text: String): Boolean {
    val match = "[0-9,]*".toRegex()
    return text.matches(match)
}

fun cleanPhoneNumber(number: String?): String {
    if (number == null) return ""
    val modNumber = number.replace("""[\s-]+""".toRegex(),"")
    if (modNumber.contains("+")) {
        return modNumber.substring((modNumber.length-11),modNumber.length)
    }
    return modNumber
}

fun goToSetting(context: Context) {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.packageName)).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    }.also {
        context.startActivity(it)
    }

}

fun isEnglishLetterOnly(text: String): Boolean {
    val match = """[a-zA-Z0-9/?><:;,(){}\[\]\-–_+=!@#%^&*|.'"\r\n ]*""".toRegex() // ${'['}
    return text.matches(match)
}

fun isAlphaNumericPassword(text: String): Boolean {
    //(?!^[0-9]$)(?!^[a-zA-Z]$)^([a-zA-Z0-9]{8,20})$
    return text.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*^#?&]{8,20}$".toRegex())
}

fun isExistSpecialCharacter(text: String): Boolean {
    return text.matches("^[A-Za-z\\d ]+$".toRegex())
}

fun isValidFacebookPage(text: String): Boolean {
    return text.matches("((http|https)://)?(www[.])?facebook.com/.+".toRegex())
}

fun generateNameInitial(name: String?): String {
    if (name.isNullOrEmpty()) return ""
    var initial: String = ""
    val array = name.trim().split(" ")
    array.forEach {
        if (it.isNotEmpty()) {
            initial += it[0]
        }
    }
    return  initial
}

fun Activity.appVersion(): String {
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        pInfo.versionName
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun Fragment.appVersion(): String {
    return try {
        val pInfo: PackageInfo? = this.context?.packageManager?.getPackageInfo(this.context?.packageName ?: "", 0)
        pInfo?.versionName ?: ""
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}