package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class VisitProduct(
    @SerializedName("creationDate")
    val creationDate: String?="",
    @SerializedName("customProductDescription")
    val customProductDescription: String?="",
    @SerializedName("customProductName")
    val customProductName: String?="",
    @SerializedName("id")
    val id: Int?=0,
    @SerializedName("isDeleted")
    val isDeleted: Any?=null ,
    @SerializedName("modificationDate")
    val modificationDate: String?="",
    @SerializedName("notes")
    val notes: Any?=null ,
    @SerializedName("priceIncluded")
    val priceIncluded: Any?=null ,
    @SerializedName("priceListIdDetails")
    val priceListIdDetails: Any?=null ,
    @SerializedName("priceListIdDetailsNavigation")
    val priceListIdDetailsNavigation: Any?=null ,
    @SerializedName("productId")
    val productId: Int?=0,
    @SerializedName("quantity")
    val quantity: Int?=0,
    @SerializedName("serialNumber")
    val serialNumber: String?="",
    @SerializedName("timeStamp")
    val timeStamp: String?="",
    @SerializedName("unit")
    val unit: Any?=null ,
    @SerializedName("unitId")
    val unitId: Int?=0,
    @SerializedName("visit")
    val visit: Any?=null ,
    @SerializedName("visitId")
    val visitId: Int?=0,
    @SerializedName("visitNumber")
    val visitNumber: String?="",
    @SerializedName("visitProductDetails")
    val visitProductDetails: ArrayList <Any> = arrayListOf(),
    @SerializedName("visitProductReports")
    val visitProductReports:  ArrayList <Any> = arrayListOf(),
)