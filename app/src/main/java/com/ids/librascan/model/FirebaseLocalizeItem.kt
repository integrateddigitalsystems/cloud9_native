package com.ids.librascan.model

import com.google.gson.annotations.SerializedName
import com.ids.librascan.controller.MyApplication
import utils.AppConstants


class FirebaseLocalizeItem {

    @SerializedName("message_en")
    var message_en: String?=""
    @SerializedName("message_ar")
    var message_ar: String?=""
    @SerializedName("localize_Key")
    var localize_Key: String?=""

    fun getMessage():String?{
        return if (MyApplication.languageCode == AppConstants.LANG_ENGLISH)
            return message_en
        else
            message_ar
    }

}