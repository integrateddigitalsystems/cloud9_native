package com.ids.librascan.model

import com.ids.librascan.db.QrCode

class SearchProductModel {

    var product = QrCode()
    var units = arrayListOf<Unit>()
    var isByBarcode = false

    override fun toString(): String {
        return product.toString()
    }
}