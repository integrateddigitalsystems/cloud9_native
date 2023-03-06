package com.ids.cloud9.model

import com.google.gson.annotations.SerializedName

class TokenResource(
    @SerializedName("name")
    var name : String ?="" ,

    @SerializedName("userId")
    var userId : Int ?=0
) {
}