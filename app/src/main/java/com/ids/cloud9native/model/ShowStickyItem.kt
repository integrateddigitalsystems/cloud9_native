package com.ids.cloud9native.model

class ShowStickyItem (
    var isHeader : Boolean ?=false ,

    var visitTitle : String ?="" ,

    var visitCompanyName : String ?="" ,

    var durationFromTo : String ?="" ,

    var statusReason : String ?="" ,

    var dayname : String ?="" ,

    var date:String ?="" ,

    var reasonId : Int ?=0 ,

    var today : Boolean ?=false ,

    var textColor : Int ?=0 ,

    var backg : Int ?=0
        ){

}