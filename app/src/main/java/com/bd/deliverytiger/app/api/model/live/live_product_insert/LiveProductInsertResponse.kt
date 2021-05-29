package com.bd.deliverytiger.app.api.model.live.live_product_insert

import com.google.gson.annotations.SerializedName

data class LiveProductInsertResponse(
    @SerializedName("ProductId")
    var productId: Int = 0,
    @SerializedName("FolderName")
    var folderName: String? = ""
)