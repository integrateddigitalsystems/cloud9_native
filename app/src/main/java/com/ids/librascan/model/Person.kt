package com.ids.librascan.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Person {

    @SerializedName("firstname")
    @Expose
    var firstname: String? = ""

    @SerializedName("lastname")
    @Expose
    var lastname: String? =""

    @SerializedName("gender")
    @Expose
    var gender: String? = ""

}