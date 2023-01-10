package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class JWTResponse(
    @SerializedName("AccountId")
    val accountId: String?="",
    @SerializedName("ApplicationUserId")
    val applicationUserId: String?="",
    @SerializedName("aud")
    val aud: String?="",
    @SerializedName("Department")
    val department: String?="",
    @SerializedName("exp")
    val exp: Int?=0,
    @SerializedName("exp2")
    val exp2: String?="",
    @SerializedName("FirstName")
    val firstName: String?="",
    @SerializedName("iss")
    val iss: String?="",
    @SerializedName("LastName")
    val lastName: String?="",
    @SerializedName("Location")
    val location: String?="",
    @SerializedName("Roles")
    val roles: ArrayList<String>?= arrayListOf(),
    @SerializedName("UserId")
    val userId: String?="",
    @SerializedName("UserName")
    val userName: String?="",
    @SerializedName("VAT")
    val vAT: String?=""
){

}