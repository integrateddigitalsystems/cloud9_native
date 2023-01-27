package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class ActivitiesListItem(
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
    @SerializedName("entity")
    val entity: String,
    @SerializedName("entityType")
    val entityType: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isAutomated")
    val isAutomated: Boolean,
    @SerializedName("owner")
    val owner: String,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("reasonId")
    val reasonId: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("subject")
    val subject: String
)