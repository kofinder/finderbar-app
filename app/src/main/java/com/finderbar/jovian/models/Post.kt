package com.finderbar.jovian.models

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class Post(
        val _id: String = "",
        val userId: String = "",
        val title: String = "",
        val body: String = "",
        val postImages: List<PostImage> = ArrayList(),
        val movies: List<Movie> = ArrayList(),
        val tags: List<String> = ArrayList(),
        val likeCount: Int = 0,
        val commentCount: Int = 0,
        val viewCount: Int = 0,
        val userAvatar: String = "",
        val userName: String = "",
        val createdAt: String = "",
        val postType: Int = 0
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(PostImage),
            parcel.createTypedArrayList(Movie),
            parcel.createStringArrayList(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(_id)
        dest.writeString(userId)
        dest.writeString(title)
        dest.writeString(body)
        dest.writeArray(postImages.toTypedArray())
        dest.writeArray(movies.toTypedArray())
        dest.writeArray(tags.toTypedArray())
        dest.writeInt(likeCount)
        dest.writeInt(commentCount)
        dest.writeInt(viewCount)
        dest.writeString(userAvatar)
        dest.writeString(userName)
        dest.writeString(createdAt)
        dest.writeInt(postType)
    }

    override fun describeContents(): Int =  0

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}


data class PostSearchResult(
    val posts: List<Post>,
    val hasNext: Boolean,
    val totalCount: Long
)

data class PostSearchQuery(
    val keywords: String = "",
    val excludedKeywords: String = ""
)
