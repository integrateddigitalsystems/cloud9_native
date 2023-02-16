package com.ids.cloud9.model


import com.fasterxml.jackson.annotation.JsonInclude
import com.google.gson.annotations.SerializedName


@JsonInclude(JsonInclude.Include.ALWAYS)
data class testVisitItem(
    @JsonInclude
    @SerializedName("account")
    var account: Any?=null,
    @JsonInclude
    @SerializedName("accountId")
    var accountId: Int?=0,
    @JsonInclude
    @SerializedName("actualArrivalTime")
    var actualArrivalTime: String="",
    @JsonInclude
    @SerializedName("actualCompletedTime")
    var actualCompletedTime: String="",
    @JsonInclude
    @SerializedName("actualDuration")
    var actualDuration: Double =0.0,
    @JsonInclude
    @SerializedName("assignedTo")
    var assignedTo: Any?=null,
    @JsonInclude
    @SerializedName("assignedToId")
    var assignedToId: Int?=0,
    @JsonInclude
    @SerializedName("company")
    var company: Company?=null,
    @JsonInclude
    @SerializedName("companyAddress")
    var companyAddress: CompanyAddress?=null,
    @JsonInclude
    @SerializedName("companyAddressId")
    var companyAddressId: Int?=0,
    @JsonInclude
    @SerializedName("companyId")
    var companyId: Int?=0,
    @JsonInclude
    @SerializedName("contact")
    var contact: Contact?=null,
    @JsonInclude
    @SerializedName("contactId")
    var contactId: Int?=0,
    @JsonInclude
    @SerializedName("contract")
    var contract: Any?=null,
    @JsonInclude
    @SerializedName("contractId")
    var contractId: Int ?=0,
    @JsonInclude
    @SerializedName("creationDate")
    var creationDate: String?="",
    @JsonInclude
    @SerializedName("duration")
    var duration: Double?=0.0,
    @JsonInclude
    @SerializedName("email")
    var email: String?="",
    @JsonInclude
    @SerializedName("fromTime")
    var fromTime: String?="",
    @JsonInclude
    @SerializedName("id")
    var id: Int?=0,
    @JsonInclude
    @SerializedName("inverseParentVisit")
    var inverseParentVisit: ArrayList<Any> = arrayListOf(),
    @JsonInclude
    @SerializedName("isDeleted")
    var isDeleted: Boolean?=false,
    @JsonInclude
    @SerializedName("modificationDate")
    var modificationDate: String?="",
    @JsonInclude
    @SerializedName("number")
    var number: String?="",
    @JsonInclude
    @SerializedName("opportunity")
    var opportunity: Opportunity?=null,
    @JsonInclude
    @SerializedName("opportunityId")
    var opportunityId: Any?=null,
    @JsonInclude
    @SerializedName("order")
    var order: Any?=null,
    @JsonInclude
    @SerializedName("orderId")
    var orderId: Int?=0,
    @JsonInclude
    @SerializedName("owner")
    var owner: Any?=null,
    @JsonInclude
    @SerializedName("ownerId")
    var ownerId: Int?=0,
    @JsonInclude
    @SerializedName("parentVisit")
    var parentVisit: Any?=null,
    @JsonInclude
    @SerializedName("parentVisitId")
    var parentVisitId: Int?=0,
    @JsonInclude
    @SerializedName("phoneNumber")
    var phoneNumber: String?="",
    @JsonInclude
    @SerializedName("priority")
    var priority: Any?=null,
    @JsonInclude
    @SerializedName("priorityId")
    var priorityId: Int?=0,
    @JsonInclude
    @SerializedName("reason")
    var reason: Any?=null,
    @JsonInclude
    @SerializedName("reasonCode")
    var reasonCode: String?="",
    @JsonInclude
    @SerializedName("reasonId")
    var reasonId: Int?=0,
    @JsonInclude
    @SerializedName("remark")
    var remark: String?="",
    @JsonInclude
    @SerializedName("resource")
    var resource: Any?=null,
    @JsonInclude
    @SerializedName("resourceId")
    var resourceId: Int ?=0,
    @JsonInclude
    @SerializedName("status")
    var status: Any?=null,
    @JsonInclude
    @SerializedName("statusId")
    var statusId: Int?=0,
    @JsonInclude
    @SerializedName("timeStamp")
    var timeStamp: String?="",
    @JsonInclude
    @SerializedName("title")
    var title: String?="",
    @JsonInclude
    @SerializedName("toTime")
    var toTime: String?="",
    @JsonInclude
    @SerializedName("type")
    var type: Any?=null,
    @JsonInclude
    @SerializedName("typeId")
    var typeId: Int?=0,
    @JsonInclude
    @SerializedName("visitDate")
    var visitDate: String?="",
    @JsonInclude
    @SerializedName("visitLocations")
    var visitLocations: ArrayList<Any> = arrayListOf(),
    @JsonInclude
    @SerializedName("visitProducts")
    var visitProducts: ArrayList<Any> = arrayListOf(),
    @JsonInclude
    @SerializedName("visitResources")
    var visitResources: ArrayList<VisitResource> = arrayListOf(),

    var isHeader :Boolean ?=false,

    var dateMill : Long =0,

    var headerOrder : Int = -1
)