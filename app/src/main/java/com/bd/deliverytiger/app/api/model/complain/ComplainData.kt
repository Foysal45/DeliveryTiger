package com.bd.deliverytiger.app.api.model.complain


import com.google.gson.annotations.SerializedName

data class ComplainData(
    @SerializedName("SolvedDate")
    var solvedDate: String? = "",
    @SerializedName("ComplaintDate")
    var complaintDate: String? = "",
    @SerializedName("OrderId")
    var orderId: Int = 0,
    @SerializedName("ComplainType")
    var complainType: String? = "",
    @SerializedName("Complain")
    var complain: String? = "",

    //Internal use only
    var isExpand: Boolean = false
)