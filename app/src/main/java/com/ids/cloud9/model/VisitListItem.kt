package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class VisitListItem(
    @SerializedName("account")
    var account: Any?=null,
    @SerializedName("accountId")
    var accountId: Int?=0,
    @SerializedName("actualArrivalTime")
    var actualArrivalTime: String?="",
    @SerializedName("actualCompletedTime")
    var actualCompletedTime: String?="",
    @SerializedName("actualDuration")
    var actualDuration: Double?=0.0,
    @SerializedName("assignedTo")
    var assignedTo: Any?=null,
    @SerializedName("assignedToId")
    var assignedToId: Any?=null,
    @SerializedName("company")
    var company: Company?=null,
    @SerializedName("companyAddress")
    var companyAddress: CompanyAddress?=null,
    @SerializedName("companyAddressId")
    var companyAddressId: Int?=0,
    @SerializedName("companyId")
    var companyId: Int?=0,
    @SerializedName("contact")
    var contact: Contact?=null,
    @SerializedName("contactId")
    var contactId: Int?=0,
    @SerializedName("contract")
    var contract: Any?=null,
    @SerializedName("contractId")
    var contractId: Any?=null,
    @SerializedName("creationDate")
    var creationDate: String?="",
    @SerializedName("duration")
    var duration: Double?=0.0,
    @SerializedName("email")
    var email: String?="",
    @SerializedName("fromTime")
    var fromTime: String?="",
    @SerializedName("id")
    var id: Int?=0,
    @SerializedName("inverseParentVisit")
    var inverseParentVisit: ArrayList<Any>?= arrayListOf(),
    @SerializedName("isDeleted")
    var isDeleted: Boolean?=false,
    @SerializedName("modificationDate")
    var modificationDate: String?="",
    @SerializedName("number")
    var number: String?="",
    @SerializedName("opportunity")
    var opportunity: Any?=null,
    @SerializedName("opportunityId")
    var opportunityId: Any?=null,
    @SerializedName("order")
    var order: Any?=null,
    @SerializedName("orderId")
    var orderId: Any?=null,
    @SerializedName("owner")
    var owner: Any?=null,
    @SerializedName("ownerId")
    var ownerId: Int?=0,
    @SerializedName("parentVisit")
    var parentVisit: Any?=null,
    @SerializedName("parentVisitId")
    var parentVisitId: Any?=null,
    @SerializedName("phoneNumber")
    var phoneNumber: String ?="",
    @SerializedName("priority")
    var priority: Any?=null,
    @SerializedName("priorityId")
    var priorityId: Any?=null,
    @SerializedName("reason")
    var reason: Any?=null,
    @SerializedName("reasonId")
    var reasonId: Int?=0,
    @SerializedName("remark")
    var remark: String?=null,
    @SerializedName("resource")
    var resource: Any?=null,
    @SerializedName("resourceId")
    var resourceId: Any?=null,
    @SerializedName("status")
    var status: Any?=null,
    @SerializedName("statusId")
    var statusId: Int?=0,
    @SerializedName("timeStamp")
    var timeStamp: String ?="",
    @SerializedName("title")
    var title: String ?="",
    @SerializedName("toTime")
    var toTime: String ?="",
    @SerializedName("type")
    var type: Any?=null,
    @SerializedName("typeId")
    var typeId: Int?=0,
    @SerializedName("visitDate")
    var visitDate: String ?="",
    @SerializedName("visitLocations")
    var visitLocations: ArrayList<Any>?= arrayListOf(),
    @SerializedName("visitProducts")
    var visitProducts: ArrayList<Any>?= arrayListOf(),
    @SerializedName("visitResources")
    var visitResources: ArrayList<VisitResource>?= arrayListOf(),

    var isHeader :Boolean ?=false,

    var dateMill : Long =0,

    var headerOrder : Int = -1 
){

}