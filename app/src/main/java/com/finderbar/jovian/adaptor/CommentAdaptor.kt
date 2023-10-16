package com.finderbar.jovian.adaptor
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finderbar.jovian.OnCvItemClick
import com.finderbar.jovian.agoTimeUtil
import com.finderbar.jovian.models.Comment
import com.finderbar.jovian.databinding.ItemCommentBinding
import com.finderbar.jovian.databinding.ItemLoadingBinding
import com.finderbar.jovian.utilities.AppConstants.ITEM
import com.finderbar.jovian.utilities.AppConstants.LOADING
import com.finderbar.jovian.utilities.android.loadAvatar
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_comment.view.user_image


class CommentAdaptor(private val onItemClick: OnCvItemClick) : RecyclerView.Adapter<CommentAdaptor.ItemViewHolder>() {

    private var isLoadingAdded = false
    var hasNext: Boolean = false;

    abstract class ItemViewHolder(root: View) : RecyclerView.ViewHolder(root)
    private class CommentViewHolder(binding: ItemCommentBinding) : ItemViewHolder(binding.root)
    private class LoadingViewHolder(binding: ItemLoadingBinding) : ItemViewHolder(binding.root)
    private val arrayList: ArrayList<Comment> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> {
                val binding = ItemCommentBinding.inflate(inflater, parent, false)
                return CommentViewHolder(binding)
            }
            LOADING -> {
                val binding = ItemLoadingBinding.inflate(inflater, parent, false)
                return LoadingViewHolder(binding)
            }
        }

        throw IllegalArgumentException("unknown viewType: $viewType") as Throwable
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val datum = getItem(position);
                (holder as? CommentViewHolder)?.let { v ->
                    v.itemView.user_image.loadAvatar(Uri.parse(datum.userAvatar))
                    v.itemView.txt_usr_name.text = datum.userName
                    v.itemView.txt_body.text = datum.body
                    v.itemView.txt_ago.text = agoTimeUtil(datum.createdAt)
                }
            }
            LOADING -> {
                holder as LoadingViewHolder
            }
        }
    }

    override fun getItemCount(): Int = arrayList?.size

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ITEM
        } else {
            if (position == arrayList.size - 1 && isLoadingAdded) LOADING else ITEM
        }
    }

    fun getItem(position: Int): Comment = arrayList[position]

    fun add(r: Comment) {
        arrayList.add(r)
        notifyItemInserted(arrayList.size - 1)
    }

    fun remove(r: Comment) {
        val position = arrayList.indexOf(r)
        if (position > -1) {
            arrayList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addAll(result: List<Comment>) {
        for (result in result) {
            add(result)
        }
    }

    fun isEmpty(): Boolean {
        return itemCount == 0
    }

}
