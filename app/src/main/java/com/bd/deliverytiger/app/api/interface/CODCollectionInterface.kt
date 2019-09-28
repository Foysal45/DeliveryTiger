package com.bd.deliverytiger.app.api.`interface`

import com.bd.deliverytiger.app.api.model.district.DeliveryChargePayLoad
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CODCollectionInterface {
    @GET("/api/Other/GetAllDistrictFromApi/{id}")
    fun getAllCODCollection(@Path("id") id: Int): Call<DeliveryChargePayLoad>

}