package com.ids.cloud9native.model


import com.google.gson.annotations.SerializedName

data class VisitResource(
    @SerializedName("account")
    val account: Any,
    @SerializedName("accountId")
    val accountId: Any,
    @SerializedName("id")
    var id: Int,
    @SerializedName("resource")
    val resource: Resource,
    @SerializedName("resourceId")
    val resourceId: Int,
    @SerializedName("timeStamp")
    val timeStamp: String,
    @SerializedName("visitId")
    val visitId: Int
)