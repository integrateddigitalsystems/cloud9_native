package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class FilteredActivityListItem(
    @SerializedName("assignedTo")
    var assignedTo: String?=null,
    @SerializedName("assignedToId")
    var assignedToId: Int?=0,
    @SerializedName("creationDate")
    var creationDate: String?="",
    @SerializedName("description")
    var description: String?="",
    @SerializedName("dueDate")
    var dueDate: String?="",
    @SerializedName("endDate")
    var endDate: String?="",
    @SerializedName("entity")
    var entity: String?="",
    @SerializedName("entityId")
    var entityId: Int?=0,
    @SerializedName("entityType")
    var entityType: String?="",
    @SerializedName("entityTypeId")
    var entityTypeId: Int?=0,
    @SerializedName("id")
    var id: Int?=0,
    @SerializedName("owner")
    var owner: String?="",
    @SerializedName("ownerId")
    var ownerId: Int?=0,
    @SerializedName("reasonId")
    var reasonId: Int?=0,
    @SerializedName("startDate")
    var startDate: String?="",
    @SerializedName("status")
    var status: String?="",
    @SerializedName("statusId")
    var statusId: Int?=0,
    @SerializedName("statusReason")
    var statusReason: String?="",
    @SerializedName("statusReasonCode")
    var statusReasonCode: String?="",
    @SerializedName("subject")
    var subject: String?="",
    @SerializedName("type")
    var type: String?="" ,
    @SerializedName("reason")
    var reason : String = "",
    @SerializedName("userId")
    var userId : String ?="" ,
    @SerializedName("visitReasonId")
    var visitReasonId : Int ?=0,
    @SerializedName("activityId")
    var activityId : Int ?=0


)