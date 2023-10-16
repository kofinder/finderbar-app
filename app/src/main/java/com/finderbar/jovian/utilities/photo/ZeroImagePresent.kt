package com.finderbar.jovian.utilities.photo

import com.finderbar.jovian.models.ImageItem

class ZeroImagePresent : ImagePresent {
    override val maxDisplayItem: Int
        get() = 1
    override val item: List<ImageItem>
        get() = listOf()
    override val requestColumns: Int
        get() = 1
}
