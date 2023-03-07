package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class RecordListsItem(
    @SerializedName("code")
    val code: String,
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("recordFormId")
    val recordFormId: Int,
    @SerializedName("recordId")
    val recordId: Int
)