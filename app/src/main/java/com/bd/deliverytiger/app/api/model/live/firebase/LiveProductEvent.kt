package com.bd.deliverytiger.app.api.model.live.firebase

data class LiveProductEvent(
    var time: Long = 0,
    var imageUrl: String? = "",
    var price: Int? = 0,
    var productCode: Int? = 0
)
