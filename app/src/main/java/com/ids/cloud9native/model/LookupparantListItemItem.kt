package com.ids.cloud9native.model

data class LookupparantListItemItem(
    val accountId: Int?=0,
    val creationDate: Any?=null ,
    val description: Any?=null ,
    val id: Int?=0,
    val isDefault: Any?=null ,
    val isPublic: Boolean?=false,
    val lookupCode: String?="",
    val modiciationDate: Any?=null ,
    val name: String?="",
    val order: Int?=0,
    val parentId: Int?=0,
    val parentName: Any?=null ,
    val pid: Any?=null
)