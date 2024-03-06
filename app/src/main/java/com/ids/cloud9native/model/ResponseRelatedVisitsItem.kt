package com.ids.cloud9native.model

import com.google.gson.annotations.SerializedName

data class ResponseRelatedVisitsItem(
    @SerializedName("address")
    val address: String,
    @SerializedName("assignedBy")
    val assignedBy: String,
    @SerializedName("assignedTo")
    val assignedTo: String,
    @SerializedName("company")
    val company: String,
    @SerializedName("companyId")
    val companyId: Int,
    @SerializedName("contact")
    val contact: String,
    @SerializedName("contractNumber")
    val contractNumber: String,
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("fromTime")
    val fromTime: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("number")
    val number: String,
    @SerializedName("opportunity")
    val opportunity: String,
    @SerializedName("orderId")
    val orderId: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("reasonId")
    val reasonId: Int,
    @SerializedName("resourceName")
    val resourceName: String,
    @SerializedName("secondaryAddress")
    val secondaryAddress: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("toTime")
    val toTime: String,
    @SerializedName("visitDate")
    val visitDate: String,
    @SerializedName("visitType")
    val visitType: String,
    @SerializedName("visitResources")
    var visitResources: ArrayList<VisitResource> = arrayListOf(),
)