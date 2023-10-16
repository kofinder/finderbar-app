package com.finderbar.jovian.models

import android.os.Parcel
import android.os.Parcelable

data class Movie(
    var id: String = "",
    var body: String = "",
    val videoUrl: String? = "",
    val coverUrl: String? = "",
    val likeCount: Long = 0,
    val commentCount: Long = 0,
    val viewCount: Long = 0,
    val userId: String = "",
    val userAvatar: String = "",
    val userName: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(body)
        parcel.writeString(videoUrl)
        parcel.writeString(coverUrl)
        parcel.writeLong(likeCount)
        parcel.writeLong(commentCount)
        parcel.writeLong(viewCount)
        parcel.writeString(userId)
        parcel.writeString(userAvatar)
        parcel.writeString(userName)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }
    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }
        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }
}

data class MovieSearchResult(
    val movies: List<Movie>,
    val hasNext: Boolean,
    val totalCount: Long
)

data class MovieSearchQuery(
    val keywords: String = "",
    val excludedKeywords: String = ""
)