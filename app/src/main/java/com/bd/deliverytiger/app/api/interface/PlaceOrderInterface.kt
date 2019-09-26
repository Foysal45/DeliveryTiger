package com.bd.deliverytiger.app.api.`interface`

import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.charge.BreakableChargeData
import com.bd.deliverytiger.app.api.model.packaging.PackagingData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PlaceOrderInterface {

    @GET("api/Fetch/GetBreakableCharge")
    fun getBreakableCharge(): Call<GenericResponse<BreakableChargeData>>

    @GET("api/Fetch/GetPackagingChargeRange/{onlyActive}")
    fun getPackagingCharge(@Path("onlyActive") onlyActive: Boolean = true): Call<GenericResponse<List<PackagingData>>>

}