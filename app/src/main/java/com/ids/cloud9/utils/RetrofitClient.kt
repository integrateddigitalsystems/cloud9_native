package com.ids.cloud9.utils

import com.google.gson.GsonBuilder
import com.ids.cloud9.controller.MyApplication
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient{
    // var BASE_URL ="http://demo.ids.com.lb/inPoint/api/"
    var BASE_URL ="https://dev.qasemti.com/wp-json/api/v1/"




    private var retrofit: Retrofit? = null
    val client: Retrofit?
        get() {

            val gson = GsonBuilder()
                .setLenient()
                .create()

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(MyApplication.BASE_URL)
                    .client(requestHeader)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit
        }

    private/*   HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);*//* .addInterceptor(interceptor)*/ val requestHeader: OkHttpClient
        get() = OkHttpClient.Builder()
            // .addNetworkInterceptor()
            .connectTimeout(5, TimeUnit.MINUTES) // connect timeout
            .writeTimeout(5, TimeUnit.MINUTES) // write timeout
            .readTimeout(5, TimeUnit.MINUTES)
            .build()
    private fun cancelRequest() {}
}