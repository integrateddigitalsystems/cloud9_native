package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class VisitResourceX(
    @SerializedName("id")
    val id: Int,
    @SerializedName("resourceId")
    val resourceId: Int,
    @SerializedName("visitId")
    val visitId: Int
)