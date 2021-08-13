package com.bd.deliverytiger.app.api.model.deal_management

import com.google.gson.annotations.SerializedName

data class DealManagementData(
    @SerializedName("DealId")
    var dealId: Int = 0,
    @SerializedName("DealTitle")
    var dealTitle: String? = "",
    @SerializedName("InsertedOn")
    var insertedOn: String? = "",
    @SerializedName("IsSoldOut")
    var isSoldOut: Boolean = false,
    @SerializedName("IsDealClosed")
    var isDealClosed: Boolean = false,
    @SerializedName("IsDeleted")
    var isDeleted: Boolean = false,
    @SerializedName("ProfileID")
    var profileID: Int = 0,
    @SerializedName("ProductCode")
    var productCode: String? = "",
    @SerializedName("FolderName")
    var folderName: String? = "",
    @SerializedName("ImageLink")
    var imageLink: String? = "",
    @SerializedName("CurrentStatus")
    var currentStatus: String? = "",
    @SerializedName("DealsComment")
    var dealsComment: String? = "",
    @SerializedName("IsHotDeal")
    var isHotDeal: Boolean = false,
    @SerializedName("IsActive")
    var isActive: Boolean = false,
    @SerializedName("CategoryId")
    var categoryId: Int = 0,
    @SerializedName("SubCategoryId")
    var subCategoryId: Int = 0,
    @SerializedName("SubSubCategoryID")
    var subSubCategoryID: Int = 0,
    @SerializedName("CommissionPercent")
    var commissionPercent: Int = 0,
    @SerializedName("ImageCount")
    var imageCount: Int = 0,
    @SerializedName("Sizes")
    var sizes: String? = "",
    @SerializedName("ColorID")
    var colorID: Int = 0,
    @SerializedName("BulletDescription")
    var bulletDescription: String? = "",
    @SerializedName("CategoryBng")
    var categoryBng: String? = "",
    @SerializedName("SubCategoryBng")
    var subCategoryBng: String? = "",
    @SerializedName("SubSubCategoryBng")
    var subSubCategoryBng: String? = "",
    @SerializedName("QtnAfterBooking")
    var qtnAfterBooking: Int = 0,
    @SerializedName("AccountsTitle")
    var accountsTitle: String? = "",
    @SerializedName("DealPrice")
    var dealPrice: String? = "",
    @SerializedName("BulletDescriptionEng")
    var bulletDescriptionEng: String? = "",
    @SerializedName("RegularPrice")
    var regularPrice: String? = "",
    @SerializedName("YoutubeEmbeddedCode")
    var youtubeEmbeddedCode: String? = "",
    @SerializedName("IsDealLive")
    var isDealLive: Boolean = false,
    @SerializedName("IsEditable")
    var isEditable: Boolean = false,
    @SerializedName("VoucherId")
    var voucherId: Int = 0,
    @SerializedName("EventId")
    var eventId: Int = 0,
    @SerializedName("ImageList")
    var imageList: List<String> = listOf()

)