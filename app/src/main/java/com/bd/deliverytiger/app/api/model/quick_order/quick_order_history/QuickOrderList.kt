package com.bd.deliverytiger.app.api.model.quick_order.quick_order_history


import com.bd.deliverytiger.app.api.model.quick_order.QuickOrderTimeSlotData
import com.google.gson.annotations.SerializedName

data class QuickOrderList(
    @SerializedName("orderRequestId")
    var orderRequestId: Int = 0,
    @SerializedName("courierUserId")
    var courierUserId: Int = 0,
    @SerializedName("requestOrderAmount")
    var requestOrderAmount: Int = 0,
    @SerializedName("requestDate")
    var requestDate: String? = "",
    @SerializedName("districtId")
    var districtId: Int = 0,
    @SerializedName("thanaId")
    var thanaId: Int = 0,
    @SerializedName("collectionDate")
    var collectionDate: String? = "",
    @SerializedName("collectionTimeSlotId")
    var collectionTimeSlotId: Int = 0,
    @SerializedName("deliveryUserId")
    var deliveryUserId: Int = 0,
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("totalOrder")
    var totalOrder: Int = 0,
    @SerializedName("collectionTimeSlot")
    var collectionTimeSlot: String? = "",
    @SerializedName("districtsViewModel")
    var districtsViewModel: DistrictsViewModel? = DistrictsViewModel(),
    @SerializedName("actionModel")
    var actionModel: String? = "",
    @SerializedName("actionViewModel")
    var actionViewModel: ActionViewModel? = ActionViewModel(),
    @SerializedName("courierUsersView")
    var courierUsersView: String? = "",
    @SerializedName("deliveryUsersViewModel")
    var deliveryUsersViewModel: String? = "",
    @SerializedName("locationAssignViewModel")
    var locationAssignViewModel: String? = ""
)