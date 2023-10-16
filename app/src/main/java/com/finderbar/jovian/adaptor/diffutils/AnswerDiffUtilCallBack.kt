package com.finderbar.jovian.adaptor.diffutils

import android.support.v7.util.DiffUtil
import com.finderbar.jovian.models.Answer

/**
 * Created by thein on 1/9/19.
 */
class AnswerDiffUtilCallBack : DiffUtil.ItemCallback<Answer>() {
    override fun areItemsTheSame(oldItem: Answer, newItem: Answer): Boolean {
        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(oldItem: Answer, newItem: Answer): Boolean {
        return oldItem == newItem
    }
}