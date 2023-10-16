package com.finderbar.jovian.models
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
/**
 * Created by thein on 12/18/18.
 */
@Parcelize
data class BadgeCounts(
    val bronze: Int ? = 10,
    val silver: Int ? = 10,
    val gold: Int ? = 10
) : Parcelable