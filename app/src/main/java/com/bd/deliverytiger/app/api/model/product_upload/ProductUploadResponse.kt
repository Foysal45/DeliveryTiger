package com.bd.deliverytiger.app.api.model.product_upload


import com.google.gson.annotations.SerializedName

data class ProductUploadResponse(
    @SerializedName("DealId")
    var dealId: Int,
    @SerializedName("ProductTitle")
    var productTitle: String?,
    @SerializedName("ProductDescription")
    var productDescription: String?,
    @SerializedName("CustomerId")
    var customerId: Int,
    @SerializedName("FolderName")
    var folderName: String?,
    @SerializedName("ProductPrice")
    var productPrice: Int
)