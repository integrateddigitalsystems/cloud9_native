package com.ids.cloud9.model

import com.google.gson.annotations.SerializedName

data class CompanyRequest(
    @SerializedName("address")
    val address: String?="",
    @SerializedName("address2")
    val address2: String?="",
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
    val website: String?=""
)