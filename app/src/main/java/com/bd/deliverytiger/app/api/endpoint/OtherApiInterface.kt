package com.bd.deliverytiger.app.api.endpoint

import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.status.StatusGroupModel
import com.bd.deliverytiger.app.api.model.status.StatusModel
import com.bd.deliverytiger.app.api.model.terms.TermsModel
import retrofit2.Call
import retrofit2.http.GET

interface OtherApiInterface {

    @GET("api/Fetch/LoadCourierOrderStatus")
    fun loadCourierOrderStatus(): Call<GenericResponse<MutableList<StatusModel>>>

    @GET("api/Settings/GetSettings")
    fun loadTerms(): Call<GenericResponse<TermsModel>>

    @GET("api/Fetch/GetStatusGroup")
    fun loadStatusGroup(): Call<GenericResponse<MutableList<StatusGroupModel>>>

}