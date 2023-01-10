package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class ApplicationUserListItem(
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("departmentId")
    val departmentId: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("identity")
    val identity: String,
    @SerializedName("isActive")
    val isActive: Boolean,
    @SerializedName("isDeleted")
    val isDeleted: Boolean,
    @SerializedName("isLocked")
    val isLocked: Boolean,
    @SerializedName("jobRole")
    val jobRole: Int,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("leadAssignedTos")
    val leadAssignedTos: List<Any>,
    @SerializedName("leadOwners")
    val leadOwners: List<Any>,
    @SerializedName("location")
    val location: Any,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("roles")
    val roles: List<String>,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userProducts")
    val userProducts: List<Any>
)