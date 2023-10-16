package com.finderbar.jovian.models

import android.os.Parcel
import android.os.Parcelable

data class PostText(
        val _id: String = "",
        val userId: String = "",
        val title: String = "",
        val body: String = "",
        val likeCount: Int = 0,
        val commentCount: Int = 0,
        val viewCount: Int = 0,
        val userAvatar: String = "",
        val userName: String = "",
        val createdAt: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(_id)
        dest.writeString(userId)
        dest.writeString(title)
        dest.writeString(body)
        dest.writeInt(likeCount)
        dest.writeInt(commentCount)
        dest.writeInt(viewCount)
        dest.writeString(userAvatar)
        dest.writeString(userName)
        dest.writeString(createdAt)
    }

    override fun describeContents(): Int =  0

    companion object CREATOR : Parcelable.Creator<PostText> {
        override fun createFromParcel(parcel: Parcel): PostText {
            return PostText(parcel)
        }

        override fun newArray(size: Int): Array<PostText?> {
            return arrayOfNulls(size)
        }
    }
}