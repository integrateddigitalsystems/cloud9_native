package com.ids.cloud9native.model


import com.google.gson.annotations.SerializedName

data class MobileConfigTypes(
    @SerializedName("android")
    val android: ArrayList<MobileConfig> ?= arrayListOf()
)