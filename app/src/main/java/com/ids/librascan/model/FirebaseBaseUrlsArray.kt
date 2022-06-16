package com.ids.librascan.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FirebaseBaseUrlsArray(

    @SerializedName("android")
    @Expose
    var android : ArrayList<FirebaseUrlItems> = arrayListOf()
)