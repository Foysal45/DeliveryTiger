package com.bd.deliverytiger.app.api.model.image_upload


import com.google.gson.annotations.SerializedName

data class ImageUploadResponse(
    @SerializedName("status")
    var status: String? = "",
    @SerializedName("bigImageSize")
    var bigImageSize: ImageDimension = ImageDimension(),
    @SerializedName("smallImageSize")
    var smallImageSize: ImageDimension = ImageDimension(),
    @SerializedName("miniImageSize")
    var miniImageSize: ImageDimension = ImageDimension(),
    @SerializedName("location")
    var location: String? = ""
)