package com.bd.deliverytiger.app.api.model.category


import com.google.gson.annotations.SerializedName

data class CategoryData(
    @SerializedName("categoryId")
    var categoryId: Int = 0,
    @SerializedName("categoryNameBng")
    var categoryNameBng: String? = "",
    @SerializedName("categoryNameEng")
    var categoryNameEng: String? = "",
    @SerializedName("isActive")
    var isActive: Boolean = false
)