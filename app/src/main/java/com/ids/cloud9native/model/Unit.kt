package com.ids.cloud9native.model


import com.google.gson.annotations.SerializedName

data class Unit(
    @SerializedName("accountDefaultBranches")
    val accountDefaultBranches: ArrayList <Any> = arrayListOf(),
    @SerializedName("accountDefaultCurrencies")
    val accountDefaultCurrencies: ArrayList <Any> = arrayListOf(),
    @SerializedName("accountId")
    val accountId: Int?=0,
    @SerializedName("activityEntityTypes")
    val activityEntityTypes:  ArrayList <Any> = arrayListOf(),
    @SerializedName("activityKeys")
    val activityKeys:  ArrayList <Any> = arrayListOf(),
    @SerializedName("activityPriorities")
    val activityPriorities:  ArrayList <Any> = arrayListOf(),
    @SerializedName("activityReasons")
    val activityReasons:  ArrayList <Any> = arrayListOf(),
    @SerializedName("activityStatuses")
    val activityStatuses:  ArrayList <Any> = arrayListOf(),
    @SerializedName("activityTypes")
    val activityTypes:  ArrayList <Any> = arrayListOf(),
    @SerializedName("attachmentEntityTypes")
    val attachmentEntityTypes:  ArrayList <Any> = arrayListOf(),
    @SerializedName("attachmentSignatureTypeNavigations")
    val attachmentSignatureTypeNavigations:  ArrayList <Any> = arrayListOf(),
    @SerializedName("certificateBranchNavigations")
    val certificateBranchNavigations:  ArrayList <Any> = arrayListOf(),
    @SerializedName("certificateProjects")
    val certificateProjects:  ArrayList <Any> = arrayListOf(),
    @SerializedName("certificateReasons")
    val certificateReasons:  ArrayList <Any> = arrayListOf(),
    @SerializedName("certificateStatuses")
    val certificateStatuses:  ArrayList <Any> = arrayListOf(),
    @SerializedName("companies")
    val companies:  ArrayList <Any> = arrayListOf(),
    @SerializedName("contractCurrencies")
    val contractCurrencies:  ArrayList <Any> = arrayListOf(),
    @SerializedName("contractProducts")
    val contractProducts:  ArrayList <Any> = arrayListOf(),
    @SerializedName("contractReasons")
    val contractReasons:  ArrayList <Any> = arrayListOf(),
    @SerializedName("contractStatuses")
    val contractStatuses:  ArrayList <Any> = arrayListOf(),
    @SerializedName("contractTerms")
    val contractTerms:  ArrayList <Any> = arrayListOf(),
    @SerializedName("creationDate")
    val creationDate: String?="",
    @SerializedName("description")
    val description: Any?=null,
    @SerializedName("id")
    val id: Int?=0,
    @SerializedName("isDefault")
    val isDefault: Any?=null,
    @SerializedName("isPublic")
    val isPublic: Boolean?=false,
    @SerializedName("leadIndustries")
    val leadIndustries:  ArrayList <Any> = arrayListOf(),
    @SerializedName("leadReasons")
    val leadReasons:  ArrayList <Any> = arrayListOf(),
    @SerializedName("leadStatuses")
    val leadStatuses:  ArrayList <Any> = arrayListOf(),
    @SerializedName("lookupCode")
    val lookupCode: String?="",
    @SerializedName("modiciationDate")
    val modiciationDate: Any?=null ,
    @SerializedName("name")
    val name: String?="",
    @SerializedName("opportunityBranchNavigations")
    val opportunityBranchNavigations:  ArrayList <Any> = arrayListOf(),
    @SerializedName("opportunityCurrencies")
    val opportunityCurrencies:  ArrayList <Any> = arrayListOf(),
    @SerializedName("opportunityPotentials")
    val opportunityPotentials:  ArrayList <Any> = arrayListOf(),
    @SerializedName("opportunityProjectCategories")
    val opportunityProjectCategories:  ArrayList <Any> = arrayListOf(),
    @SerializedName("opportunityReasons")
    val opportunityReasons:  ArrayList <Any> = arrayListOf(),
    @SerializedName("opportunityStatuses")
    val opportunityStatuses:  ArrayList <Any> = arrayListOf(),
    @SerializedName("opportunityTypes")
    val opportunityTypes:  ArrayList <Any> = arrayListOf(),
    @SerializedName("order")
    val order: Int?=0,
    @SerializedName("orderBranchNavigations")
    val orderBranchNavigations:  ArrayList <Any> = arrayListOf(),
    @SerializedName("orderCurrencies")
    val orderCurrencies:  ArrayList <Any> = arrayListOf(),
    @SerializedName("orderProducts")
    val orderProducts:  ArrayList <Any> = arrayListOf(),
    @SerializedName("orderProjects")
    val orderProjects:  ArrayList <Any> = arrayListOf(),
    @SerializedName("orderReasons")
    val orderReasons:  ArrayList <Any> = arrayListOf(),
    @SerializedName("orderStatuses")
    val orderStatuses:  ArrayList <Any> = arrayListOf(),
    @SerializedName("parentId")
    val parentId: Int?=0,
    @SerializedName("pid")
    val pid: String?="",
    @SerializedName("priceListCurrencies")
    val priceListCurrencies:  ArrayList <Any> = arrayListOf(),
    @SerializedName("priceListDetailCurrencies")
    val priceListDetailCurrencies:  ArrayList <Any> = arrayListOf(),
    @SerializedName("priceListDetailUnits")
    val priceListDetailUnits:  ArrayList <Any> = arrayListOf(),
    @SerializedName("priceListDetailVariants")
    val priceListDetailVariants:  ArrayList <Any> = arrayListOf(),
    @SerializedName("priceListStatuses")
    val priceListStatuses:  ArrayList <Any> = arrayListOf(),
    @SerializedName("productCurrencies")
    val productCurrencies:  ArrayList <Any> = arrayListOf(),
    @SerializedName("productManufacturers")
    val productManufacturers:  ArrayList <Any> = arrayListOf(),
    @SerializedName("productStatuses")
    val productStatuses:  ArrayList <Any> = arrayListOf(),
    @SerializedName("productUnits")
    val productUnits:  ArrayList <Any> = arrayListOf(),
    @SerializedName("productWarrentyDurations")
    val productWarrentyDurations:  ArrayList <Any> = arrayListOf(),
    @SerializedName("quoteBranchNavigations")
    val quoteBranchNavigations:  ArrayList <Any> = arrayListOf(),
    @SerializedName("quoteProducts")
    val quoteProducts:  ArrayList <Any> = arrayListOf(),
    @SerializedName("quoteProjects")
    val quoteProjects:  ArrayList <Any> = arrayListOf(),
    @SerializedName("quoteReasons")
    val quoteReasons:  ArrayList <Any> = arrayListOf(),
    @SerializedName("quoteStatuses")
    val quoteStatuses:  ArrayList <Any> = arrayListOf(),
    @SerializedName("rateFromCurrencyNavigations")
    val rateFromCurrencyNavigations:  ArrayList <Any> = arrayListOf(),
    @SerializedName("rateToCurrencyNavigations")
    val rateToCurrencyNavigations:  ArrayList <Any> = arrayListOf(),
    @SerializedName("reminderKeyTypes")
    val reminderKeyTypes:  ArrayList <Any> = arrayListOf(),
    @SerializedName("reminderKeys")
    val reminderKeys:  ArrayList <Any> = arrayListOf(),
    @SerializedName("reports")
    val reports:  ArrayList <Any> = arrayListOf(),
    @SerializedName("resourceJobs")
    val resourceJobs:  ArrayList <Any> = arrayListOf(),
    @SerializedName("resourceStatuses")
    val resourceStatuses:  ArrayList <Any> = arrayListOf(),
    @SerializedName("resourceTypes")
    val resourceTypes:  ArrayList <Any> = arrayListOf(),
    @SerializedName("serviceReasons")
    val serviceReasons:  ArrayList <Any> = arrayListOf(),
    @SerializedName("serviceStatuses")
    val serviceStatuses:  ArrayList <Any> = arrayListOf(),
    @SerializedName("serviceTypes")
    val serviceTypes:  ArrayList <Any> = arrayListOf(),
    @SerializedName("timeStamp")
    val timeStamp: String?="",
    @SerializedName("userTaskReasons")
    val userTaskReasons:  ArrayList <Any> = arrayListOf(),
    @SerializedName("userTaskRelatedTos")
    val userTaskRelatedTos:  ArrayList <Any> = arrayListOf(),
    @SerializedName("userTaskStatuses")
    val userTaskStatuses:  ArrayList <Any> = arrayListOf(),
    @SerializedName("visitPriorities")
    val visitPriorities:  ArrayList <Any> = arrayListOf(),
    @SerializedName("visitProducts")
    val visitProducts: ArrayList <VisitProduct> = arrayListOf(),
    @SerializedName("visitReasons")
    val visitReasons:  ArrayList <Any> = arrayListOf(),
    @SerializedName("visitStatuses")
    val visitStatuses:  ArrayList <Any> = arrayListOf(),
    @SerializedName("visitTypes")
    val visitTypes:  ArrayList <Any> = arrayListOf()
)