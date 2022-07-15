package com.ids.librascan.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WareHouse {
    @SerializedName("name")
    @Expose
    var name: String? = ""

    @SerializedName("parentWarehouseId")
    @Expose
    var parentWarehouseId: Int? = 0

    @SerializedName("id")
    @Expose
    var id: Int? = 0

    @SerializedName("rowVersion")
    @Expose
    var rowVersion: String? = ""

    @SerializedName("isDeleted")
    @Expose
    var isDeleted: Boolean? = false

}