package com.app.kiyalearning.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RestManager {

    private var retrofit: Retrofit? = null

    var gson = GsonBuilder()
        .setLenient()
        .create()!!

    fun getInstance(): Api {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(WebConstant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit?.create(Api::class.java)!!
    }

}