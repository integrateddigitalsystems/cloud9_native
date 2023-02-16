package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class Company(
    @SerializedName("account")
    var account: Any?=null,
    @SerializedName("accountId")
    var accountId: Int?=0,
    @SerializedName("active")
    var active: Boolean?=false,
    @SerializedName("address")
    var address: String?="",
    @SerializedName("address2")
    var address2: String?="",
    @SerializedName("certificates")
    var certificates: ArrayList<Any> = arrayListOf(),
    @SerializedName("city")
    var city: Any?=null,
    @SerializedName("cityId")
    var cityId: Int?=0,
    @SerializedName("companyAddresses")
    var companyAddresses: ArrayList<Any> = arrayListOf(),
    @SerializedName("companyContact")
    var companyContact: Any?=null,
    @SerializedName("companyContactId")
    var companyContactId: Any?=null,
    @SerializedName("companyContacts")
    var companyContacts: ArrayList<Any> = arrayListOf(),
    @SerializedName("companyFiles")
    var companyFiles: ArrayList<Any> = arrayListOf(),
    @SerializedName("companyName")
    var companyName: String?="",
    @SerializedName("companyNameAr")
    var companyNameAr: String?="",
    @SerializedName("contacts")
    var contacts: ArrayList<Any> = arrayListOf(),
    @SerializedName("contracts")
    var contracts: ArrayList<Any> = arrayListOf(),
    @SerializedName("countryId")
    var countryId: Int?=0,
    @SerializedName("creationDate")
    var creationDate: String?="",
    @SerializedName("email")
    var email: String?="",
    @SerializedName("fax")
    var fax: String?="",
    @SerializedName("id")
    var id: Int?=0,
    @SerializedName("industry")
    var industry: Any?=null,
    @SerializedName("industryId")
    var industryId: Any?=null,
    @SerializedName("isDeleted")
    var isDeleted: Boolean?=false,
    @SerializedName("lat")
    var lat: String?="",
    @SerializedName("lead")
    var lead: Any?=null,
    @SerializedName("leadId")
    var leadId: Int?=0,
    @SerializedName("long")
    var long: String?="",
    @SerializedName("modificationDate")
    var modificationDate: String?="",
    @SerializedName("opportunityBeneficiaries")
    var opportunityBeneficiaries: ArrayList<Any> = arrayListOf(),
    @SerializedName("opportunityCompanies")
    var opportunityCompanies: ArrayList<Any>  = arrayListOf(),
    @SerializedName("orders")
    var orders: ArrayList<Any> = arrayListOf(),
    @SerializedName("owner")
    var owner: Any?=null,
    @SerializedName("ownerId")
    var ownerId: Int?=0,
    @SerializedName("phoneNumber")
    var phoneNumber: String?="",
    @SerializedName("phoneNumberSecondary")
    var phoneNumberSecondary: Any?=null,
    @SerializedName("pid")
    var pid: String?="",
    @SerializedName("primaryContact")
    var primaryContact: Int?=0,
    @SerializedName("primaryContactNavigation")
    var primaryContactNavigation: Any?=null,
    @SerializedName("quotes")
    var quotes: ArrayList<Any> = arrayListOf(),
    @SerializedName("services")
    var services: ArrayList<Any> = arrayListOf(),
    @SerializedName("timeStamp")
    var timeStamp: String?="",
    @SerializedName("vat")
    var vat: Boolean?=false,
    @SerializedName("visits")
    var visits: ArrayList<Any> = arrayListOf(),
    @SerializedName("website")
    var website: String?=""
)