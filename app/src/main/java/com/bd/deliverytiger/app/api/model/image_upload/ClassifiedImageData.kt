package com.bd.deliverytiger.app.api.model.image_upload


import com.google.gson.annotations.SerializedName

data class ClassifiedImageData(
    @SerializedName("ProductTitleEng")
    var productTitleEng: String? = "",
    @SerializedName("FolderName")
    var folderName: String? = "",
    @SerializedName("ClassifiedId")
    var classifiedId: Int = 0,
    @SerializedName("ImageCount")
    var imageCount: Int = 1
)