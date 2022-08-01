package com.ids.librascan.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

 class ResponseGetCompanies {
     @SerializedName("status")
     @Expose
     var status: String? = ""

     @SerializedName("code")
     @Expose
     var codenew: Int? = 0
     @SerializedName("total")
     @Expose
     var total: Int? = 0

     @SerializedName("data")
     @Expose
     var companies:ArrayList<Companies>? = arrayListOf()
 }
