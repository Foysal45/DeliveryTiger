package com.bd.deliverytiger.app.api.model.district

import com.google.gson.annotations.SerializedName

class DeliveryChargePayLoad {
    @SerializedName("messageCode")
    private var messageCode = 0

    @SerializedName("messageText")
    private var messageText: String? = null


    @SerializedName("data")
    var data: DeliveryAddDataModel? = null

    override fun toString(): String {
        return "DeliveryChargePayLoad{" +
                "MessageCode=" + messageCode.toString() +
                ", MessageText='" + messageText + '\''.toString() +
                ", Data=" + data +
                '}'
    }
}