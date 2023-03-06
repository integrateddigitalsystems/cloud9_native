package com.ids.cloud9.utils

interface ApiListener {
    fun  onDataRetrieved(success:Boolean,response: Any,apiId:Int)
}