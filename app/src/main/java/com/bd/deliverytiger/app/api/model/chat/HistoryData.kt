package com.bd.deliverytiger.app.api.model.chat

data class HistoryData(
    var receiverId: String = "",
    var receiverName: String = "",
    var receiverRole: String = "",
    var receiverProfile: String = "",
    var senderId: String = "",
    var message: String = "",
    var url: String = "",
    var type: String = "",
    var date: Long = 0L,
    var seenStatus: Int = 0
)
