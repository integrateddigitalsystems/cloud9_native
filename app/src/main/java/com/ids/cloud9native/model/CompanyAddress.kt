package com.ids.cloud9native.model


import com.google.gson.annotations.SerializedName

data class CompanyAddress(
    @SerializedName("address")
    val address: String?="",
    @SerializedName("city")
    val city: Any?=null,
    @SerializedName("cityId")
    val cityId: Double?=0.0,
    @SerializedName("company")
    val company: Any?=null,
    @SerializedName("companyId")
    val companyId: Int?=0,
    @SerializedName("country")
    val country: Any?=null ,
    @SerializedName("countryId")
    val countryId: Int?=0,
    @SerializedName("fax")
    val fax: String?="",
    @SerializedName("id")
    val id: Int?=0,
    @SerializedName("lat")
    var lat: String?="",
    @SerializedName("long")
    var long: String?="",
    @SerializedName("name")
    val name: String?="",
    @SerializedName("phone")
    val phone: String?="",
    @SerializedName("visits")
    val visits:  ArrayList <Any> = arrayListOf()
)