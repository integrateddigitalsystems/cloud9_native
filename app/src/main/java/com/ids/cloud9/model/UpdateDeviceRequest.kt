package com.ids.cloud9.model

import com.google.gson.annotations.SerializedName

data class UpdateDeviceRequest(
    @SerializedName("appVersion")
    val appVersion: String?="",
    @SerializedName("creationDate")
    val creationDate: String?="",
    @SerializedName("deviceId")
    val deviceId: String?="",
    @SerializedName("deviceModel")
    val deviceModel: String?="",
    @SerializedName("deviceToken")
    val deviceToken: String?="",
    @SerializedName("deviceTypeId")
    val deviceTypeId: Int?=0,
    @SerializedName("id")
    val id: Int?=0,
    @SerializedName("language")
    val language: String?="",
    @SerializedName("modelName")
    val modelName: String?="",
    @SerializedName("showNotification")
    val showNotification: Boolean?=false,
    @SerializedName("IMEI")
    var imei : String ?="" ,
    @SerializedName("userId")
    var userId: Int?=null
){}