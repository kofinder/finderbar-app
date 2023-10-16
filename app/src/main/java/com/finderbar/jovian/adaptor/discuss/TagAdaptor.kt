package com.finderbar.jovian.adaptor.discuss

import android.arch.paging.PagedListAdapter
import android.support.design.chip.Chip
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.*
import com.finderbar.jovian.adaptor.diffutils.TagDiffUtilCallBack
import com.finderbar.jovian.databinding.ItemLoadingBinding
import com.finderbar.jovian.databinding.ItemTagBinding
import com.finderbar.jovian.models.Tags
import com.finderbar.jovian.utilities.AppConstants.ITEM
import com.finderbar.jovian.utilities.AppConstants.LOADING
/**
 * Created by FinderBar on 27-Dec-17.
 */
class TagAdaptor(private var onCvItemClick: OnCvItemClick) : PagedListAdapter<Tags, TagAdaptor.ItemViewHolder>(TagDiffUtilCallBack()) {

    private var loadingBefore = false
    private var loadingAfter = false

    override fun getItemViewType(position: Int): Int {
        return if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            LOADING
        } else {
            ITEM
        }
    }

    abstract class ItemViewHolder(root: View) : RecyclerView.ViewHolder(root)
    private class TagViewHolder(val binding: ItemTagBinding) : ItemViewHolder(binding.root)
    private class LoadingViewHolder(val binding: ItemLoadingBinding) : ItemViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> {
                val binding = ItemTagBinding.inflate(inflater, parent, false)
                return TagViewHolder(binding)
            }
            LOADING -> {
                val binding = ItemLoadingBinding.inflate(inflater, parent, false)
                return LoadingViewHolder(binding)
            }
        }

        throw IllegalArgumentException("unknown viewType: ${viewType}")
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val datum = super.getItem(position)
        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return
        }

        (holder as? TagViewHolder)?.let { viewHolder ->
            holder.itemView.setOnClickListener{onCvItemClick.onItemClick(datum!!.tagName)}
            viewHolder.binding.tags = datum
            viewHolder.binding.chipGroup.removeAllViews()
            var chip = Chip(viewHolder.binding.chipGroup.context)
            chip.setChipBackgroundColorResource(R.color.pf_white)
            chip.setChipStrokeColorResource(R.color.pf_green)
            chip.isCheckable = false;
            chip.chipStrokeWidth = 1f
            chip.chipStartPadding = 20.0f;
            chip.chipEndPadding = 20.0f
            chip.textEndPadding = 0.0f
            chip.textStartPadding = 0.0f
            chip.chipMinHeight = 30.0f
            chip.setTextAppearanceResource(R.style.ChipTextStyle)
            chip.setPadding(5, 10, 5, 10)
            chip.text = datum!!.tagName
            chip.isCheckable = true
            viewHolder.binding.chipGroup!!.addView(chip);
            viewHolder.binding.chipGroup.isSingleSelection = true
        }
    }


    override fun getItem(position: Int): Tags? {
        if ((loadingBefore && position == 0) || (loadingAfter && position == itemCount - 1)) {
            return null
        }
        if (loadingBefore) {
            return super.getItem(position - 1)
        } else {
            return super.getItem(position)
        }
    }

    private val discussItemCount: Int get() = super.getItemCount()

    override fun getItemCount(): Int {
        if (loadingBefore && loadingAfter) return discussItemCount + 2
        if (loadingBefore || loadingAfter) return discussItemCount + 1
        return discussItemCount
    }

    fun updateLoadingBefore(loading: Boolean) {
        val previousLoading = loadingBefore
        loadingBefore = loading
        if (loading) {
            if (previousLoading != loading) {
                notifyItemInserted(0)
            } else {
                notifyItemChanged(0)
            }
        } else {
            if (previousLoading != loading) {
                notifyItemRemoved(0)
            }
        }
    }

    fun updateLoadingAfter(loading: Boolean) {
        val previousLoading = loadingAfter
        loadingAfter = loading
        if (loading) {
            if (previousLoading != loading) {
                notifyItemInserted(itemCount - 1)
            } else {
                notifyItemChanged(itemCount - 1)
            }
        } else {
            if (previousLoading != loading) {
                notifyItemRemoved(itemCount)
            }
        }
    }
}
