package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.cod_collection.CODReqBody
import com.bd.deliverytiger.app.api.model.cod_collection.CODResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AllOrderInterface {
    @POST("api/Fetch/GetAllOrders")
    fun getAllOrder(@Body body: CODReqBody): Call<GenericResponse<CODResponse>>
}