package com.finderbar.jovian.adaptor.diffutils

import android.support.v7.util.DiffUtil
import com.finderbar.jovian.models.Category

class CategoryDiffUtilCallBack : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.categoryId == newItem.categoryId
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}