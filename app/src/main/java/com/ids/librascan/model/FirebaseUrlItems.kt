package com.ids.librascan.model

import com.google.gson.annotations.SerializedName


class FirebaseUrlItems(

    @SerializedName("version_code")
    var versionCode : Double ?=0.0 ,

    @SerializedName("version_name")
    var versionName : String ?="",

    @SerializedName("base_url")
    var baseUrl : String ?="" ,

    @SerializedName("last_version")
    var lastVersion : String ?="" ,

    @SerializedName("force_update_flag")
    var forceUpdate : Boolean ?=false

)