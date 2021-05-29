package com.bd.deliverytiger.app.api.model.live.category


import com.google.gson.annotations.SerializedName

data class CategoryData(
    @SerializedName("CategoryId")
    var categoryId: Int = 0,
    @SerializedName("CategoryName")
    var categoryName: String? = "",
    @SerializedName("CategoryNameInEnglish")
    var categoryNameInEnglish: String? = "",
    @SerializedName("RoutingName")
    var routingName: String? = "",
    @SerializedName("BannerImage")
    var bannerImage: String? = "",
    @SerializedName("BannerActionUrl")
    var bannerActionUrl: String? = "",
    @SerializedName("IsSubCategoryAvailable")
    var isSubCategoryAvailable: Int = 0,
    @SerializedName("CategoryIcon")
    var categoryIcon: String? = "",
    @SerializedName("SubCategory")
    var subCategory: MutableList<SubCategoryData> = mutableListOf(),
    @SerializedName("Brand")
    var brand: List<BrandData> = listOf()
)