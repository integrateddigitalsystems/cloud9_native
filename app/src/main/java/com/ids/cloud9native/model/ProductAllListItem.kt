package com.ids.cloud9native.model


import com.google.gson.annotations.SerializedName

data class ProductAllListItem(
    @SerializedName("amc")
    val amc: Any?=null ,
    @SerializedName("category")
    val category: String?="",
    @SerializedName("categoryId")
    val categoryId: Int?=0,
    @SerializedName("creationDate")
    val creationDate: String?="",
    @SerializedName("currency")
    val currency: String?="",
    @SerializedName("currentPrice")
    val currentPrice: Any?=null ,
    @SerializedName("description")
    val description: String?="" ,
    @SerializedName("id")
    val id: Int?=0,
    @SerializedName("isComponent")
    val isComponent: Boolean?=false,
    @SerializedName("name")
    val name: String?="",
    @SerializedName("opportunityName")
    val opportunityName: Any?=null ,
    @SerializedName("owner")
    val owner: String?="",
    @SerializedName("serialNumber")
    val serialNumber: String?="",
    @SerializedName("sparePart")
    val sparePart: Boolean?=false,
    @SerializedName("status")
    val status: String?="",
    @SerializedName("typeOthers")
    val typeOthers: Boolean?=false,
    @SerializedName("unitId")
    val unitId: Int?=0 ,

    var selected : Boolean ?=false
)