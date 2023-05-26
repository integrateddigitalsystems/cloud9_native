package com.ids.cloud9.model


import com.google.gson.annotations.SerializedName

data class Opportunity(
    @SerializedName("account")
    val account: Any,
    @SerializedName("accountId")
    val accountId: Int,
    @SerializedName("actualClosureDate")
    val actualClosureDate: String,
    @SerializedName("actualValue")
    val actualValue: Double,
    @SerializedName("assignedTo")
    val assignedTo: Any,
    @SerializedName("assignedToId")
    val assignedToId: Any,
    @SerializedName("beneficiary")
    val beneficiary: Any,
    @SerializedName("beneficiaryId")
    val beneficiaryId: Int,
    @SerializedName("certificates")
    val certificates: List<Any>,
    @SerializedName("company")
    val company: Any,
    @SerializedName("companyId")
    val companyId: Int,
    @SerializedName("contact")
    val contact: Any,
    @SerializedName("contactId")
    val contactId: Int,
    @SerializedName("contracts")
    val contracts: List<Any>,
    @SerializedName("creationDate")
    val creationDate: String,
    @SerializedName("currency")
    val currency: Any,
    @SerializedName("currencyId")
    val currencyId: Int,
    @SerializedName("estimatedRevenue")
    val estimatedRevenue: Any,
    @SerializedName("estimatedValue")
    val estimatedValue: Int,
    @SerializedName("expectedClosureDate")
    val expectedClosureDate: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isDeleted")
    val isDeleted: Boolean,
    @SerializedName("lead")
    val lead: Any,
    @SerializedName("leadId")
    val leadId: Any,
    @SerializedName("modificationDate")
    val modificationDate: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("nextContactDate")
    val nextContactDate: Any,
    @SerializedName("opportunityFiles")
    val opportunityFiles: List<Any>,
    @SerializedName("opporunityPrices")
    val opporunityPrices: List<Any>,
    @SerializedName("orders")
    val orders: List<Any>,
    @SerializedName("owner")
    val owner: Any,
    @SerializedName("ownerId")
    val ownerId: Int,
    @SerializedName("potential")
    val potential: Any,
    @SerializedName("potentialId")
    val potentialId: Int,
    @SerializedName("priceList")
    val priceList: Any,
    @SerializedName("priceListId")
    val priceListId: Int,
    @SerializedName("quotes")
    val quotes: List<Any>,
    @SerializedName("reason")
    val reason: Any,
    @SerializedName("reasonId")
    val reasonId: Int,
    @SerializedName("relatedTender")
    val relatedTender: String,
    @SerializedName("services")
    val services: List<Any>,
    @SerializedName("status")
    val status: Any,
    @SerializedName("statusId")
    val statusId: Int,
    @SerializedName("type")
    val type: Any,
    @SerializedName("typeId")
    val typeId: Int,
    @SerializedName("visits")
    val visits: List<Any>
)