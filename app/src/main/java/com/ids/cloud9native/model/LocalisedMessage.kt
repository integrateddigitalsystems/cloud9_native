package com.ids.cloud9native.model


import com.google.gson.annotations.SerializedName
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.utils.AppConstants

data class LocalisedMessage(
    @SerializedName("key")
    val key: String?="",
    @SerializedName("valueAr")
    val valueAr: String?="",
    @SerializedName("valueEn")
    val valueEn: String?=""
){
    fun getMessage():String? {
        return if (MyApplication.languageCode == AppConstants.LANG_ENGLISH)
            return valueEn
        else
            return valueAr
    }
}