package com.ids.cloud9native.model

import com.google.gson.annotations.SerializedName

class ResponseMessage (

    @SerializedName("success")
    var success : String ?="" ,

    @SerializedName("message")
    var message : String ?="" ,


    @SerializedName("id")
    var id : Int ?=0

        ){
}