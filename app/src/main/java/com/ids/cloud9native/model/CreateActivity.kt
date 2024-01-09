package com.ids.cloud9native.model


import com.google.gson.annotations.SerializedName

data class CreateActivity(
    @SerializedName("assignedToId")
    var assignedToId: Int?=0,
    @SerializedName("description")
    var description: String?="",
    @SerializedName("entityId")
    var entityId: Int?=0,
    @SerializedName("manyAssigned")
    var manyAssigned: ArrayList<Int>?= arrayListOf(),
    @SerializedName("ownerId")
    var ownerId: String?="",
    @SerializedName("subject")
    var subject: String?="",
    @SerializedName("dueDate")
    var dueDate : String ?=""
)