package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class MobileConfig(
    @SerializedName("force")
    val force: Boolean,
    @SerializedName("force_version")
    val forceVersion: Double,
    @SerializedName("main_url")
    val mainUrl: String,
    @SerializedName("version")
    val version: Double
)