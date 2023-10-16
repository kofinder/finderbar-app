package com.finderbar.jovian.utilities.photo

import com.finderbar.jovian.models.ImageItem

internal class SingleImagePresent(val urls: List<String>) : ImagePresent {

    private val itemImage = ImageItem(urls[0])

    override val item: List<ImageItem>
        get() = listOf(itemImage)
    override val requestColumns: Int
        get() = 1
    override val maxDisplayItem: Int
        get() = 1
}
