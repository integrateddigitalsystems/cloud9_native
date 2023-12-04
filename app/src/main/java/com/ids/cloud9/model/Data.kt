package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("notification_foreground")
    val notificationForeground: String?="",
    @SerializedName("visitId")
    val visitId: String?=""
)