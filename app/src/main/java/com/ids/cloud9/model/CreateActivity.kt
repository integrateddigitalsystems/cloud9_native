package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class CreateActivity(
    @SerializedName("assignedToId")
    val assignedToId: Any,
    @SerializedName("description")
    val description: String,
    @SerializedName("entityId")
    val entityId: Int,
    @SerializedName("manyAssigned")
    val manyAssigned: List<Int>,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("subject")
    val subject: String
)