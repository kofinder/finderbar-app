package com.finderbar.jovian.utilities.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by sanyatihan on 27-Dec-17.
 */
class RetrofitClient {

    companion object {
        private var retrofit : Retrofit? = null
        fun getClient(baseUrl : String) : Retrofit?{
            if(retrofit ==null){
                retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(baseUrl).build()
            }
            return retrofit
        }
    }

}