package com.bd.deliverytiger.app.api.model.delivery_return_count


import com.google.gson.annotations.SerializedName

data class DeliveredReturnCountResponseItem(
    @SerializedName("delivered")
    var delivered: Int = 0,
    @SerializedName("return")
    var returned: Int = 0,
    @SerializedName("deliveredPercentage")
    var deliveredPercentage: Int = 0,
    @SerializedName("returnPercentagee")
    var returnPercentagee: Int = 0,

)