package com.ids.cloud9native.model


import com.google.gson.annotations.SerializedName

data class ApplicationUserListItem(
    @SerializedName("creationDate")
    val creationDate: String?="",
    @SerializedName("departmentId")
    val departmentId: Int?=0,
    @SerializedName("email")
    val email: String?="",
    @SerializedName("firstName")
    val firstName: String?="",
    @SerializedName("id")
    val id: Int?=0,
    @SerializedName("identity")
    val identity: String?="",
    @SerializedName("isActive")
    val isActive: Boolean?=false,
    @SerializedName("isDeleted")
    val isDeleted: Boolean?=false,
    @SerializedName("isLocked")
    val isLocked: Boolean?=false,
    @SerializedName("jobRole")
    val jobRole: Int?=0,
    @SerializedName("lastName")
    val lastName: String?="",
    @SerializedName("leadAssignedTos")
    val leadAssignedTos: ArrayList <Any> = arrayListOf(),
    @SerializedName("leadOwners")
    val leadOwners: ArrayList <Any> = arrayListOf(),
    @SerializedName("location")
    val location: Any?=null ,
    @SerializedName("phoneNumber")
    val phoneNumber: String?="",
    @SerializedName("roles")
    val roles:  ArrayList <String> = arrayListOf(),
    @SerializedName("userId")
    val userId: String?="",
    @SerializedName("userName")
    val userName: String?="",
    @SerializedName("userProducts")
    val userProducts: ArrayList <Any> = arrayListOf()
)