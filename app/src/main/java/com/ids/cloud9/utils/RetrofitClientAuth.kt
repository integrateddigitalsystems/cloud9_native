package com.ids.cloud9.utils

import android.util.Log
import com.google.gson.GsonBuilder
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.MyApplication.Companion.BASE_URL
import com.ids.cloud9.controller.MyApplication.Companion.token
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClientAuth {
    // var BASE_URL ="http://www.idsdemo.me/Trax/API/Api/"
    private var retrofit: Retrofit? = null

    val requestHeader: OkHttpClient
        get() = OkHttpClient.Builder()
            .addInterceptor{ it.proceed(it.request().newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .build())}
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES) // write timeout
            .connectTimeout(5, TimeUnit.MINUTES)
            .build()
    val client: Retrofit?
        get() {

            val gson = GsonBuilder()
                .setLenient()
                .create()

            /*if (retrofit == null) {*/
            Log.wtf("the_Used_URL", BASE_URL)
            var x = MyApplication.BASE_URL
            retrofit = Retrofit.Builder()
                .baseUrl(MyApplication.BASE_URL)
                .client(requestHeader)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            /*  }*/
            return retrofit
        }



    private fun cancelRequest() {}


}