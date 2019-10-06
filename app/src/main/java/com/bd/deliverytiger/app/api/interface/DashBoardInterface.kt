package com.bd.deliverytiger.app.api.`interface`

import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.dashboard.DashBoardReqBody
import com.bd.deliverytiger.app.api.model.dashboard.DashboardResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface DashBoardInterface {

    @POST("api/Dashboard/GetOrderCountByStatusGroup")
    fun getDashboardStatusGroup(@Body body: DashBoardReqBody): Call<GenericResponse<List<DashboardResponseModel>>>
}