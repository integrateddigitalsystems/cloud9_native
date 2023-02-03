package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class ActivitiesListItem(
    @SerializedName("assignedTo")
    var assignedTo: String,
    @SerializedName("assignedToId")
    var assignedToId: Int,
    @SerializedName("creationDate")
    var creationDate: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("dueDate")
    var dueDate: String,
    @SerializedName("entity")
    var entity: String,
    @SerializedName("entityType")
    var entityType: String,
    @SerializedName("id")
    var id: Int,
    @SerializedName("isAutomated")
    var isAutomated: Boolean,
    @SerializedName("owner")
    var owner: String,
    @SerializedName("reason")
    var reason: String,
    @SerializedName("reasonId")
    var reasonId: Int,
    @SerializedName("status")
    var status: String,
    @SerializedName("subject")
    var subject: String
)