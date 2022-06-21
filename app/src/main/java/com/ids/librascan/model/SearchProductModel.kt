package com.ids.librascan.model

import com.ids.librascan.db.QrCode

class SearchProductModel {

    var qrCode = QrCode("",1,"")
    var units = arrayListOf<Unit>()
    var isByBarcode = false

    override fun toString(): String {
        return qrCode.toString()
    }
}