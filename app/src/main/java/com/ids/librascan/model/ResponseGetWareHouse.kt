package com.ids.librascan.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ids.librascan.db.Warehouse

class ResponseGetWareHouse {
     @SerializedName("message")
     @Expose
     var message: String? = ""

     @SerializedName("hasError")
     @Expose
     var hasError: String? = ""

     @SerializedName("errorMessage")
     @Expose
     var errorMessage: String? = ""

     @SerializedName("body")
     @Expose
     var warehouse:ArrayList<Warehouse>? = arrayListOf()
 }
