package com.bd.deliverytiger.app.api.model.district

import com.google.gson.annotations.SerializedName

class DeliveryAddDataModel {
    //ThanaPayLoad
    @SerializedName("districtInfo")
    var districtInfo: List<DistrictDeliveryChargePayLoad>? = null

    @SerializedName("specialDeliveryThana")
    private var specialDeliveryThana: List<String>? = null

    override fun toString(): String {
        return "DeliveryAddDataModel{" +
                "DistrictInfo=" + districtInfo.toString() +
                ", SpecialDeliveryThana=" + specialDeliveryThana +
                '}'
    }
}