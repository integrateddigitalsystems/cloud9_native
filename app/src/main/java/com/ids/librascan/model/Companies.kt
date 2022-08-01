package com.ids.librascan.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Companies {

    @SerializedName("name")
    @Expose
    var nameCompanies: String? = ""

    @SerializedName("email")
    @Expose
    var email: String? =""

    @SerializedName("phone")
    @Expose
    var phone: String? = ""

}