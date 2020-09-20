package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.barikoi.RouteResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterfaceBariKoi {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceBariKoi {
            return retrofit.create(ApiInterfaceBariKoi::class.java)
        }
    }

    @GET("v1/api/route/MTYzMDpSN0xKRTJOVEVE/{startLngLat};{endLngLat}")
    suspend fun getRoutingDetails(
        @Path("startLngLat") startLngLat: String,
        @Path("endLngLat") endLngLat: String,
        @Query("overview") overview: String = "true",
        @Query("alternatives") alternatives: String = "true",
        @Query("steps") steps: String = "true",
    ): NetworkResponse<RouteResponse, ErrorResponse>

}