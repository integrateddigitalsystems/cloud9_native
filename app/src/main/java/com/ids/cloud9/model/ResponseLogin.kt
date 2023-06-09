package com.ids.cloud9.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseLogin(

    @SerializedName("success")
    @Expose
    var success : Boolean ?= false ,

    @SerializedName("token")
    @Expose
    var token : String ?="" ,

    @SerializedName("apiUrl")
    @Expose
    var apiURL : String ?="" ,

    @SerializedName("loginUrl")
    @Expose
    var loginUrl : String ?="",

    @SerializedName("userId")
    @Expose
    var userId : Int ?=0

) {
}