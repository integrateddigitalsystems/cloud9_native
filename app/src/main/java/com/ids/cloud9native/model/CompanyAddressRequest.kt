package com.ids.cloud9native.model

import com.google.gson.annotations.SerializedName

data class CompanyAddressRequest(
    @SerializedName("address")
    val address: String?="",
    @SerializedName("fax")
    val fax: String?="",
    @SerializedName("companyId")
    val companyId: Int?=0,
    @SerializedName("id")
    val id: Int?=0,
    @SerializedName("countryId")
    val countryId: Int?=0,
    @SerializedName("cityId")
    val cityId: Int?=0,
    @SerializedName("phone")
    val phone: String?="",
    @SerializedName("name")
    val name: String?="",
    @SerializedName("long")
    val long: String?="",
    @SerializedName("lat")
    val lat: String?=""
)