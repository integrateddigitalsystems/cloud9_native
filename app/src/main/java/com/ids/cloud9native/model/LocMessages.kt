package com.ids.cloud9native.model


import com.google.gson.annotations.SerializedName

data class LocMessages(
    @SerializedName("values")
    val values: ArrayList<LocalisedMessage> = arrayListOf()
)