package com.ids.cloud9.model

import com.google.android.exoplayer2.Player
import com.ids.cloud9.custom.TouchImageView

class Videos {
    var id: Int? = null
    var url: String? = ""
    var type: Int? = null
    var zoomer : TouchImageView ?=null
    var player : Player?=null
    var isLocal : Boolean ?= false





    constructor()
    constructor(id: Int?, url: String?, type: Int?) {
        this.id = id
        this.url = url
        this.type = type
    }

}