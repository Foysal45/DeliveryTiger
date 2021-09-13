package com.bd.deliverytiger.app.api

import com.bd.deliverytiger.app.api.model.ErrorResponse
import com.bd.deliverytiger.app.api.model.voice_SMS.VoiceSmsAudiRequestBody
import com.bd.deliverytiger.app.api.model.voice_SMS.VoiceSmsAudioResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.Retrofit
import retrofit2.http.*

interface ApiInterfaceInfobip {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceInfobip {
            return retrofit.create(ApiInterfaceInfobip::class.java)
        }
    }

    @Headers(
        "username: AjkDeal",
        "password: isQPx63xTjx96i!",
        "Authorization: App 9ee985c0a29b8325a5e9550498c17cdf-d0eae13e-6316-48fb-8179-90149807af80"
    )
    @POST("tts/3/multi")
    suspend fun sendVoiceSms(@Body requestBody: VoiceSmsAudiRequestBody): NetworkResponse<VoiceSmsAudioResponse, ErrorResponse>

}