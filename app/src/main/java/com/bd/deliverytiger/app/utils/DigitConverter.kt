package com.bd.deliverytiger.app.utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object DigitConverter {

    private val banglaMonth = arrayOf("জানুয়ারী","ফেব্রুয়ারী","মার্চ","এপ্রিল","মে","জুন","জুলাই","আগস্ট","সেপ্টেম্বর","অক্টোবর","নভেম্বর","ডিসেম্বর")

    fun toBanglaDigit(digits: Any?, isComma: Boolean = false): String {

        if (digits is String) {
            return engCahrReplacer(digits)
        } else if (digits is Int) {
            return if (isComma) {
                engCahrReplacer(formatNumber(digits.toLong()))
            } else {
                engCahrReplacer(digits.toString())
            }
        } else if (digits is Double)
            return if (isComma) {
                engCahrReplacer(formatNumber(digits.toLong()))
            } else {
                engCahrReplacer(digits.toString())
            }
        else {
            return (digits as? String).toString()
        }
    }

    fun toBanglaDate(banglaDate: String?, pattern: String = "yyyy-MM-dd HH:mm:ss"): String {

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

                return engCahrReplacer(day.toString()) + " " + banglaMonth[month] + ", " + engCahrReplacer(year.toString())
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

    private fun formatNumber(digits: Long): String {
        val decimalFormat = DecimalFormat()
        decimalFormat.isGroupingUsed = true
        decimalFormat.groupingSize = 3
        return decimalFormat.format(digits)
    }

}