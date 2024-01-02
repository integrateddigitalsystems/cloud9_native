package com.ids.cloud9native.utils

interface ApiListener {
    fun  onDataRetrieved(success:Boolean,response: Any,apiId:Int)
}