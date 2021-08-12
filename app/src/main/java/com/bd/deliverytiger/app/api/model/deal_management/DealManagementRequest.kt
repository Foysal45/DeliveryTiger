package com.bd.deliverytiger.app.api.model.deal_management

import com.google.gson.annotations.SerializedName

data class DealManagementRequest(

    @SerializedName("ProfileId")
    var profileId: Int = 0,

    @SerializedName("LiveSoldout")
    var liveSoldOut: String = "0",

    @SerializedName("Dealtitle")
    var dealTitle: String = "",

    @SerializedName("DealId")
    var dealId: String = "",

    @SerializedName("FromDate")
    var fromDate: String = "",

    @SerializedName("ToDate")
    var toDate: String = "",

    @SerializedName("OrderBy")
    var orderBy: String = "DESC",

    @SerializedName("CategoryId")
    var categoryId: Int = 0,

    @SerializedName("OrderByType")
    var orderByType: String = "deal", // sale deal

    @SerializedName("TopSaleFlag")
    var topSaleFlag: Int = 0, // for sale 1

    @SerializedName("PagingLowerVal")
    var pagingLowerVal: Int = 0,

    @SerializedName("PagingUpperVal")
    var pagingUpperVal: Int = 20

)