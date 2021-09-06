package com.bd.deliverytiger.app.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.ui.chat.ChatActivity
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.SessionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class FCMService : FirebaseMessagingService() {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val notificationId: Int = 9720
    private val repository: AppRepository by inject()
    private val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US)

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Timber.d("FCMServiceTag ${p0.data}")

        var title = ""
        var body = ""
        var imageUrl = ""
        var type = ""
        var bigText = ""

        p0.notification?.let {
            Timber.d("Message Notification Title: ${it.title}")
            Timber.d("Message Notification Body: ${it.body}")
            Timber.d("Message Notification Image: ${it.imageUrl}")

            title = it.title ?: ""
            body = it.body ?: ""
            imageUrl = it.imageUrl.toString()
        }

        if (p0.data.isNotEmpty()) {
            Timber.d("Message data payload: ${p0.data}")

            p0.data["title"]?.let {
                title = it
            }
            p0.data["body"]?.let {
                body = it
            }
            p0.data["imageUrl"]?.let {
                imageUrl = it
            }
            type = p0.data["notificationType"] ?: ""
            bigText = p0.data["bigText"] ?: ""
        }

        val jsonElement = gson.toJsonTree(p0.data)
        val fcmModel: FCMData = gson.fromJson(jsonElement, FCMData::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            repository.insert(fcmModel.apply {
                createdAt = sdf.format(Date().time)
            })
        }

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannels(notificationManager)
        val builder = createNotification(
            getString(R.string.default_notification_channel_id),
            title, body, createPendingIntent(fcmModel)
        )

        when (type) {
            // Default
            "0" -> {
                notificationManager.notify(notificationId, builder.build())
            }
            // Big text
            "1" -> {
                builder.setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                notificationManager.notify(notificationId, builder.build())
            }
            //Banner
            "2" -> {
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(imageUrl)
                    .into(object : CustomTarget<Bitmap?>() {

                        override fun onLoadCleared(placeholder: Drawable?) {
                            notificationManager.notify(notificationId, builder.build())
                        }

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                            val notificationStyle = NotificationCompat.BigPictureStyle()
                            notificationStyle.bigPicture(resource)
                            notificationStyle.bigLargeIcon(null)
                            builder.setStyle(notificationStyle)
                            notificationManager.notify(notificationId, builder.build())
                        }
                    })
            }
            // BigTextWithSideImage
            "3" -> {
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(imageUrl)
                    .into(object : CustomTarget<Bitmap?>() {

                        override fun onLoadCleared(placeholder: Drawable?) {
                            notificationManager.notify(notificationId, builder.build())
                        }

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                            builder.setLargeIcon(resource)
                            builder.setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                            notificationManager.notify(notificationId, builder.build())
                        }
                    })
            }
            "dt-retention" -> {
                val builder1 = createNotification(
                    getString(R.string.notification_channel_chat),
                    title, body, createChatPendingIntent(fcmModel)
                )
                notificationManager.notify(notificationId, builder1.build())
            }
            // Notification message handle
            else -> {
                Timber.d("Notification message handle called")
                builder.setContentTitle(title).setContentText(body)
                if (imageUrl.isNotEmpty()) {
                    Glide.with(applicationContext)
                        .asBitmap()
                        .load(imageUrl)
                        .apply(RequestOptions().timeout(8000))
                        .into(object : CustomTarget<Bitmap?>() {
                            override fun onLoadCleared(placeholder: Drawable?) {
                                notificationManager.notify(notificationId, builder.build())
                            }

                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                                builder.setLargeIcon(resource)
                                val notificationStyle = NotificationCompat.BigPictureStyle()
                                with(notificationStyle) {
                                    bigPicture(resource)
                                    bigLargeIcon(null)
                                }
                                builder.setStyle(notificationStyle)
                                notificationManager.notify(notificationId, builder.build())
                            }
                        })
                } else {
                    notificationManager.notify(notificationId, builder.build())
                }
            }
        }
    }

    private fun createPendingIntent(fcmModel: FCMData): PendingIntent {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("data", fcmModel)
        return PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createChatPendingIntent(fcmModel: FCMData): PendingIntent {
        val intent = Intent(this, ChatActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("data", fcmModel)
            putExtra("notificationType", fcmModel.notificationType)
        }
        return TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(System.currentTimeMillis().toInt(), PendingIntent.FLAG_UPDATE_CURRENT)!!
        }
    }

    private fun createNotification(
        channelId: String,
        title: String,
        body: String,
        pendingIntent: PendingIntent
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, channelId).apply {
            setSmallIcon(R.drawable.ic_tiger)
            setContentTitle(title)
            setContentText(body)
            setAutoCancel(true)
            color = ContextCompat.getColor(this@FCMService, R.color.colorPrimary)
            setDefaults(NotificationCompat.DEFAULT_ALL)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
        }
    }

    private fun createNotificationChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            val channelList: MutableList<NotificationChannel> = mutableListOf()
            val channel1 = NotificationChannel(
                getString(R.string.default_notification_channel_id),
                "Promotion",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Delivery Tiger offers and promotions"
                setShowBadge(true)
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }
            channelList.add(channel1)

            val channel2 = NotificationChannel(
                getString(R.string.notification_channel_chat),
                getString(R.string.notification_channel_chat_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Delivery Tiger merchant & retention manager chat"
                setShowBadge(true)
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }
            channelList.add(channel2)

            notificationManager.createNotificationChannels(channelList)
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        SessionManager.firebaseToken = p0
    }
}