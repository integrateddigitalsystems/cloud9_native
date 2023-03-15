package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class FilteredActivityListItem(
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
    @SerializedName("endDate")
    var endDate: String,
    @SerializedName("entity")
    var entity: String,
    @SerializedName("entityId")
    var entityId: Int,
    @SerializedName("entityType")
    var entityType: String,
    @SerializedName("entityTypeId")
    var entityTypeId: Int,
    @SerializedName("id")
    var id: Int,
    @SerializedName("owner")
    var owner: String,
    @SerializedName("ownerId")
    var ownerId: Int,
    @SerializedName("reasonId")
    var reasonId: Int,
    @SerializedName("startDate")
    var startDate: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("statusId")
    var statusId: Int,
    @SerializedName("statusReason")
    var statusReason: String?="",
    @SerializedName("statusReasonCode")
    var statusReasonCode: String,
    @SerializedName("subject")
    var subject: String,
    @SerializedName("type")
    var type: String ,
    @SerializedName("reason")
    var reason : String = ""
)