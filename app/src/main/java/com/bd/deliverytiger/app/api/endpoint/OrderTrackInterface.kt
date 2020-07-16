package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackMainResponse
import com.bd.deliverytiger.app.api.model.order_track.OrderTrackReqBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderTrackInterface {
    @POST("api/Fetch/GetOrderTracking/{flag}")
    fun getOrderTrackingList(@Path("flag") flag: String, @Body body: OrderTrackReqBody): Call<GenericResponse<List<OrderTrackMainResponse>>>
}