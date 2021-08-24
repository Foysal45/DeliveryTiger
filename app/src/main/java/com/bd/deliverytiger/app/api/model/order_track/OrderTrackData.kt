package com.bd.deliverytiger.app.api.model.order_track


import com.google.gson.annotations.SerializedName

data class OrderTrackData(
    @SerializedName("statusGroupId")
    var statusGroupId: Int = 0,
    @SerializedName("trackingName")
    var trackingName: String? = "",
    @SerializedName("trackingColor")
    var trackingColor: String? = "",
    @SerializedName("trackingFlag")
    var trackingFlag: Boolean = false,
    @SerializedName("trackingDate")
    var trackingDate: String? = "",
    @SerializedName("expectedDeliveryDate")
    var expectedDeliveryDate: String? = "",
    @SerializedName("expectedFirstDeliveryDate")
    var expectedFirstDeliveryDate: String? = "",
    @SerializedName("trackingOrderBy")
    var trackingOrderBy: Float = 0f,
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("subTrackingShipmentName")
    var subTrackingShipmentName: SubTrackingShipmentName = SubTrackingShipmentName(),
    @SerializedName("subTrackingReturnName")
    var subTrackingReturnName: SubTrackingReturnName = SubTrackingReturnName(),
    @SerializedName("courierDeliveryMan")
    var courierDeliveryMan: CourierDeliveryMan? = CourierDeliveryMan(),

    // Internal use only
    var trackState: Int = 0,
    var trackStateCount: Int = 0

)