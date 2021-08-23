package com.bd.deliverytiger.app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.repository.AppRepository
import com.haroldadmin.cnradapter.NetworkResponse
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

@KoinApiExtension
class DistrictCacheWorker(private val context: Context, private val parameters: WorkerParameters): CoroutineWorker(context, parameters), KoinComponent {

    private val repository: AppRepository by inject()
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationId: Int = 10821
    private var isSuccess: Int = 0
    private var resultMsg: String? = ""

    override suspend fun doWork(): Result {

        val data = parameters.inputData

        createNotification()

        syncDistrictData()

        notificationManager.cancel(notificationId)
        return when (isSuccess) {
            1 -> {
                val outputData = Data.Builder()
                    .putString("work_result", "$resultMsg")
                    .build()
                Timber.d("outputData resultMsg: $resultMsg")
                Result.success(outputData)
            }
            3 -> {
                Timber.d("outputData resultMsg: $resultMsg")
                Result.retry()
            }
            else -> {
                val outputData = Data.Builder().putString("work_result", "$resultMsg").build()
                Result.failure(outputData)
            }
        }
    }

    private suspend fun syncDistrictData() {

        val response = repository.loadAllDistricts()
        if (response is NetworkResponse.Success) {
            val dataList = response.body.model
            Timber.d("districtList ${dataList.map { it.districtId }}")
            repository.deleteAndInsert(dataList)
            //repository.insert(dataList)
            isSuccess = 1
        } else {
            isSuccess = 3
        }
    }

    private suspend fun createNotification() {

        val id = "channelSync"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, "Syncing service data", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationBuilder = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle("Syncing service data")
            .setTicker(context.getString(R.string.app_name))
            .setContentText("please wait")
            .setSmallIcon(R.drawable.ic_tiger)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setProgress(10, 0, true)

        //notificationManager.notify(notificationId, notificationBuilder.build())

        val foregroundInfo = ForegroundInfo(notificationId,notificationBuilder.build())
        setForeground(foregroundInfo)
    }
}