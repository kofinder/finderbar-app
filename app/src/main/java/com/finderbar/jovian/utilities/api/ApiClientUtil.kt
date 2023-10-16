package com.finderbar.jovian.utilities.api

object ApiClientUtil {
    private const val BASE_URL = "http://139.59.206.23/";
    fun fileUploader() : ApiServices = RetrofitClient.getClient(BASE_URL)!!.create(ApiServices::class.java)
    fun movieUploader() : ApiServices = RetrofitClient.getClient(BASE_URL)!!.create(ApiServices::class.java)
}