package com.finderbar.jovian.utilities.android

import android.support.v4.widget.NestedScrollView

fun NestedScrollView.onScrolledToBottom(block: (NestedScrollView) -> Unit) {
    viewTreeObserver.addOnScrollChangedListener {
        if (getChildAt(0).bottom <= (height + scrollY)) {
            block(this)
        }
    }
}
