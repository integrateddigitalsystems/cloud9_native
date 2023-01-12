package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class VisitListItem(
    @SerializedName("account")
    val account: Any,
    @SerializedName("accountId")
    val accountId: Int,
    @SerializedName("actualArrivalTime")
    val actualArrivalTime: String,
    @SerializedName("actualCompletedTime")
    val actualCompletedTime: String,
    @SerializedName("actualDuration")
    val actualDuration: Double,
    @SerializedName("assignedTo")
    val assignedTo: Any,
    @SerializedName("assignedToId")
    val assignedToId: Any,
    @SerializedName("company")
    val company: Company,
    @SerializedName("companyAddress")
    val companyAddress: CompanyAddress,
    @SerializedName("companyAddressId")
    val companyAddressId: Int,
    @SerializedName("companyId")
    val companyId: Int,
    @SerializedName("contact")
    val contact: Contact,
    @SerializedName("contactId")
    val contactId: Int,
    @SerializedName("contract")
    val contract: Any,
    @SerializedName("contractId")
    val contractId: Any,
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("duration")
    val duration: Double,
    @SerializedName("email")
    val email: String,
    @SerializedName("fromTime")
    val fromTime: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("inverseParentVisit")
    val inverseParentVisit: List<Any>,
    @SerializedName("isDeleted")
    val isDeleted: Boolean,
    @SerializedName("modificationDate")
    val modificationDate: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("opportunity")
    val opportunity: Any,
    @SerializedName("opportunityId")
    val opportunityId: Any,
    @SerializedName("order")
    val order: Any,
    @SerializedName("orderId")
    val orderId: Any,
    @SerializedName("owner")
    val owner: Any,
    @SerializedName("ownerId")
    val ownerId: Int,
    @SerializedName("parentVisit")
    val parentVisit: Any,
    @SerializedName("parentVisitId")
    val parentVisitId: Any,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("priority")
    val priority: Any,
    @SerializedName("priorityId")
    val priorityId: Any,
    @SerializedName("reason")
    val reason: Any,
    @SerializedName("reasonId")
    val reasonId: Int,
    @SerializedName("remark")
    val remark: Any,
    @SerializedName("resource")
    val resource: Any,
    @SerializedName("resourceId")
    val resourceId: Any,
    @SerializedName("status")
    val status: Any,
    @SerializedName("statusId")
    val statusId: Int,
    @SerializedName("timeStamp")
    val timeStamp: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("toTime")
    val toTime: String,
    @SerializedName("type")
    val type: Any,
    @SerializedName("typeId")
    val typeId: Int,
    @SerializedName("visitDate")
    val visitDate: String,
    @SerializedName("visitLocations")
    val visitLocations: List<Any>,
    @SerializedName("visitProducts")
    val visitProducts: List<Any>,
    @SerializedName("visitResources")
    val visitResources: List<VisitResource>
)