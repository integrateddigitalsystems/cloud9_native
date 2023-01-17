package com.ids.cloud9.model

import android.view.View

interface StickerInterface {

    fun getHeaderPositionForItem(itemPosition: Int): Int

    fun getHeaderLayout(headerPosition: Int): Int

    fun bindHeaderData(header: View, headerPosition: Int)

    fun isHeader(itemPosition: Int): Boolean
}
