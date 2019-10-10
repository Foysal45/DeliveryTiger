package com.bd.deliverytiger.app.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.provider.Settings
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.LoginInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.login.LoginResponse
import com.bd.deliverytiger.app.interfaces.Session
import com.bd.deliverytiger.app.ui.login.LoginActivity
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainApplication: Application() {

    private lateinit var retrofit: Retrofit
    private var session: Session? = null

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
        RetrofitSingleton.addSessionListener(getSession())
        retrofit = RetrofitSingleton.getInstance(this)

        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener {
                val token = it.token
                SessionManager.firebaseToken = token
                Timber.d("applicationLog", "FirebaseToken:\n$token")
            }

        SessionManager.deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

    }

    fun getSession(): Session{
        if (session == null){
            session = object : Session {
                override fun invalidate() {

                    refreshToken()
                }
            }
        }
        return session!!
    }

    private fun refreshToken(){

        Timber.d("applicationLog", "refreshToken called from Interceptor")
        val loginInterface = retrofit.create(LoginInterface::class.java)
        loginInterface.refreshToken(SessionManager.refreshToken).enqueue(object : Callback<GenericResponse<LoginResponse>>{
            override fun onFailure(call: Call<GenericResponse<LoginResponse>>, t: Throwable) {
                Timber.d("applicationLog", "onFailure: ${t.message}")
            }

            override fun onResponse(call: Call<GenericResponse<LoginResponse>>, response: Response<GenericResponse<LoginResponse>>) {
                Timber.d("applicationLog", "onResponse: ${response.code()} ${response.message()}")
                if (response.code() == 404){
                    val intent = Intent(this@MainApplication, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)

                }
                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.model != null){
                        SessionManager.createSession(response.body()!!.model)
                    } else {
                        startActivity(Intent(this@MainApplication, LoginActivity::class.java))
                    }
                }
            }

        })
    }


}