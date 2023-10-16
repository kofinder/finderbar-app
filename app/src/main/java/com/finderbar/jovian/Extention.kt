package com.finderbar.jovian

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloCallback
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.danimahardhika.cafebar.CafeBar
import com.danimahardhika.cafebar.CafeBarGravity
import com.danimahardhika.cafebar.CafeBarTheme
import com.finderbar.jovian.adaptor.discuss.DiscussAdaptor
import com.finderbar.jovian.viewholder.MovieViewHolder
import com.finderbar.jovian.models.*
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import im.ene.toro.ToroPlayer
import im.ene.toro.media.PlaybackInfo
import type.VoteStatus
import java.text.SimpleDateFormat
import java.util.*


fun ViewGroup.inflate(@LayoutRes  layoutRes: Int,attachToRoot : Boolean = false) : View {
    return LayoutInflater.from(context).inflate(layoutRes,this,attachToRoot)
}

fun Context.toast(message: String?, length: Int=Toast.LENGTH_SHORT){
    Toast.makeText(this,message,length).show()
}

fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(adapterPosition)
    }
    return this
}


fun agoTimeUtil(dateStr: String): String {
    var str: String = ""
    try {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        formatter.timeZone = TimeZone.getTimeZone("GMT");
        var result = formatter.parse(dateStr);
        val calendar = Calendar.getInstance()
        calendar.time = result
        val lang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Locale.forLanguageTag("en")
        } else {
            Locale.ENGLISH
        }
        val messages = TimeAgoMessages.Builder().withLocale(lang).build()

        str = TimeAgo.using(calendar.timeInMillis, messages)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return str
}


fun <T> ApolloCall<T>.enqueue(
        onSuccess: (response: Response<T>) -> Unit,
        onFailure: (ex: ApolloException) -> Unit = { it.printStackTrace() }) {

    this.enqueue(ApolloCallback<T>(object : ApolloCall.Callback<T>() {

        override fun onFailure(e: ApolloException) = onFailure(e)

        override fun onResponse(response: Response<T>) = onSuccess(response)

    }, Handler(Looper.getMainLooper())))

}

fun showSnack( context: Context, icon: Drawable, message: String) {
    CafeBar.Builder(context)
            .theme(CafeBarTheme.DARK)
            .icon(icon)
            .duration(CafeBar.Duration.MEDIUM)
            .content(message)
            .fitSystemWindow()
            .gravity(CafeBarGravity.END)
            .show()
}


interface OnCvItemClick {
    fun onItemClick(_id: String)
}

interface OnItemNotifyClick {
    fun onItemClick(notification: Notification)
}

interface ISearch {
    fun onTextQuery(text: String)
}

interface DiscussVoteCallback {
    fun onItemClick(viewHolder: DiscussAdaptor.ItemViewHolder, _id: String, voteStatus: VoteStatus, voteType: DiscussType)
}

interface DiscussDialogListener {
    fun setData(_id: String, type: DiscussType)
}

interface DiscussFavoriteCallback {
    fun onItemClick(viewHolder: DiscussAdaptor.ItemViewHolder, _id: String, voteType: DiscussType)
}

interface DiscussEditCallback {
    fun onItemClick(discuss: Discuss, voteType: DiscussType)
}

interface IFragmentListener {
    fun addiSearch(iSearch: ISearch)
    fun removeISearch(iSearch: ISearch)
}

interface OnEntityItemClick {
    fun onItemClick(entity: Category)
}

interface ItemMovieClick {
    fun onItemClick(view: View, position: Int, movie: Movie)
}

interface ItemUserClick {
    fun onItemClick(userId: String, userName: String, avatar: String)
}

interface DiscussCommentCallBack {
    fun onItemClick(view: View, _id: String, type: DiscussType)
}

interface ItemQuestionCallBack {
    fun onItemClick(view: View, item: Question, position: Int)
}

interface ItemMovieCallback {
    fun onItemClick(viewHolder: MovieViewHolder, view: View, item: Movie, position: Int)
}

interface ItemPostCallBack {
    fun onItemClick(view: View, position: Int, item: Post)
}

interface ItemTimelineCallBack {
    fun onItemClick(view: View, position: Int)
}

interface OnCompleteCallback {
    fun onCompleted(player: ToroPlayer)
}

interface MoviePlayerCallback {
    fun onBigPlayerCreated()
    fun onBigPlayerDestroyed(order: Int, baseItem: Movie?, latestInfo: PlaybackInfo?)
}

interface MoviePlayListCallback {
    fun onPlaylistCreated()
    fun onPlaylistDestroyed(basePosition: Int, result: Movie, latestInfo: PlaybackInfo)
}


