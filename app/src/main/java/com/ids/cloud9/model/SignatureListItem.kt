package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class SignatureListItem(
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("directory")
    val directory: String?="",
    @SerializedName("entity")
    val entity: Any,
    @SerializedName("entityType")
    val entityType: Any,
    @SerializedName("fileName")
    val fileName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isDefault")
    val isDefault: Any,
    @SerializedName("isSignature")
    val isSignature: Boolean,
    @SerializedName("owner")
    val owner: String,
    @SerializedName("signatureType")
    val signatureType: Boolean,
    @SerializedName("signatureTypeLookup")
    val signatureTypeLookup: String?=""
)