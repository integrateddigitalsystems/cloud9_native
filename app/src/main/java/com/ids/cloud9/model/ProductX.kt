package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class ProductX(
    @SerializedName("account")
    val account: Any,
    @SerializedName("accountId")
    val accountId: Int,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("amc")
    val amc: Any,
    @SerializedName("category")
    val category: Any,
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("contractProducts")
    val contractProducts: List<Any>,
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("currency")
    val currency: Any,
    @SerializedName("currencyId")
    val currencyId: Int,
    @SerializedName("currentPrice")
    val currentPrice: Any,
    @SerializedName("defaultPriceList")
    val defaultPriceList: Any,
    @SerializedName("defaultPriceListId")
    val defaultPriceListId: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isComponent")
    val isComponent: Boolean,
    @SerializedName("isDeleted")
    val isDeleted: Any,
    @SerializedName("manufacturer")
    val manufacturer: Any,
    @SerializedName("manufacturerId")
    val manufacturerId: Any,
    @SerializedName("modificationDate")
    val modificationDate: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("orderProducts")
    val orderProducts: List<Any>,
    @SerializedName("owner")
    val owner: Any,
    @SerializedName("ownerId")
    val ownerId: Int,
    @SerializedName("pid")
    val pid: String,
    @SerializedName("priceListDetails")
    val priceListDetails: List<Any>,
    @SerializedName("productFiles")
    val productFiles: List<Any>,
    @SerializedName("quoteProducts")
    val quoteProducts: List<Any>,
    @SerializedName("serialNumber")
    val serialNumber: Any,
    @SerializedName("serviceProducts")
    val serviceProducts: List<Any>,
    @SerializedName("sparePart")
    val sparePart: Any,
    @SerializedName("status")
    val status: Any,
    @SerializedName("statusId")
    val statusId: Int,
    @SerializedName("timeStamp")
    val timeStamp: String,
    @SerializedName("typeOthers")
    val typeOthers: Any,
    @SerializedName("unit")
    val unit: UnitXX,
    @SerializedName("unitId")
    val unitId: Int,
    @SerializedName("userProducts")
    val userProducts: List<Any>,
    @SerializedName("visitProducts")
    val visitProducts: List<VisitProductX>,
    @SerializedName("warrentyDuration")
    val warrentyDuration: Any,
    @SerializedName("warrentyDurationId")
    val warrentyDurationId: Any
)