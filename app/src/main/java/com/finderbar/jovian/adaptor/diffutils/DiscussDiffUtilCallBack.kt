package com.finderbar.jovian.adaptor.diffutils

import android.support.v7.util.DiffUtil
import com.finderbar.jovian.models.Discuss

class DiscussDiffUtilCallBack : DiffUtil.ItemCallback<Discuss>() {
    override fun areItemsTheSame(oldItem: Discuss, newItem: Discuss): Boolean {
        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(oldItem: Discuss, newItem: Discuss): Boolean {
        return oldItem == newItem
    }
}