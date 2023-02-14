package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class ContactX(
    @SerializedName("account")
    val account: Any,
    @SerializedName("accountId")
    val accountId: Int,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("address")
    val address: String,
    @SerializedName("address2")
    val address2: String,
    @SerializedName("certificates")
    val certificates: List<Any>,
    @SerializedName("city")
    val city: Any,
    @SerializedName("cityId")
    val cityId: Int,
    @SerializedName("companies")
    val companies: List<Any>,
    @SerializedName("company")
    val company: Any,
    @SerializedName("companyId")
    val companyId: Int,
    @SerializedName("companyName")
    val companyName: String,
    @SerializedName("companyNameAr")
    val companyNameAr: String,
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
    val fax: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("firstNameAr")
    val firstNameAr: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isDeleted")
    val isDeleted: Boolean,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("lastNameAr")
    val lastNameAr: String,
    @SerializedName("lead")
    val lead: Any,
    @SerializedName("leadId")
    val leadId: Int,
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
    val personalPhoneNumber: String,
    @SerializedName("quotes")
    val quotes: List<Any>,
    @SerializedName("visits")
    val visits: List<Any>
)