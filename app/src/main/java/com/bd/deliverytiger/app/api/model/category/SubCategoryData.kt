package com.bd.deliverytiger.app.api.model.category


import com.google.gson.annotations.SerializedName

data class SubCategoryData(
    @SerializedName("categoryId")
    var categoryId: Int = 0,
    @SerializedName("isActive")
    var isActive: Boolean = false,
    @SerializedName("subCategoryId")
    var subCategoryId: Int = 0,
    @SerializedName("subCategoryNameBng")
    var subCategoryNameBng: String? = "",
    @SerializedName("subCategoryNameEng")
    var subCategoryNameEng: String? = ""
)