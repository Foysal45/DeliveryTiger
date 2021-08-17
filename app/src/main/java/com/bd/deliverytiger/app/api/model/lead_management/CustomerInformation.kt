package com.bd.deliverytiger.app.api.model.lead_management


import com.google.gson.annotations.SerializedName

data class CustomerInformation(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("customerName")
    var customerName: String? = "",
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("otherMobile")
    var otherMobile: String? = "",
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("districtId")
    var districtId: Int = 0,
    @SerializedName("thanaId")
    var thanaId: Int = 0,
    @SerializedName("areaId")
    var areaId: Int = 0,
    @SerializedName("paymentType")
    var paymentType: String? = "",
    @SerializedName("orderType")
    var orderType: String? = "",
    @SerializedName("totalOrder")
    var totalOrder: Int = 0,
    @SerializedName("totalCharge")
    var totalCharge: Double = 0.0,
    @SerializedName("weight")
    var weight: String? = "",
    @SerializedName("collectionName")
    var collectionName: String? = "",
    @SerializedName("collectionAmount")
    var collectionAmount: Double = 0.0,
    @SerializedName("deliveryCharge")
    var deliveryCharge: Double = 0.0,
    @SerializedName("isActive")
    var isActive: Boolean = false,
    @SerializedName("updatedBy")
    var updatedBy: Int = 0,
    @SerializedName("updatedOn")
    var updatedOn: String? = "",
    @SerializedName("confirmationDate")
    var confirmationDate: String? = "",
    @SerializedName("courierOrdersId")
    var courierOrdersId: String? = "",
    @SerializedName("orderDate")
    var orderDate: String? = "",
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("postedOn")
    var postedOn: String? = "",
    @SerializedName("postedBy")
    var postedBy: Int = 0,
    @SerializedName("merchantId")
    var merchantId: Int = 0,
    @SerializedName("totalMerchant")
    var totalMerchant: Int = 0,
    @SerializedName("comment")
    var comment: String? = "",
    @SerializedName("podNumber")
    var podNumber: String? = "",
    @SerializedName("breakableCharge")
    var breakableCharge: Double = 0.0,
    @SerializedName("note")
    var note: String? = "",
    @SerializedName("isConfirmedBy")
    var isConfirmedBy: String? = "",
    @SerializedName("codCharge")
    var codCharge: Double = 0.0,
    @SerializedName("courierId")
    var courierId: Int = 0,
    @SerializedName("collectionCharge")
    var collectionCharge: Double = 0.0,
    @SerializedName("returnCharge")
    var returnCharge: Double = 0.0,
    @SerializedName("packagingName")
    var packagingName: String? = "",
    @SerializedName("packagingCharge")
    var packagingCharge: Double = 0.0,
    @SerializedName("collectAddress")
    var collectAddress: String? = "",
    @SerializedName("hubName")
    var hubName: String? = "",
    @SerializedName("orderFrom")
    var orderFrom: String? = "",
    @SerializedName("serviceType")
    var serviceType: String? = "",
    @SerializedName("courierCharge")
    var courierCharge: Double = 0.0,
    @SerializedName("isOpenBox")
    var isOpenBox: Boolean = false,
    @SerializedName("isAutoProcess")
    var isAutoProcess: Boolean = false,
    @SerializedName("isTakaCollectionFromCourier")
    var isTakaCollectionFromCourier: Boolean = false,
    @SerializedName("invoiceNumber")
    var invoiceNumber: String? = "",
    @SerializedName("invoiceCourier")
    var invoiceCourier: String? = "",
    @SerializedName("courierOrderStatus")
    var courierOrderStatus: String? = "",
    @SerializedName("courierUsers")
    var courierUsers: String? = "",
    @SerializedName("couriers")
    var couriers: String? = "",
    @SerializedName("deliveryRangeId")
    var deliveryRangeId: Int = 0,
    @SerializedName("weightRangeId")
    var weightRangeId: Int = 0,
    @SerializedName("expectedDeliveryDate")
    var expectedDeliveryDate: String? = "",
    @SerializedName("deliveredDate")
    var deliveredDate: String? = "",
    @SerializedName("hours")
    var hours: Int = 0,
    @SerializedName("statusNameEng")
    var statusNameEng: String? = "",
    @SerializedName("courierName")
    var courierName: String? = "",
    @SerializedName("isQuickOrder")
    var isQuickOrder: Boolean = false,
    @SerializedName("quickOrderImageUrl")
    var quickOrderImageUrl: String? = "",
    @SerializedName("orderRequestId")
    var orderRequestId: Int = 0,
    @SerializedName("isDownloaded")
    var isDownloaded: Boolean = false,
    @SerializedName("downloadedDate")
    var downloadedDate: String? = "",
    @SerializedName("quickOrderGenerateForHub")
    var quickOrderGenerateForHub: String? = "",
    @SerializedName("documentUrl")
    var documentUrl: String? = "",
    @SerializedName("riderAcceptDate")
    var riderAcceptDate: String? = "",
    @SerializedName("companyName")
    var companyName: String? = "",
    @SerializedName("districtsViewModel")
    var districtsViewModel: DistrictsViewModel? = DistrictsViewModel(),
    @SerializedName("deliveryRange")
    var deliveryRange: DeliveryRange? = DeliveryRange(),
    @SerializedName("deliveryUsersViewModel")
    var deliveryUsersViewModel: String? = "",
    @SerializedName("collectionTimeSlot")
    var collectionTimeSlot: String? = "",
)