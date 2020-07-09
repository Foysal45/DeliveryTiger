package com.bd.deliverytiger.app.api

import android.content.Context
import androidx.annotation.NonNull
import com.bd.deliverytiger.app.BuildConfig
import com.bd.deliverytiger.app.interfaces.Session
import com.bd.deliverytiger.app.utils.AppConstant
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitSingletonADM private constructor() {

    companion object {

        @Volatile
        private var instance: Retrofit? = null
        private var okHttpClient: OkHttpClient? = null
        private var session: Session? = null

        fun getInstance(@NonNull mContext: Context): Retrofit {
            return instance ?: synchronized(this) {
                instance ?: Retrofit.Builder()
                    .addCallAdapterFactory(NetworkResponseAdapterFactory())
                    .client(createOkHttpClient(mContext))
                    .baseUrl(AppConstant.BASE_URL_ADM)
                    .addConverterFactory(GsonConverterFactory.create()) // Json
                    //.addConverterFactory(ScalarsConverterFactory.create()) // Plain Text
                    .build().also { instance = it }
            }
        }

        fun addSessionListener(session: Session) {
            this.session = session
        }

        private fun createOkHttpClient(context: Context): OkHttpClient {

            /*val customConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
                .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA
                )
                .build()*/

            return okHttpClient ?: OkHttpClient.Builder()
                // Time out
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                // Cache
                .cache(Cache(context.cacheDir, (10 * 1024 * 1024).toLong())) // 10 MB
                // LoggingInterceptor
                .addInterceptor(HttpLoggingInterceptor().apply {
                    //this.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS  else HttpLoggingInterceptor.Level.NONE
                    this.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY  else HttpLoggingInterceptor.Level.NONE
                })
                //.addInterceptor(AuthInterceptor(session))
                // connectionSpecs
                //.connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
                //.connectionSpecs(Collections.singletonList(customConnectionSpec))
                .build().also { okHttpClient = it }
        }

    }
}