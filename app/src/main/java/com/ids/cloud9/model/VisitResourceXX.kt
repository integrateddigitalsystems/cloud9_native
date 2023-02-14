package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class VisitResourceXX(
    @SerializedName("account")
    val account: Any,
    @SerializedName("accountId")
    val accountId: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("resource")
    val resource: Any,
    @SerializedName("resourceId")
    val resourceId: Int,
    @SerializedName("visitId")
    val visitId: Int
)