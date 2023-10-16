
package com.finderbar.jovian.utilities

import android.view.View
import com.finderbar.jovian.utilities.android.onClick
import com.finderbar.jovian.utilities.android.onLongClick

typealias OnItemClick<T> = View.(T) -> Unit
class AdapterClickListener<T> {
    var onItemClick: OnItemClick<T>? = null

    fun onItemClick(block: OnItemClick<T>) {
        onItemClick = block
    }

    internal fun bindOnItemClick(itemView: View, data: T) {
        onItemClick?.let { itemView.onClick { _ -> it.invoke(itemView, data) } }
    }

}
