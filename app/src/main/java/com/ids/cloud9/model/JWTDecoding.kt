package com.ids.cloud9.model

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import java.io.UnsupportedEncodingException

object JWTDecoding {
    @Throws(Exception::class)
    fun decoded(JWTEncoded: String):JWTResponse {
        try {
            val split = JWTEncoded.split("\\.".toRegex()).toTypedArray()
            Log.d("JWT_DECODED", "Header: " + getJson(split[0]))
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]))

            return Gson().fromJson(getJson(split[1]),JWTResponse::class.java)
        } catch (e: UnsupportedEncodingException) {
            return JWTResponse()
        }
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String {
        val decodedBytes: ByteArray = Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes)
    }
}