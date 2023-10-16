package com.finderbar.jovian.utilities

import android.content.Context
import android.support.v7.widget.LinearSmoothScroller
import android.util.DisplayMetrics

class SnapTopLinearSmoothScroller(private val context: Context):  LinearSmoothScroller(context)  {
    override fun getVerticalSnapPreference(): Int {
        return LinearSmoothScroller.SNAP_TO_START
    }

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        return super.calculateSpeedPerPixel(displayMetrics) * 5f
    }
}