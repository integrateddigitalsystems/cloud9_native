package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("account")
    val account: Any?=null,
    @SerializedName("accountId")
    val accountId: Int?=0,
    @SerializedName("active")
    val active: Boolean?=false,
    @SerializedName("address")
    val address: String?="",
    @SerializedName("address2")
    val address2: String?="",
    @SerializedName("certificates")
    val certificates:  ArrayList <Any> = arrayListOf(),
    @SerializedName("city")
    val city: Any?=null ,
    @SerializedName("cityId")
    val cityId: Double?=0.0,
    @SerializedName("companies")
    val companies:  ArrayList <Any> = arrayListOf(),
    @SerializedName("company")
    val company: Any?=null ,
    @SerializedName("companyId")
    val companyId: Int?=0,
    @SerializedName("companyName")
    val companyName: String?="",
    @SerializedName("companyNameAr")
    val companyNameAr: Any?=null ,
    @SerializedName("contactFiles")
    val contactFiles:  ArrayList <Any> = arrayListOf(),
    @SerializedName("contracts")
    val contracts:  ArrayList <Any> = arrayListOf(),
    @SerializedName("countryId")
    val countryId: Int?=0,
    @SerializedName("creationDate")
    val creationDate: String?="",
    @SerializedName("email")
    val email: String?="",
    @SerializedName("fax")
    val fax: String?="",
    @SerializedName("firstName")
    var firstName: String?="",
    @SerializedName("firstNameAr")
    val firstNameAr: String?="",
    @SerializedName("id")
    val id: Int?=0,
    @SerializedName("isDeleted")
    val isDeleted: Boolean?=false,
    @SerializedName("jobRole")
    val jobRole: String?="",
    @SerializedName("lastName")
    var lastName: String?="",
    @SerializedName("lastNameAr")
    val lastNameAr: String?="",
    @SerializedName("lead")
    val lead: Any?=null ,
    @SerializedName("leadId")
    val leadId: Any?=null ,
    @SerializedName("modificationDate")
    val modificationDate: String?="",
    @SerializedName("opportunities")
    val opportunities:  ArrayList <Any> = arrayListOf(),
    @SerializedName("orders")
    val orders:  ArrayList <Any> = arrayListOf(),
    @SerializedName("owner")
    val owner: Any?=null ,
    @SerializedName("ownerId")
    val ownerId: Int?=0,
    @SerializedName("personalPhoneNumber")
    var personalPhoneNumber: String?="",
    @SerializedName("phoneNumberSecondary")
    val phoneNumberSecondary: String?="",
    @SerializedName("pid")
    val pid: String?="",
    @SerializedName("quotes")
    val quotes:  ArrayList <Any> = arrayListOf(),
    @SerializedName("timeStamp")
    val timeStamp: String?="",
    @SerializedName("visits")
    val visits:  ArrayList <Any> = arrayListOf()
)