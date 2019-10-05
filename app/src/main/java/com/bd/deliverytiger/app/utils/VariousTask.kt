package com.bd.deliverytiger.app.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Environment
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object VariousTask {

    fun getCurrentDateTime(pattern: String = "yyyy-MM-dd'T'HH:mm:ss"): String? {
        val dateFormat: DateFormat = SimpleDateFormat(pattern, Locale.US)
        val date = Date()
        return dateFormat.format(date)
    }

    fun getPreviousDateTime(month: Int, pattern: String = "yyyy-MM-dd'T'HH:mm:ss"): String? {
        val dateFormat: DateFormat = SimpleDateFormat(pattern, Locale.US)
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

    fun hideSoftKeyBoard(activity: Activity?) {
        try {  // hide keyboard if its open
            val inputMethodManager: InputMethodManager? =
                activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun saveImage(finalBitmap: Bitmap): String {
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/TMPFOLDER")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val fname = "image-$n.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    fun scaledBitmapImage(bitmap: Bitmap): Bitmap {
        val nh = (bitmap.height.times((512.0 / bitmap.width)))
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 512, nh.toInt(), true)
        return scaledBitmap
    }

    fun getCircularImage(context: Context?, bitmap: Bitmap): RoundedBitmapDrawable {
        val circularBitmapDrawable: RoundedBitmapDrawable =
            RoundedBitmapDrawableFactory.create(context!!.resources, bitmap)
        circularBitmapDrawable.isCircular = true
        return circularBitmapDrawable
    }
}