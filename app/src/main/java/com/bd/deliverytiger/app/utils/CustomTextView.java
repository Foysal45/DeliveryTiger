package com.bd.deliverytiger.app.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {


    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public void init() {

        int typefaceStyle = Typeface.NORMAL;
        Typeface typeface = getTypeface();
        if (typeface != null){
            typefaceStyle = typeface.getStyle();
        }

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/SolaimanLipi.ttf");

        setTypeface(tf,typefaceStyle);

    }


}
