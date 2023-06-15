package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class RecordListsItem(
    @SerializedName("id")
    var id : Int ?=0 ,
    @SerializedName("formId")
    var formId : Int ?=0 ,
    @SerializedName("creationDate")
    var creationDate : String ?="",
    @SerializedName("url")
    var url : String ?="" ,
    @SerializedName("formName")
    var formName : String ?=""
)