package com.ids.cloud9.model

data class UpdateDeviceResponse(
    val appVersion: String,
    val creationDate: String,
    val deviceId: String,
    val deviceModel: String,
    val deviceToken: String,
    val deviceTypeId: Int,
    val id: Int,
    val imei: String,
    val language: String,
    val mobileNotifications: List<Any>,
    val modelName: String,
    val showNotification: Boolean,
    val user: Any,
    val userId: Any
)