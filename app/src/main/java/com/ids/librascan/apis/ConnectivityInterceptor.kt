package com.ids.librascan.apis

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectivityInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!WifiService.instance.isOnline()) {
            throw NoConnectivityException()
        } else {
            return chain.proceed(chain.request())
        }
    }
}

class NoConnectivityException : IOException() {
    override val message: String
        get() = "No network available, please check your WiFi or Data connection"
}