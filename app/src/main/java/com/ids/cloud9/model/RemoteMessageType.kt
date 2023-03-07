package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class RemoteMessageType(
    @SerializedName("bundle")
    val bundle: Bundle,
    @SerializedName("data")
    val `data`: Data
)