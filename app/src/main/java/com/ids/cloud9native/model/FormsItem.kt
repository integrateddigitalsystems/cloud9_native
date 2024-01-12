package com.ids.cloud9native.model

data class FormsItem(
    var id: Int?=0,
    val name: String?="",
    var url: String?="",
    var selected : Boolean = false
)