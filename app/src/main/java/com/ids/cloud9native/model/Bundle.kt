package com.ids.cloud9native.model


import com.google.gson.annotations.SerializedName

data class Bundle(
    @SerializedName("mMap")
    val mMap: MMap
)