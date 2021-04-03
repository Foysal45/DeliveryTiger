package com.bd.deliverytiger.app.api.model.delivery_return_count


import com.google.gson.annotations.SerializedName

data class DeliveredReturnCountResponseItem(
    @SerializedName("Delivered")
    var delivered: Int = 0,
    @SerializedName("Return")
    var returned: Int = 0,
    @SerializedName("DeliveredPercentage")
    var deliveredPercentage: Int = 0,
    @SerializedName("ReturnPercentagee")
    var returnPercentagee: Int = 0,

)