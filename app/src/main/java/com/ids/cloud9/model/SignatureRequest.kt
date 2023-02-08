package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class SignatureRequest(
    @SerializedName("entityId")
    val entityId: Int,
    @SerializedName("fileName")
    val fileName: String,
    @SerializedName("fileString")
    val fileString: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isDeleted")
    val isDeleted: Boolean,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("SignatureType")
    val signatureType: Boolean?=null ,
    @SerializedName("isSignature")
    var isSign : Boolean ?=false
)