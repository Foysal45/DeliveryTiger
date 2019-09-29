package com.bd.deliverytiger.app.ui.filter

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

class DatePickerDialogCustom: DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var mDate = ""
    private var theMonth = 0
    private var mFlag = 0

    companion object {
        fun newInstance(flag: Int, mDate: String): DatePickerDialogCustom = DatePickerDialogCustom().apply {
            this.mFlag = flag
            this.mDate = mDate
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val yy = calendar.get(Calendar.YEAR)
        val mm = calendar.get(Calendar.MONTH)
        val dd = calendar.get(Calendar.DAY_OF_MONTH)
        val da = DatePickerDialog(activity!!, this, yy, mm, dd)

        if (mDate.isNotEmpty() && mFlag == 2) {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = df.parse(mDate)
            da.datePicker.minDate = date.getTime()
        }

        if (mDate.isNotEmpty() && mFlag == 1) {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = df.parse(mDate)
            da.datePicker.maxDate = date.getTime()
        }

        return da
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        theMonth = month + 1
        dateFormat = "$year-$theMonth-$dayOfMonth"
        passDateInterface2.gotDate2(dateFormat!!, mFlag)
    }


    private var dateFormat: String? = null
    lateinit var passDateInterface2: PassDateInterface2
    internal fun setDate(passDateInterface2: PassDateInterface2) {
        this.passDateInterface2 = passDateInterface2
    }

    interface PassDateInterface2 {
        fun gotDate2(date: String, flag: Int)
    }
}