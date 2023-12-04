package com.ids.cloud9.model

data class FormsItem(
    val id: Int?=0,
    val name: String?="",
    val url: String?="",
    var selected : Boolean = false
)