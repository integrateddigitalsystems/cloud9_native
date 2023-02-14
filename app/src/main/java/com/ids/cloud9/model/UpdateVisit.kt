package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class UpdateVisit(
    @SerializedName("accountId")
    var accountId: Int,
    @SerializedName("actualArrivalTime")
    var actualArrivalTime: String,
    @SerializedName("actualCompletedTime")
    var actualCompletedTime: String,
    @SerializedName("actualDuration")
    var actualDuration: Double,
    @SerializedName("assignedToId")
    var assignedToId: Int,
    @SerializedName("companyAddressId")
    var companyAddressId: Int,
    @SerializedName("companyId")
    var companyId: Int,
    @SerializedName("contactId")
    var contactId: Int,
    @SerializedName("contractId")
    var contractId: Int,
    @SerializedName("creationDate")
    var creationDate: String,
    @SerializedName("duration")
    var duration: Double,
    @SerializedName("email")
    var email: String,
    @SerializedName("fromTime")
    var fromTime: String,
    @SerializedName("id")
    var id: Int,
    @SerializedName("isDeleted")
    var isDeleted: Boolean,
    @SerializedName("modificationDate")
    var modificationDate: String,
    @SerializedName("number")
    var number: String,
    @SerializedName("opportunityId")
    var opportunityId: Int,
    @SerializedName("orderId")
    var orderId: Int,
    @SerializedName("ownerId")
    var ownerId: Int,
    @SerializedName("parentVisitId")
    var parentVisitId: Int,
    @SerializedName("phoneNumber")
    var phoneNumber: String,
    @SerializedName("priorityId")
    var priorityId: Int,
    @SerializedName("reasonId")
    var reasonId: Int,
    @SerializedName("remark")
    var remark: String,
    @SerializedName("resourceId")
    var resourceId: Int,
    @SerializedName("statusId")
    var statusId: Int,
    @SerializedName("title")
    var title: String,
    @SerializedName("toTime")
    var toTime: String,
    @SerializedName("typeId")
    var typeId: Int,
    @SerializedName("visitDate")
    var visitDate: String,
    @SerializedName("visitResources")
    var visitResources: ArrayList<VisitResource> ?= arrayListOf()
)