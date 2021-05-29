package com.bd.deliverytiger.app.api.model.live.catalog

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CatalogData(
    @SerializedName("CatalogId")
    var catalogId: Int = 0,
    @SerializedName("VideoTitle")
    var videoTitle: String? = "",
    @SerializedName("CoverPhoto")
    var coverPhoto: String? = "",
    @SerializedName("VideoLink")
    var videoLink: String? = "",
    @SerializedName("VideoLink1")
    var videoLink1: String = "",

    @SerializedName("TotalView")
    var totalView: Int = 0,
    @SerializedName("FavCount")
    var favCount: Int = 0,
    @SerializedName("TotalAnswerCount")
    var totalAnswerCount: Int = 0,

    @SerializedName("DealPrice")
    var dealPrice: Int = 0,
    @SerializedName("RegularPrice")
    var regularPrice: Int = 0,
    @SerializedName("StartFrom")
    var startFrom: Int = 0,

    @SerializedName("ChannelLogo")
    var channelLogo: String = "",
    @SerializedName("SellingText")
    var sellingText: String? = "",
    @SerializedName("SellingTag")
    var sellingTag: String? = "",

    @SerializedName("CustomerId")
    var customerId: Int = 0,
    @SerializedName("CustomerName")
    var customerName: String? = "",
    @SerializedName("ProfileImage")
    var profileImage: String? = "",

    @SerializedName("CategoryId")
    var categoryId: Int = 0,
    @SerializedName("SubCategoryId")
    var subCategoryId: Int = 0,
    @SerializedName("SubSubCategoryId")
    var subSubCategoryId: Int = 0,
    @SerializedName("HomeFlag")
    var homeFlag: Int = 0,
    @SerializedName("CoverPhotoSize")
    var coverPhotoSize: String? = "",
    @SerializedName("ICon")
    var icon: String? = "",
    @SerializedName("CategoryImage")
    var categoryImage: String? = "",
    @SerializedName("CategoryName")
    var categoryName: String? = "",
    @SerializedName("SubCategoryName")
    var subCategoryName: String? = "",
    @SerializedName("SubSubCategoryName")
    var subSubCategoryName: String? = "",

    // Internal use only
    var isDialogShown: Boolean = false

) : Parcelable