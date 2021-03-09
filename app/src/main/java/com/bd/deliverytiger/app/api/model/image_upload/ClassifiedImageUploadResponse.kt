package com.bd.deliverytiger.app.api.model.image_upload


import com.google.gson.annotations.SerializedName

data class ClassifiedImageUploadResponse(
    @SerializedName("DealId")
    var dealId: Int = 0,
    @SerializedName("WebPImageList")
    var webPImageList: WebPImageList? = WebPImageList(),
    @SerializedName("ProductTitle")
    var productTitle: Any? = Any(),
    @SerializedName("ProductTitleEng")
    var productTitleEng: String? = "",
    @SerializedName("ProductDescription")
    var productDescription:  String? = "",
    @SerializedName("CustomerId")
    var customerId: Int = 0,
    @SerializedName("FolderName")
    var folderName: String? = "",
    @SerializedName("ProductType")
    var productType: Int = 0,
    @SerializedName("ProductPrice")
    var productPrice: Int = 0,
    @SerializedName("DistrictId")
    var districtId: Int = 0,
    @SerializedName("CategoryId")
    var categoryId: Int = 0,
    @SerializedName("SubCategoryId")
    var subCategoryId: Int = 0,
    @SerializedName("SubSubCategoryId")
    var subSubCategoryId: Int = 0,
    @SerializedName("Sizes")
    var sizes: String? = "",
    @SerializedName("BrandId")
    var brandId: Int = 0,
    @SerializedName("IsNegotiable")
    var isNegotiable: Int = 0,
    @SerializedName("ImageCount")
    var imageCount: Int = 0
)