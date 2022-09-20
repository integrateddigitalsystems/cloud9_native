package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.utils.AppConstants

data class LocalisedMessage(
    @SerializedName("key")
    val key: String,
    @SerializedName("valueAr")
    val valueAr: String,
    @SerializedName("valueEn")
    val valueEn: String
){
    fun getMessage():String? {
        return if (MyApplication.languageCode == AppConstants.LANG_ENGLISH)
            return valueEn
        else
            return valueAr
    }
}