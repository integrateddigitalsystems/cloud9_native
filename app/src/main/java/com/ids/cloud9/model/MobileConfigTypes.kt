package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class MobileConfigTypes(
    @SerializedName("android")
    val android: ArrayList<MobileConfig>
)