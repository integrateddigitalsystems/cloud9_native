package com.ids.cloud9native.model


import com.google.gson.annotations.SerializedName

data class CreateProduct(
    @SerializedName("creationDate")
    var creationDate: String?="",
    @SerializedName("customProductDescription")
    var customProductDescription: String?="",
    @SerializedName("customProductName")
    var customProductName: String?="",
    @SerializedName("formId")
    var formId: Int?=0,
    @SerializedName("id")
    var id: Int?=0,
    @SerializedName("isDeleted")
    var isDeleted: Boolean?=false,
    @SerializedName("modificationDate")
    var modificationDate: String?="",
    @SerializedName("notes")
    var notes: String?="",
    @SerializedName("productId")
    var productId: Int?=0,
    @SerializedName("quantity")
    var quantity: Int?=0,
    @SerializedName("serialNumber")
    var serialNumber: Int?=0,
    @SerializedName("serialNumbers")
    var serialNumbers: ArrayList<String>?= arrayListOf(),
    @SerializedName("unitId")
    var unitId: Int?=0,
    @SerializedName("visitId")
    var visitId: Int?=0,
    @SerializedName("visitNumber")
    var visitNumber: String?=""
)