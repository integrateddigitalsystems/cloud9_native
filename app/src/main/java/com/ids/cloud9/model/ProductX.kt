package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class ProductX(
    @SerializedName("account")
    val account: Any?=null,
    @SerializedName("accountId")
    val accountId: Int?=0,
    @SerializedName("active")
    val active: Boolean?=false,
    @SerializedName("amc")
    val amc: Any?=null,
    @SerializedName("category")
    val category: Any?=null,
    @SerializedName("categoryId")
    val categoryId: Int?=0,
    @SerializedName("contractProducts")
    val contractProducts:ArrayList<Any> = arrayListOf(),
    @SerializedName("creationDate")
    val creationDate: String?="",
    @SerializedName("currency")
    val currency: Any ?=null,
    @SerializedName("currencyId")
    val currencyId: Int?=0,
    @SerializedName("currentPrice")
    val currentPrice: Any?=null,
    @SerializedName("defaultPriceList")
    val defaultPriceList: Any?=null,
    @SerializedName("defaultPriceListId")
    val defaultPriceListId: Int?=0,
    @SerializedName("description")
    val description: String?="",
    @SerializedName("id")
    val id: Int?=0,
    @SerializedName("isComponent")
    val isComponent: Boolean?=false,
    @SerializedName("isDeleted")
    val isDeleted: Any?=null,
    @SerializedName("manufacturer")
    val manufacturer: Any?=null,
    @SerializedName("manufacturerId")
    val manufacturerId: Any?=null,
    @SerializedName("modificationDate")
    val modificationDate: String?="",
    @SerializedName("name")
    val name: String?="",
    @SerializedName("orderProducts")
    val orderProducts: ArrayList<Any> ?= arrayListOf(),
    @SerializedName("owner")
    val owner: Any?=null,
    @SerializedName("ownerId")
    val ownerId: Int?=0,
    @SerializedName("pid")
    val pid: String?="",
    @SerializedName("priceListDetails")
    val priceListDetails: ArrayList<Any> = arrayListOf(),
    @SerializedName("productFiles")
    val productFiles: ArrayList <Any> = arrayListOf(),
    @SerializedName("quoteProducts")
    val quoteProducts: ArrayList <Any> = arrayListOf(),
    @SerializedName("serialNumber")
    val serialNumber: Any?=null ,
    @SerializedName("serviceProducts")
    val serviceProducts:  ArrayList <Any> = arrayListOf(),
    @SerializedName("sparePart")
    val sparePart: Any?=null ,
    @SerializedName("status")
    val status: Any?=null ,
    @SerializedName("statusId")
    val statusId: Int?=0,
    @SerializedName("timeStamp")
    val timeStamp: String?="",
    @SerializedName("typeOthers")
    val typeOthers: Any?=null ,
    @SerializedName("unit")
    val unit: Unit?=null ,
    @SerializedName("unitId")
    val unitId: Int?=0,
    @SerializedName("userProducts")
    val userProducts:  ArrayList <Any> = arrayListOf(),
    @SerializedName("visitProducts")
    val visitProducts:  ArrayList <VisitProduct> = arrayListOf(),
    @SerializedName("warrentyDuration")
    val warrentyDuration: Any?=null ,
    @SerializedName("warrentyDurationId")
    val warrentyDurationId: Any?=null
)