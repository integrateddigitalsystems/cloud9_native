package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class CompanyAddress(
    @SerializedName("address")
    val address: String,
    @SerializedName("city")
    val city: Any,
    @SerializedName("cityId")
    val cityId: Double,
    @SerializedName("company")
    val company: Any,
    @SerializedName("companyId")
    val companyId: Int,
    @SerializedName("country")
    val country: Any,
    @SerializedName("countryId")
    val countryId: Int,
    @SerializedName("fax")
    val fax: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("long")
    val long: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("visits")
    val visits: List<Any>
)