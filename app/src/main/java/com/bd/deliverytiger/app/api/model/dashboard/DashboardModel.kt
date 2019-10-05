package com.bd.deliverytiger.app.api.model.dashboard

data class DashboardModel(
    var count: Int,
    var title: String,
    var spanCount: Int = 1,
    var viewType: String = "positive"
)