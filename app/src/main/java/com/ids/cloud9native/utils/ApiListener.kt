package com.ids.cloud9native.utils

interface ApiListener {
    fun  onDataRetrieved(success:Boolean, currLoc: android.location.Location?=null)
}