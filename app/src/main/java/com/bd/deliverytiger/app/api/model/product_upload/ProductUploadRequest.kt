package com.bd.deliverytiger.app.api.model.product_upload


import com.google.gson.annotations.SerializedName

data class ProductUploadRequest(

    @SerializedName("ProductTitle")
    var productTitle: String = "",
    @SerializedName("ProductTitleEng")
    var productTitleEng: String = "",
    @SerializedName("ProductDescription")
    var productDescription: String = "",

    @SerializedName("ProductPrice")
    var productPrice: Int = 0,

    @SerializedName("MobileNumber")
    var mobileNumber: String = "",
    @SerializedName("CustomerId")
    var customerId: Int = 0,

    @SerializedName("ProductType")
    var productType: Int = 1,
    @SerializedName("IsNegotiable")
    var isNegotiable: Int = 0,
    @SerializedName("DistrictId")
    var districtId: Int = 0,
    @SerializedName("CategoryId")
    var categoryId: Int = 0,
    @SerializedName("SubCategoryId")
    var subCategoryId: Int = 0,
    @SerializedName("SubSubCategoryId")
    var subSubCategoryId: Int = 0,
    @SerializedName("BrandId")
    var brandId: Int = 0,
    @SerializedName("ImageCount")
    var imageCount: Int = 1,
    @SerializedName("UploadBy")
    var uploadBy: String = "dtApp",
    @SerializedName("AppVersion")
    var appVersion: String = ""
)