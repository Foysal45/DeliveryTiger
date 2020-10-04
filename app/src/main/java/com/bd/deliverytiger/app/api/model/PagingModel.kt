package com.bd.deliverytiger.app.api.model

data class PagingModel<T> (
    var isInitLoad: Boolean = false,
    var totalCount: Int = 0,
    var totalAmount: Int = 0,
    var totalAmountOnlyDelivery: Double = 0.0,
    var totalAmountDeliveryTakaCollection: Double = 0.0,
    var dataList: T
)
