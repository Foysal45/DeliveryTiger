package com.bd.deliverytiger.app.api.model.image_upload


import com.google.gson.annotations.SerializedName

data class ImageDimension(
    @SerializedName("width")
    var width: Int = 0,
    @SerializedName("height")
    var height: Int = 0
)