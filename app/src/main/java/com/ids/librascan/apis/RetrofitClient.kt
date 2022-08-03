package com.ids.librascan.apis

import com.google.gson.GsonBuilder
import com.ids.librascan.controller.MyApplication
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
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

    private val requestHeader: OkHttpClient
        get() = OkHttpClient.Builder()
            .addInterceptor{ it.proceed(it.request().newBuilder().addHeader("Client-Key", MyApplication.clientKey).build())}
            .connectTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .addInterceptor(ConnectivityInterceptor())
            .build()
}


