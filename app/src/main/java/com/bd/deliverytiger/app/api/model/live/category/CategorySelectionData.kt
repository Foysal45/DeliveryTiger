package com.bd.deliverytiger.app.api.model.live.category

import com.google.gson.annotations.SerializedName

data class CategorySelectionData (
    @SerializedName("CategoryId")
    var categoryId: Int = 0,
    @SerializedName("SubCategoryId")
    var subCategoryId: Int = 0,
    @SerializedName("SubSubCategoryId")
    var subSubCategoryId: Int = 0,
    @SerializedName("CategoryName")
    var categoryName: String? = "",
    @SerializedName("CategoryNameInEnglish")
    var categoryNameInEnglish: String? = "",
    @SerializedName("SubCategoryName")
    var subCategoryName: String? = "",
    @SerializedName("SubCategoryNameInEnglish")
    var subCategoryNameInEnglish: String? = "",
    @SerializedName("SubSubCategoryName")
    var subSubCategoryName: String? = "",
    @SerializedName("SubSubCategoryNameInEnglish")
    var subSubCategoryNameInEnglish: String? = "",
    var routingName: String? = ""
)