package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class Report(
    @SerializedName("accountId")
    val accountId: Any,
    @SerializedName("code")
    val code: Any,
    @SerializedName("createdBy")
    val createdBy: Any,
    @SerializedName("creationDate")
    val creationDate: Any,
    @SerializedName("fields")
    val fields: List<Any>,
    @SerializedName("footer")
    val footer: Any,
    @SerializedName("header")
    val header: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isActive")
    val isActive: Any,
    @SerializedName("isDeleted")
    val isDeleted: Any,
    @SerializedName("language")
    val language: Any,
    @SerializedName("modificationDate")
    val modificationDate: Any,
    @SerializedName("name")
    val name: String,
    @SerializedName("productCategories")
    val productCategories: List<Any>,
    @SerializedName("productCategoryForms")
    val productCategoryForms: List<Any>,
    @SerializedName("records")
    val records: List<Any>,
    @SerializedName("timeStamp")
    val timeStamp: Any,
    @SerializedName("visitProductReports")
    val visitProductReports: List<Any>
)