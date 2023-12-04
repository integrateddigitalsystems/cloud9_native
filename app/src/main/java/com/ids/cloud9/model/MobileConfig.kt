package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class MobileConfig(
    @SerializedName("force")
    val force: Boolean ?=false ,
    @SerializedName("force_version")
    val forceVersion: Double ?=0.0 ,
    @SerializedName("main_url")
    val mainUrl: String ?="",
    @SerializedName("url_contains")
    val url_contains: String ?="",
    @SerializedName("version")
    val version: Double ?=0.0 
)