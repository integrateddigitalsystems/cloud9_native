package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class Opportunity(
    @SerializedName("account")
    val account: Any ?=null,
    @SerializedName("accountId")
    val accountId: Int ?=0,
    @SerializedName("actualClosureDate")
    val actualClosureDate: String?="",
    @SerializedName("actualValue")
    val actualValue: Double,
    @SerializedName("assignedTo")
    val assignedTo: Any ?=null,
    @SerializedName("assignedToId")
    val assignedToId: Any ?=null,
    @SerializedName("beneficiary")
    val beneficiary: Any ?=null,
    @SerializedName("beneficiaryId")
    val beneficiaryId: Int ?=0,
    @SerializedName("certificates")
    val certificates: ArrayList<Any>?=arrayListOf(),
    @SerializedName("company")
    val company: Any ?=null,
    @SerializedName("companyId")
    val companyId: Int ?=0,
    @SerializedName("contact")
    val contact: Any ?=null,
    @SerializedName("contactId")
    val contactId: Int ?=0,
    @SerializedName("contracts")
    val contracts: ArrayList<Any>?=arrayListOf(),
    @SerializedName("creationDate")
    val creationDate: String?="",
    @SerializedName("currency")
    val currency: Any ?=null,
    @SerializedName("currencyId")
    val currencyId: Int ?=0,
    @SerializedName("estimatedRevenue")
    val estimatedRevenue: Any ?=null,
    @SerializedName("estimatedValue")
    val estimatedValue: Int ?=0,
    @SerializedName("expectedClosureDate")
    val expectedClosureDate: Any ?=null,
    @SerializedName("id")
    val id: Int ?=0,
    @SerializedName("isDeleted")
    val isDeleted: Boolean,
    @SerializedName("lead")
    val lead: Any ?=null,
    @SerializedName("leadId")
    val leadId: Any ?=null,
    @SerializedName("modificationDate")
    val modificationDate: String?="",
    @SerializedName("name")
    val name: String?="",
    @SerializedName("nextContactDate")
    val nextContactDate: Any ?=null,
    @SerializedName("opportunityFiles")
    val opportunityFiles: ArrayList<Any>?=arrayListOf(),
    @SerializedName("opporunityPrices")
    val opporunityPrices: ArrayList<Any>?=arrayListOf(),
    @SerializedName("orders")
    val orders: ArrayList<Any>?=arrayListOf(),
    @SerializedName("owner")
    val owner: Any ?=null,
    @SerializedName("ownerId")
    val ownerId: Int ?=0,
    @SerializedName("potential")
    val potential: Any ?=null,
    @SerializedName("potentialId")
    val potentialId: Int ?=0,
    @SerializedName("priceList")
    val priceList: Any ?=null,
    @SerializedName("priceListId")
    val priceListId: Int ?=0,
    @SerializedName("quotes")
    val quotes: ArrayList<Any>?=arrayListOf(),
    @SerializedName("reason")
    val reason: Any ?=null,
    @SerializedName("reasonId")
    val reasonId: Int ?=0,
    @SerializedName("relatedTender")
    val relatedTender: String?="",
    @SerializedName("services")
    val services: ArrayList<Any>?=arrayListOf(),
    @SerializedName("status")
    val status: Any ?=null,
    @SerializedName("statusId")
    val statusId: Int ?=0,
    @SerializedName("type")
    val type: Any ?=null,
    @SerializedName("typeId")
    val typeId: Int ?=0,
    @SerializedName("visits")
    val visits: ArrayList<Any> ?= arrayListOf(),
)