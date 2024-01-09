package com.ids.cloud9native.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RequestLogin(

    @SerializedName("email")
    @Expose
    var email : String ?="" ,

    @SerializedName("password")
    @Expose
    var password : String ?="" ,

    @SerializedName("fromMobile")
    @Expose
    var fromMobile : Boolean ?= false
) {
}