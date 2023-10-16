package com.finderbar.jovian.models

import android.os.Parcel
import android.os.Parcelable

data class PostImage(
    val _id: String = "",
    val userId: String = "",
    val body: String = "",
    val url: String = "",
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val createdAt: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()) {
    }
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(_id)
        dest.writeString(userId)
        dest.writeString(body)
        dest.writeString(url)
        dest.writeInt(likeCount)
        dest.writeInt(commentCount)
        dest.writeString(createdAt)
    }


    override fun describeContents(): Int  = 0

    companion object CREATOR : Parcelable.Creator<PostImage> {
        override fun createFromParcel(parcel: Parcel): PostImage {
            return PostImage(parcel)
        }
        override fun newArray(size: Int): Array<PostImage?> {
            return arrayOfNulls(size)
        }
    }
}