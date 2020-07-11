package com.bd.deliverytiger.app.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton


class HideBottomViewOnScrollBehavior(context: Context, attributeSet: AttributeSet) : CoordinatorLayout.Behavior<View>(context, attributeSet) {


    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        //Timber.d("onNestedScroll dyConsumed: $dyConsumed dyUnconsumed: $dyUnconsumed")
        if (dyConsumed > 30) {
            //slideDown(child);
            if (child is ExtendedFloatingActionButton) {
                child.shrink()
            }
        } else if (dyConsumed < -30) {
            //slideUp(child);
            if (child is ExtendedFloatingActionButton) {
                child.extend()
            }
        }
    }

    private fun slideUp(child: View) {

    }

}