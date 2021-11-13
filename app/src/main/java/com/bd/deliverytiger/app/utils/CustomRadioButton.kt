package com.bd.deliverytiger.app.utils

import android.content.Context
import androidx.appcompat.widget.AppCompatRadioButton
import android.widget.TextView
import com.bd.deliverytiger.app.R
import android.view.LayoutInflater
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View

class CustomRadioButton : AppCompatRadioButton {
    private var view: View? = null
    private var textView: TextView? = null
    private var textView1: TextView? = null
    private var textView2: TextView? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        init(context)
    }

    // setText is a final method in ancestor, so we must take another name.
    fun setTextWith(resId: Int, resId2: Int, resId3: Int) {
        textView!!.setText(resId)
        textView1!!.setText(resId2)
        textView2!!.setText(resId3)
        redrawLayout()
    }

    fun setTextWith(text: CharSequence?, text1: CharSequence?, text2: CharSequence?, flag: Int) {
        if (flag == 1) {
            textView!!.textSize = 12f
            textView!!.setTextColor(resources.getColor(R.color.cod_bg_ten))
        }
        textView!!.text = text
        textView1!!.text = text1
        textView2!!.text = text2
        redrawLayout()
    }

    private fun init(context: Context) {
        view = LayoutInflater.from(context).inflate(R.layout.item_view_custom_radio_btn, null)
        textView = view?.findViewById(R.id.time)
        textView1 = view?.findViewById(R.id.paymentType)
        textView2 = view?.findViewById(R.id.charge)
        redrawLayout()
    }

    private fun redrawLayout() {
        view!!.isDrawingCacheEnabled = true
        view!!.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        view!!.layout(0, 0, view!!.measuredWidth, view!!.measuredHeight)
        view!!.buildDrawingCache(true)
        val bitmap = Bitmap.createBitmap(view!!.drawingCache)
        setCompoundDrawablesWithIntrinsicBounds(BitmapDrawable(resources, bitmap), null, null, null)
        view!!.isDrawingCacheEnabled = false
    }

    private fun dp2px(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}