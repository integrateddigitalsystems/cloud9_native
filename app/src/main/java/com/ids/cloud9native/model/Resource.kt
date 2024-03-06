package com.ids.cloud9native.model

import com.google.gson.annotations.SerializedName

data class Resource(
    @SerializedName("account")
    val account: Any,
    @SerializedName("accountId")
    val accountId: Int,
    @SerializedName("address")
    val address: String,
    @SerializedName("assignedTo")
    val assignedTo: Int,
    @SerializedName("assignedToNavigation")
    val assignedToNavigation: Any,
    @SerializedName("contractResources")
    val contractResources: List<Any>,
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("designation")
    val designation: Any,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("idnumber")
    val idnumber: Any,
    @SerializedName("isDefault")
    val isDefault: Boolean,
    @SerializedName("isDeleted")
    val isDeleted: Boolean,
    @SerializedName("job")
    val job: Any,
    @SerializedName("jobId")
    val jobId: Int,
    @SerializedName("modificationDate")
    val modificationDate: String,
    @SerializedName("number")
    val number: Any,
    @SerializedName("ownerId")
    val ownerId: Int,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("signature")
    val signature: Any,
    @SerializedName("startDate")
    val startDate: Any,
    @SerializedName("status")
    val status: Any,
    @SerializedName("statusId")
    val statusId: Int,
    @SerializedName("timeStamp")
    val timeStamp: String,
    @SerializedName("type")
    val type: Any,
    @SerializedName("typeId")
    val typeId: Int,
    @SerializedName("visitResources")
    val visitResources: List<Any>,
    @SerializedName("visits")
    val visits: List<Any>
)