package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class ProductsItem(
    @SerializedName("creationDate")
    var creationDate: String,
    @SerializedName("customProductDescription")
    var customProductDescription: String,
    @SerializedName("customProductName")
    var customProductName: String,
    @SerializedName("formId")
    var formId: Int,
    @SerializedName("id")
    var id: Int,
    @SerializedName("isDeleted")
    var isDeleted: Any,
    @SerializedName("modificationDate")
    var modificationDate: String,
    @SerializedName("notes")
    var notes: Any,
    @SerializedName("priceIncluded")
    var priceIncluded: Any,
    @SerializedName("priceListIdDetails")
    var priceListIdDetails: Any,
    @SerializedName("product")
    var product: Product,
    @SerializedName("productId")
    var productId: Int,
    @SerializedName("quantity")
    var quantity: Int,
    @SerializedName("serialNumber")
    var serialNumber: String,
    @SerializedName("serialNumbers")
    var serialNumbers: List<String>,
    @SerializedName("unit")
    var unit: Unit,
    @SerializedName("unitId")
    var unitId: Int,
    @SerializedName("visit")
    var visit: Any,
    @SerializedName("visitId")
    var visitId: Int,
    @SerializedName("visitNumber")
    var visitNumber: String ,

    var reports : ArrayList<Report> = arrayListOf()
    
)