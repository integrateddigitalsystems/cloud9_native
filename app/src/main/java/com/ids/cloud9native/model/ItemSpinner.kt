package com.ids.cloud9native.model

import com.google.android.exoplayer2.Player

class ItemSpinner(
    var id : Int ?=0 ,

    var name : String ?="" ,

    var selected : Boolean ?=false ,

    var selectable : Boolean?= true ,

    var isLocal : Boolean ?=false ,

    var type : Int ? =0 ,

    var player : Player ?=null
) {
}