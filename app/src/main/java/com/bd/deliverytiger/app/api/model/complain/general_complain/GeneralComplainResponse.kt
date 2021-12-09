package com.bd.deliverytiger.app.api.model.complain.general_complain


import com.google.gson.annotations.SerializedName

data class GeneralComplainResponse(
    @SerializedName("AnswerBy")
    val answerBy: Int = 0,
    @SerializedName("Comments")
    val comments: String = "",
    @SerializedName("CompanyName")
    val companyName: String = "",
    @SerializedName("ComplainFrom")
    val complainFrom: String = "",
    @SerializedName("ComplainType")
    val complainType: String = "",
    @SerializedName("FullName")
    val fullName: String = "",
    @SerializedName("GeneralComplainId")
    val generalComplainId: Int = 0,
    @SerializedName("IsIssueSolved")
    val isIssueSolved: Int = 0,
    @SerializedName("Mobile")
    val mobile: String = "",
    @SerializedName("PostedOn")
    val postedOn: String = "",
    @SerializedName("UpdatedBy")
    val updatedBy: Int = 0,
    @SerializedName("UpdatedOn")
    val updatedOn: String = "",

    //Internal use only
    var isExpand: Boolean = false
)