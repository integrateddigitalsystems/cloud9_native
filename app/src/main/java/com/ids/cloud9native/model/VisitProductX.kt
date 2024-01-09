package com.ids.cloud9native.model


import com.google.gson.annotations.SerializedName

data class VisitProductX(
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("customProductDescription")
    val customProductDescription: String,
    @SerializedName("customProductName")
    val customProductName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isDeleted")
    val isDeleted: Boolean,
    @SerializedName("modificationDate")
    val modificationDate: String,
    @SerializedName("notes")
    val notes: String,
    @SerializedName("priceIncluded")
    val priceIncluded: Any,
    @SerializedName("priceListIdDetails")
    val priceListIdDetails: Any,
    @SerializedName("priceListIdDetailsNavigation")
    val priceListIdDetailsNavigation: Any,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("serialNumber")
    val serialNumber: String,
    @SerializedName("timeStamp")
    val timeStamp: String,
    @SerializedName("unit")
    val unit: Any,
    @SerializedName("unitId")
    val unitId: Int,
    @SerializedName("visit")
    val visit: Any,
    @SerializedName("visitId")
    val visitId: Int,
    @SerializedName("visitNumber")
    val visitNumber: String,
    @SerializedName("visitProductDetails")
    val visitProductDetails: List<Any>,
    @SerializedName("visitProductReports")
    val visitProductReports: List<Any>
)