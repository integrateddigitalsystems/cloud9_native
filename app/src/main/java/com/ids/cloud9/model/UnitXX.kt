package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class UnitXX(
    @SerializedName("accountDefaultBranches")
    val accountDefaultBranches: List<Any>,
    @SerializedName("accountDefaultCurrencies")
    val accountDefaultCurrencies: List<Any>,
    @SerializedName("accountId")
    val accountId: Int,
    @SerializedName("activityEntityTypes")
    val activityEntityTypes: List<Any>,
    @SerializedName("activityKeys")
    val activityKeys: List<Any>,
    @SerializedName("activityPriorities")
    val activityPriorities: List<Any>,
    @SerializedName("activityReasons")
    val activityReasons: List<Any>,
    @SerializedName("activityStatuses")
    val activityStatuses: List<Any>,
    @SerializedName("activityTypes")
    val activityTypes: List<Any>,
    @SerializedName("attachmentEntityTypes")
    val attachmentEntityTypes: List<Any>,
    @SerializedName("attachmentSignatureTypeNavigations")
    val attachmentSignatureTypeNavigations: List<Any>,
    @SerializedName("certificateBranchNavigations")
    val certificateBranchNavigations: List<Any>,
    @SerializedName("certificateProjects")
    val certificateProjects: List<Any>,
    @SerializedName("certificateReasons")
    val certificateReasons: List<Any>,
    @SerializedName("certificateStatuses")
    val certificateStatuses: List<Any>,
    @SerializedName("companies")
    val companies: List<Any>,
    @SerializedName("contractCurrencies")
    val contractCurrencies: List<Any>,
    @SerializedName("contractProducts")
    val contractProducts: List<Any>,
    @SerializedName("contractReasons")
    val contractReasons: List<Any>,
    @SerializedName("contractStatuses")
    val contractStatuses: List<Any>,
    @SerializedName("contractTerms")
    val contractTerms: List<Any>,
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("description")
    val description: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isDefault")
    val isDefault: Any,
    @SerializedName("isPublic")
    val isPublic: Boolean,
    @SerializedName("leadIndustries")
    val leadIndustries: List<Any>,
    @SerializedName("leadReasons")
    val leadReasons: List<Any>,
    @SerializedName("leadStatuses")
    val leadStatuses: List<Any>,
    @SerializedName("lookupCode")
    val lookupCode: String,
    @SerializedName("modiciationDate")
    val modiciationDate: Any,
    @SerializedName("name")
    val name: String?="",
    @SerializedName("opportunityBranchNavigations")
    val opportunityBranchNavigations: List<Any>,
    @SerializedName("opportunityCurrencies")
    val opportunityCurrencies: List<Any>,
    @SerializedName("opportunityPotentials")
    val opportunityPotentials: List<Any>,
    @SerializedName("opportunityProjectCategories")
    val opportunityProjectCategories: List<Any>,
    @SerializedName("opportunityReasons")
    val opportunityReasons: List<Any>,
    @SerializedName("opportunityStatuses")
    val opportunityStatuses: List<Any>,
    @SerializedName("opportunityTypes")
    val opportunityTypes: List<Any>,
    @SerializedName("order")
    val order: Int,
    @SerializedName("orderBranchNavigations")
    val orderBranchNavigations: List<Any>,
    @SerializedName("orderCurrencies")
    val orderCurrencies: List<Any>,
    @SerializedName("orderProducts")
    val orderProducts: List<Any>,
    @SerializedName("orderProjects")
    val orderProjects: List<Any>,
    @SerializedName("orderReasons")
    val orderReasons: List<Any>,
    @SerializedName("orderStatuses")
    val orderStatuses: List<Any>,
    @SerializedName("parentId")
    val parentId: Int,
    @SerializedName("pid")
    val pid: String,
    @SerializedName("priceListCurrencies")
    val priceListCurrencies: List<Any>,
    @SerializedName("priceListDetailCurrencies")
    val priceListDetailCurrencies: List<Any>,
    @SerializedName("priceListDetailUnits")
    val priceListDetailUnits: List<Any>,
    @SerializedName("priceListDetailVariants")
    val priceListDetailVariants: List<Any>,
    @SerializedName("priceListStatuses")
    val priceListStatuses: List<Any>,
    @SerializedName("productCurrencies")
    val productCurrencies: List<Any>,
    @SerializedName("productManufacturers")
    val productManufacturers: List<Any>,
    @SerializedName("productStatuses")
    val productStatuses: List<Any>,
    @SerializedName("productUnits")
    val productUnits: List<Any>,
    @SerializedName("productWarrentyDurations")
    val productWarrentyDurations: List<Any>,
    @SerializedName("quoteBranchNavigations")
    val quoteBranchNavigations: List<Any>,
    @SerializedName("quoteProducts")
    val quoteProducts: List<Any>,
    @SerializedName("quoteProjects")
    val quoteProjects: List<Any>,
    @SerializedName("quoteReasons")
    val quoteReasons: List<Any>,
    @SerializedName("quoteStatuses")
    val quoteStatuses: List<Any>,
    @SerializedName("rateFromCurrencyNavigations")
    val rateFromCurrencyNavigations: List<Any>,
    @SerializedName("rateToCurrencyNavigations")
    val rateToCurrencyNavigations: List<Any>,
    @SerializedName("reminderKeyTypes")
    val reminderKeyTypes: List<Any>,
    @SerializedName("reminderKeys")
    val reminderKeys: List<Any>,
    @SerializedName("reports")
    val reports: List<Any>,
    @SerializedName("resourceJobs")
    val resourceJobs: List<Any>,
    @SerializedName("resourceStatuses")
    val resourceStatuses: List<Any>,
    @SerializedName("resourceTypes")
    val resourceTypes: List<Any>,
    @SerializedName("serviceReasons")
    val serviceReasons: List<Any>,
    @SerializedName("serviceStatuses")
    val serviceStatuses: List<Any>,
    @SerializedName("serviceTypes")
    val serviceTypes: List<Any>,
    @SerializedName("timeStamp")
    val timeStamp: String,
    @SerializedName("userTaskReasons")
    val userTaskReasons: List<Any>,
    @SerializedName("userTaskRelatedTos")
    val userTaskRelatedTos: List<Any>,
    @SerializedName("userTaskStatuses")
    val userTaskStatuses: List<Any>,
    @SerializedName("visitPriorities")
    val visitPriorities: List<Any>,
    @SerializedName("visitProducts")
    val visitProducts: ArrayList<VisitProductsItem>,
    @SerializedName("visitReasons")
    val visitReasons: List<Any>,
    @SerializedName("visitStatuses")
    val visitStatuses: List<Any>,
    @SerializedName("visitTypes")
    val visitTypes: List<Any>
)