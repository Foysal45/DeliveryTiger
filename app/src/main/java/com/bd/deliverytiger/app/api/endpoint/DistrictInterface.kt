package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.district.DeliveryChargePayLoad
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DistrictInterface {
    @GET("api/Other/GetAllDistrictFromApi/{id}")
    fun getAllDistrictFromApi(@Path("id") id: Int): Call<DeliveryChargePayLoad>
}