package com.finderbar.jovian.utilities.api

import com.finderbar.jovian.models.MyFile
import com.finderbar.jovian.models.MyVideo
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Call
import retrofit2.http.HeaderMap

/**
 * Created by finder on 27-Dec-17.
 */
interface ApiServices {

    @Multipart
    @POST("/avatarUpload")
    fun fileUpload(@HeaderMap header: Map<String, String?>, @Part  file: MultipartBody.Part) : Call<MyFile>


    @Multipart
    @POST("/movieUpload")
    fun movieUpload(@HeaderMap header: Map<String, String?>, @Part  file: MultipartBody.Part) : Call<MyVideo>
}