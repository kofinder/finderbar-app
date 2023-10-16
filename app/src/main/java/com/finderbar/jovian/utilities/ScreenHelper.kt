package com.finderbar.jovian.utilities

import android.graphics.Point
import android.view.Display

class ScreenHelper private constructor() {
    init {
        throw RuntimeException("Meh!")
    }
    companion object {
        fun shouldUseBigPlayer(display: Display): Boolean {
            val displaySize = Point()
            display.getSize(displaySize)
            return displaySize.x >= displaySize.y
        }
    }
}