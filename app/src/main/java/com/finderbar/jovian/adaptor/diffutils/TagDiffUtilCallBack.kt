package com.finderbar.jovian.adaptor.diffutils

import android.support.v7.util.DiffUtil
import com.finderbar.jovian.models.Tags

/**
 * Created by thein on 1/12/19.
 */


class TagDiffUtilCallBack : DiffUtil.ItemCallback<Tags>() {
    override fun areItemsTheSame(oldItem: Tags, newItem: Tags): Boolean {
        return oldItem.tagName == newItem.tagName
    }
    override fun areContentsTheSame(oldItem: Tags, newItem: Tags): Boolean {
        return oldItem == newItem
    }
}