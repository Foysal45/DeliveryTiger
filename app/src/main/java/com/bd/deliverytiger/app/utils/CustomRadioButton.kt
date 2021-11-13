package com.bd.deliverytiger.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.bd.deliverytiger.app.R;

public class CustomRadioButton extends AppCompatRadioButton {

    private View view;
    private TextView textView;
    private TextView textView1;
    private TextView textView2;

    public CustomRadioButton(Context context) {
        super(context);
        init(context);
    }

    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /*private RequestListener<Bitmap> requestListener = new RequestListener<Bitmap>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
            imageView.setImageBitmap(resource);
            redrawLayout();
            return false;
        }
    };

    public void setImageResource(int resId) {
        Glide.with(getContext())
                .asBitmap()
                .load(resId)
                .apply(RequestOptions.bitmapTransform(
                        new MultiTransformation<>(
                                new CenterCrop(),
                                new RoundedCornersTransformation(dp2px(getContext(), 24), 0, RoundedCornersTransformation.CornerType.ALL))
                        )
                )
                .listener(requestListener)
                .submit();
    }

    public void setImageBitmap(Bitmap bitmap) {
        Glide.with(getContext())
                .asBitmap()
                .load(bitmap)
                .apply(RequestOptions.bitmapTransform(
                        new MultiTransformation<>(
                                new CenterCrop(),
                                new RoundedCornersTransformation(dp2px(getContext(), 24), 0, RoundedCornersTransformation.CornerType.ALL))
                        )
                )
                .listener(requestListener)
                .submit();
    }*/

    // setText is a final method in ancestor, so we must take another name.
    public void setTextWith(int resId, int resId2, int resId3) {
        textView.setText(resId);
        textView1.setText(resId2);
        textView2.setText(resId3);
        redrawLayout();
    }

    @SuppressLint("ResourceAsColor")
    public void setTextWith(CharSequence text, CharSequence text1, CharSequence text2, int flag) {
        if (flag==1){
            textView.setTextSize(12);
            textView.setTextColor(R.color.cod_bg_ten);
        }
        textView.setText(text);
        textView1.setText(text1);
        textView2.setText(text2);
        redrawLayout();
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.item_view_custom_radio_btn, null);
        textView = view.findViewById(R.id.time);
        textView1 = view.findViewById(R.id.paymentType);
        textView2 = view.findViewById(R.id.charge);
        redrawLayout();
    }

    private void redrawLayout() {
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(getResources(), bitmap), null, null, null);
        view.setDrawingCacheEnabled(false);
    }

    private int dp2px(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

}
