package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class Bundle(
    @SerializedName("mMap")
    val mMap: MMap
)