package com.finderbar.jovian.utilities.photo

import com.finderbar.jovian.models.ImageItem


internal interface ImagePresent {

    val item: List<ImageItem>

    val requestColumns: Int

    val maxDisplayItem: Int
}

internal fun imageItemPresenterFor(urls: List<String>): ImagePresent = when (urls.size) {
    0 -> ZeroImagePresent()
    1 -> SingleImagePresent(urls)
    2 -> PairImagePresent(urls)
    3 -> TrippleImagePresent(urls)
    else -> FacebookImagePresent(urls)
}
