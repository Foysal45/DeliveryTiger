package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeResponse
import com.bd.deliverytiger.app.api.model.order.UpdateOrderReqBody
import com.bd.deliverytiger.app.api.model.order.UpdateOrderResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PlaceOrderInterface {

    @POST("api/Fetch/DeliveryChargeDetailsAreaWise")
    fun getDeliveryCharge(@Body requestBody: DeliveryChargeRequest): Call<GenericResponse<List<DeliveryChargeResponse>>>

    @PUT("api/Update/UpdateCourierOrders/{courierOrdersId}")
    fun placeOrderUpdate(@Path("courierOrdersId") courierOrdersId: String, @Body requestBody: UpdateOrderReqBody): Call<GenericResponse<UpdateOrderResponse>>

}