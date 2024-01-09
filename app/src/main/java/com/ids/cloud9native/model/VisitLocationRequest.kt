package com.ids.cloud9native.model


import com.google.gson.annotations.SerializedName

data class VisitLocationRequest(
    @SerializedName("applicationUserId")
    val applicationUserId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isDeleted")
    val isDeleted: Boolean,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("long")
    val long: Double,
    @SerializedName("visitId")
    val visitId: Int
)