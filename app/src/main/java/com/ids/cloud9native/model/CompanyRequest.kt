package com.ids.cloud9native.model

import com.google.gson.annotations.SerializedName

data class CompanyRequest(
    @SerializedName("address")
    val address: String?="",
    @SerializedName("companyName")
    val companyName: String?="",
    @SerializedName("companyNameAr")
    val companyNameAr: String?="",
    @SerializedName("email")
    val email: String?="",
    @SerializedName("fax")
    val fax: String?="",
    @SerializedName("id")
    val id: Int?=0,
    @SerializedName("phoneNumber")
    val phoneNumber: String?="",
    @SerializedName("phoneNumberSecondary")
    val phoneNumberSecondary: String?="",
    @SerializedName("website")
    val website: String?="",
    @SerializedName("long")
    val long: String?="",
    @SerializedName("lat")
    val lat: String?=""
)