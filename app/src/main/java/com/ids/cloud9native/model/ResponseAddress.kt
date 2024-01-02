package com.ids.cloud9native.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseAddress (
    @SerializedName("address_id")
    @Expose
    var addressId : String ?="",

    @SerializedName("address_name")
    @Expose
    var addressName : String ?="" ,

    @SerializedName("latitude")
    @Expose
    var lat : String ?="" ,

    @SerializedName("longitude")
    @Expose
    var long : String ?="" ,

    @SerializedName("street")
    @Expose
    var street : String ?="" ,

    @SerializedName("floor")
    @Expose
    var floor : String ?="" ,

    @SerializedName("description")
    @Expose
    var desc :String ?="" ,

    @SerializedName("building")
    @Expose
    var bldg : String ?="",

    @SerializedName("area")
    @Expose
    var area : String ?="",

    @SerializedName("avenu")
    @Expose
    var avenue : String ?="" ,

    @SerializedName("Apartment")
    @Expose
    var apartment : String ?="" ,


    @SerializedName("block")
    @Expose
    var block : String ?="" ,



    @SerializedName("city")
    @Expose
    var city : String ?="" ,

    @SerializedName("province")
    @Expose
    var province : String ?=""
){

}
