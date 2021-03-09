package com.bd.deliverytiger.app.api.model.image_upload


import com.google.gson.annotations.SerializedName

data class WebPImageList(
    @SerializedName("LargeImageList")
    var largeImageList: List<Any>? = listOf(),
    @SerializedName("SmallImageList")
    var smallImageList: List<Any>? = listOf(),
    @SerializedName("MiniImageList")
    var miniImageList: List<Any>? = listOf()
)