package com.ids.librascan.db



data class SessionQrcode (
    var idSession: Int =0,
    var sessionName:String = "",
    var warehouseName: String = "",
    var date: String = "",
    var description: String = "",
    var count: Int = 0,
    var code: String? = null,
    var unitId : Int = 0 ,
    var quantity  :Int = 0,
    var sessionId : Int = 0,
    var idQrcode: Int =0
){

}