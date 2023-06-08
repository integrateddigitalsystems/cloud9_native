package com.ids.cloud9.model

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.ids.cloud9.utils.wtf
import java.io.UnsupportedEncodingException

object JWTDecoding {
    @Throws(Exception::class)
    fun decoded(JWTEncoded: String):JWTResponse {
        try {
            if(!JWTEncoded.isNullOrEmpty()){
            val split = JWTEncoded.split("\\.".toRegex()).toTypedArray()
            if(split.size > 0 ) {
                wtf("Header: " + getJson(split[0]))
                wtf("Body: " + getJson(split[1]))
                var str = Gson().fromJson(getJson(split[1]), JWTResponse::class.java)
                Log.wtf("TAG_JAD_USER",Gson().toJson(str))
                return Gson().fromJson(getJson(split[1]), JWTResponse::class.java)
            }else {
                return  JWTResponse()
            }
            }else{
                return  JWTResponse()
            }
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