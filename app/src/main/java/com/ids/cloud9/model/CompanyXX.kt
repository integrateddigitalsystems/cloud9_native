package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class CompanyXX(
    @SerializedName("account")
    val account: Any,
    @SerializedName("accountId")
    val accountId: Int,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("address")
    val address: String,
    @SerializedName("address2")
    val address2: Any,
    @SerializedName("certificates")
    val certificates: List<Any>,
    @SerializedName("city")
    val city: Any,
    @SerializedName("cityId")
    val cityId: Any,
    @SerializedName("companyAddresses")
    val companyAddresses: List<Any>,
    @SerializedName("companyContact")
    val companyContact: Any,
    @SerializedName("companyContactId")
    val companyContactId: Any,
    @SerializedName("companyContacts")
    val companyContacts: List<Any>,
    @SerializedName("companyFiles")
    val companyFiles: List<Any>,
    @SerializedName("companyName")
    val companyName: String,
    @SerializedName("companyNameAr")
    val companyNameAr: String,
    @SerializedName("contacts")
    val contacts: List<Any>,
    @SerializedName("contracts")
    val contracts: List<Any>,
    @SerializedName("countryId")
    val countryId: Any,
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("fax")
    val fax: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("industry")
    val industry: Any,
    @SerializedName("industryId")
    val industryId: Any,
    @SerializedName("isDeleted")
    val isDeleted: Boolean,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lead")
    val lead: Any,
    @SerializedName("leadId")
    val leadId: Any,
    @SerializedName("long")
    val long: String,
    @SerializedName("modificationDate")
    val modificationDate: String,
    @SerializedName("opportunityBeneficiaries")
    val opportunityBeneficiaries: List<Any>,
    @SerializedName("opportunityCompanies")
    val opportunityCompanies: List<Any>,
    @SerializedName("orders")
    val orders: List<Any>,
    @SerializedName("owner")
    val owner: Any,
    @SerializedName("ownerId")
    val ownerId: Int,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("phoneNumberSecondary")
    val phoneNumberSecondary: Any,
    @SerializedName("pid")
    val pid: String,
    @SerializedName("primaryContact")
    val primaryContact: Int,
    @SerializedName("primaryContactNavigation")
    val primaryContactNavigation: Any,
    @SerializedName("quotes")
    val quotes: List<Any>,
    @SerializedName("services")
    val services: List<Any>,
    @SerializedName("timeStamp")
    val timeStamp: String,
    @SerializedName("vat")
    val vat: Boolean,
    @SerializedName("visits")
    val visits: List<Any>,
    @SerializedName("website")
    val website: String
)