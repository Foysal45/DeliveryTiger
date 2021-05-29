package com.bd.deliverytiger.app.api.model.live.category


import com.google.gson.annotations.SerializedName

data class SubSubCategoryData(
    @SerializedName("CategoryId")
    var categoryId: Int = 0,
    @SerializedName("SubCategoryId")
    var subCategoryId: Int = 0,
    @SerializedName("SubSubCategoryId")
    var subSubCategoryId: Int = 0,
    @SerializedName("SubSubCategoryName")
    var subSubCategoryName: String? = "",
    @SerializedName("SubSubCategoryNameInEnglish")
    var subSubCategoryNameInEnglish: String? = "",
    @SerializedName("SubSubCategoryIcon")
    var subSubCategoryIcon: String? = "",
    @SerializedName("RoutingName")
    var routingName: String? = ""
)