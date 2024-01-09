package com.ids.cloud9native.model

import androidx.media3.common.Player;
import com.ids.cloud9native.custom.TouchImageView

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