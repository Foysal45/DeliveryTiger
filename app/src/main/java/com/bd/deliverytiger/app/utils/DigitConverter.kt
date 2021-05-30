package com.bd.deliverytiger.app.utils

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

object DigitConverter {

    val banglaMonth = arrayOf("জানুয়ারী","ফেব্রুয়ারী","মার্চ","এপ্রিল","মে","জুন","জুলাই","আগস্ট","সেপ্টেম্বর","অক্টোবর","নভেম্বর","ডিসেম্বর")
    private var decimalFormat: DecimalFormat? = null

    init {
        decimalFormat = DecimalFormat("#,##,##0")
        //decimalFormat?.isGroupingUsed = true
        //decimalFormat?.groupingSize = 3
    }

    fun toBanglaDigit(digits: Any?, isComma: Boolean = false): String {

        if (digits is String) {
            return engCahrReplacer(digits)
        } else if (digits is Int) {
            return if (isComma) {
                engCahrReplacer(formatNumber(digits))
            } else {
                engCahrReplacer(digits.toString())
            }
        } else if (digits is Double)
            return if (isComma) {
                engCahrReplacer(formatNumber(digits))
            } else {
                engCahrReplacer(digits.toString())
            }
        else {
            return (digits as? String).toString()
        }
    }

    fun toBanglaDate(banglaDate: String?, pattern: String = "yyyy-MM-dd HH:mm:ss", disableYear: Boolean = false): String {

        if (banglaDate == null) {
            return ""
        }
        val dateFormatter = SimpleDateFormat(pattern, Locale.US)
        try {
            val date = dateFormatter.parse(banglaDate)
            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                //val hour = calendar.get(Calendar.HOUR_OF_DAY)
                //val minute = calendar.get(Calendar.MINUTE)
                //val second = calendar.get(Calendar.SECOND)

                return if (disableYear) {
                    engCahrReplacer(day.toString()) + " " + banglaMonth[month]
                } else {
                    engCahrReplacer(day.toString()) + " " + banglaMonth[month] + ", " + engCahrReplacer(year.toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return banglaDate
    }

    private fun engCahrReplacer(string: String): String {
        return string.replace('0', '০')
            .replace('1', '১')
            .replace('2', '২')
            .replace('3', '৩')
            .replace('4', '৪')
            .replace('5', '৫')
            .replace('6', '৬')
            .replace('7', '৭')
            .replace('8', '৮')
            .replace('9', '৯')
    }

    private fun formatNumber(digits: Any): String {

        return decimalFormat?.format(digits) ?: digits.toString()
    }

    fun formatDate(inputDate: String?, patternInput: String, patternOutput: String): String {
        if (inputDate == null) return ""
        try {
            val sdf1 = SimpleDateFormat(patternInput, Locale.US)
            val sdf2 = SimpleDateFormat(patternOutput, Locale.US)
            val date = sdf1.parse(inputDate)
            if (date != null) {
                return sdf2.format(date)
            }
            return inputDate
        } catch (e: Exception) {
            e.printStackTrace()
            return inputDate
        }
    }

    fun relativeWeekday(dateTime: Date): String {
        try {
            val sdf = SimpleDateFormat("hh: mm a", Locale.US)
            val calendar = Calendar.getInstance()
            val toDate = calendar.get(Calendar.DAY_OF_YEAR)
            calendar.time = dateTime
            val selectedDate = calendar.get(Calendar.DAY_OF_YEAR)
            when (selectedDate) {
                toDate -> {
                    return "আজ @ ${toBanglaDigit(sdf.format(dateTime))}"
                }
                toDate + 1 -> {
                    return "আগামীকাল @ ${toBanglaDigit(sdf.format(dateTime))}"
                }
                toDate - 1 -> {
                    return "গতকাল @ ${toBanglaDigit(sdf.format(dateTime))}"
                }
                else -> {
                    val local = Locale("bn", "BD")
                    val sdf1 = SimpleDateFormat("EEEE", local)
                    return "${sdf1.format(dateTime)} @ ${toBanglaDigit(sdf.format(dateTime))}"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    private val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.ENGLISH)).apply {
        roundingMode = RoundingMode.HALF_UP
    }

    fun formatDecimal(value: Float): String {
        return try {
            df.format(value)
        } catch (e: Exception) {
            e.printStackTrace()
            value.toString()
        }
    }

}