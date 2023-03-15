package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class ProductAllListItem(
    @SerializedName("amc")
    val amc: Any,
    @SerializedName("category")
    val category: String,
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("currentPrice")
    val currentPrice: Any,
    @SerializedName("description")
    val description: String?="",
    @SerializedName("id")
    val id: Int,
    @SerializedName("isComponent")
    val isComponent: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("opportunityName")
    val opportunityName: Any,
    @SerializedName("owner")
    val owner: String,
    @SerializedName("serialNumber")
    val serialNumber: String,
    @SerializedName("sparePart")
    val sparePart: Boolean,
    @SerializedName("status")
    val status: String,
    @SerializedName("typeOthers")
    val typeOthers: Boolean,
    @SerializedName("unitId")
    val unitId: Int ,

    var selected : Boolean ?=false
)