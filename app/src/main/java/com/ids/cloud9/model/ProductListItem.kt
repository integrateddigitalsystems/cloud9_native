package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class ProductListItem(
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("customProductDescription")
    val customProductDescription: String,
    @SerializedName("customProductName")
    val customProductName: String,
    @SerializedName("formId")
    val formId: Int,
    @SerializedName("id")
    val id: Int?=0,
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
    @SerializedName("product")
    val product: ProductX,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("serialNumber")
    val serialNumber: String,
    @SerializedName("serialNumbers")
    val serialNumbers: ArrayList<String> = arrayListOf(),
    @SerializedName("unit")
    val unit: Any,
    @SerializedName("unitId")
    val unitId: Int?=0,
    @SerializedName("visit")
    val visit: Any,
    @SerializedName("visitId")
    val visitId: Int,
    @SerializedName("visitNumber")
    val visitNumber: String,
    var reports : ArrayList<FormsItem> = arrayListOf()
)