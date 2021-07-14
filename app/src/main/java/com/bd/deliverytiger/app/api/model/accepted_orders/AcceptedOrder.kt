package com.bd.deliverytiger.app.api.model.accepted_orders


import com.bd.deliverytiger.app.api.model.quick_order.QuickOrderTimeSlotData
import com.google.gson.annotations.SerializedName

data class AcceptedOrder(
    @SerializedName("courierOrdersId")
    var courierOrdersId: String? = "",
    @SerializedName("riderAcceptDate")
    var riderAcceptDate: String? = "",
    @SerializedName("deliveryUsersViewModel")
    var deliveryUsersViewModel: DeliverymanInfo = DeliverymanInfo(),
    @SerializedName("collectionTimeSlot")
    var collectionTimeSlot: QuickOrderTimeSlotData? = QuickOrderTimeSlotData()
)