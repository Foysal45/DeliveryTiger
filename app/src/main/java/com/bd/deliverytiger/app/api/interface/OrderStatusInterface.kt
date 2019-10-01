package com.bd.deliverytiger.app.api.`interface`

import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.status.StatusModel
import retrofit2.Call
import retrofit2.http.GET

interface OrderStatusInterface {

    @GET("api/Fetch/LoadCourierOrderStatus")
    fun loadCourierOrderStatus(): Call<GenericResponse<MutableList<StatusModel>>>
}