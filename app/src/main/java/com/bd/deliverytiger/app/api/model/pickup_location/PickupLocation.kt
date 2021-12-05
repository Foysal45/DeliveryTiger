package com.bd.deliverytiger.app.api.model.pickup_location


import com.google.gson.annotations.SerializedName

data class PickupLocation(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("districtId")
    var districtId: Int = 0,
    @SerializedName("districtName")
    var districtName: String? = "",
    @SerializedName("thanaId")
    var thanaId: Int = 0,
    @SerializedName("thanaName")
    var thanaName: String? = "",
    @SerializedName("pickupAddress")
    var pickupAddress: String? = "",
    @SerializedName("courierUserId")
    var courierUserId: Int = 0,
    @SerializedName("latitude")
    var latitude: String = "0.0",
    @SerializedName("longitude")
    var longitude: String = "0.0",
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("acceptedOrderCount")
    var acceptedOrderCount: Int = 0,
    @SerializedName("collectionTimeSlotId")
    var collectionTimeSlotId: Int = 0
)