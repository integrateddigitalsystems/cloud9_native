package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class UnitListItem(
    @SerializedName("accountId")
    val accountId: Int,
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("description")
    val description: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isDefault")
    val isDefault: Any,
    @SerializedName("isPublic")
    val isPublic: Boolean,
    @SerializedName("lookupCode")
    val lookupCode: String,
    @SerializedName("modiciationDate")
    val modiciationDate: Any,
    @SerializedName("name")
    val name: String,
    @SerializedName("order")
    val order: Int,
    @SerializedName("parentId")
    val parentId: Int,
    @SerializedName("parentName")
    val parentName: Any,
    @SerializedName("pid")
    val pid: String
)