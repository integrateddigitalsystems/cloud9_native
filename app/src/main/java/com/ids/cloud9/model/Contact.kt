package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("account")
    val account: Any,
    @SerializedName("accountId")
    val accountId: Int,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("address")
    val address: String?="",
    @SerializedName("address2")
    val address2: String,
    @SerializedName("certificates")
    val certificates: List<Any>,
    @SerializedName("city")
    val city: Any,
    @SerializedName("cityId")
    val cityId: Double,
    @SerializedName("companies")
    val companies: List<Any>,
    @SerializedName("company")
    val company: Any,
    @SerializedName("companyId")
    val companyId: Int,
    @SerializedName("companyName")
    val companyName: String,
    @SerializedName("companyNameAr")
    val companyNameAr: Any,
    @SerializedName("contactFiles")
    val contactFiles: List<Any>,
    @SerializedName("contracts")
    val contracts: List<Any>,
    @SerializedName("countryId")
    val countryId: Int,
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("fax")
    val fax: String?="",
    @SerializedName("firstName")
    var firstName: String,
    @SerializedName("firstNameAr")
    val firstNameAr: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isDeleted")
    val isDeleted: Boolean,
    @SerializedName("jobRole")
    val jobRole: String,
    @SerializedName("lastName")
    var lastName: String,
    @SerializedName("lastNameAr")
    val lastNameAr: String,
    @SerializedName("lead")
    val lead: Any,
    @SerializedName("leadId")
    val leadId: Any,
    @SerializedName("modificationDate")
    val modificationDate: String,
    @SerializedName("opportunities")
    val opportunities: List<Any>,
    @SerializedName("orders")
    val orders: List<Any>,
    @SerializedName("owner")
    val owner: Any,
    @SerializedName("ownerId")
    val ownerId: Int,
    @SerializedName("personalPhoneNumber")
    var personalPhoneNumber: String?="",
    @SerializedName("phoneNumberSecondary")
    val phoneNumberSecondary: String,
    @SerializedName("pid")
    val pid: String,
    @SerializedName("quotes")
    val quotes: List<Any>,
    @SerializedName("timeStamp")
    val timeStamp: String,
    @SerializedName("visits")
    val visits: List<Any>
)