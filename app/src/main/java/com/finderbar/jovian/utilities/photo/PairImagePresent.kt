package com.finderbar.jovian.utilities.photo

import com.finderbar.jovian.models.ImageItem

internal class PairImagePresent(urls: List<String>) : ImagePresent {

    private var _items = listOf<ImageItem>()

    init {
        require(urls.size == 2)
        _items = urls.map { ImageItem(it, 2, 3) }
    }

    override val item: List<ImageItem>
        get() = _items
    override val requestColumns: Int
        get() = 4
    override val maxDisplayItem: Int
        get() = 2
}
