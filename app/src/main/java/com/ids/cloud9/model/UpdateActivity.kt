package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class UpdateActivity(
    @SerializedName("assignedTo")
    val assignedTo: String,
    @SerializedName("assignedToId")
    val assignedToId: Int,
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("dueDate")
    val dueDate: String,
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("entity")
    val entity: String,
    @SerializedName("entityId")
    val entityId: Int,
    @SerializedName("entityType")
    val entityType: String,
    @SerializedName("entityTypeId")
    val entityTypeId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("owner")
    val owner: String,
    @SerializedName("ownerId")
    val ownerId: Int,
    @SerializedName("reasonId")
    val reasonId: Int,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("statusId")
    val statusId: Int,
    @SerializedName("statusReason")
    val statusReason: String,
    @SerializedName("subject")
    val subject: String,
    @SerializedName("type")
    val type: String
)