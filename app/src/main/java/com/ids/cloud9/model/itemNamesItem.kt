package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class itemNamesItem(
    @SerializedName("name")
    val name: String,
    @SerializedName("value")
    val value: String
)