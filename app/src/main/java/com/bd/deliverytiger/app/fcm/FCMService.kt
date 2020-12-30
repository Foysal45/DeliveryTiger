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
import androidx.core.content.ContextCompat
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.SessionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import timber.log.Timber

class FCMService: FirebaseMessagingService() {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val notificationId: Int = 9720

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

        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("data", fcmModel)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)


        val builder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
        with(builder){
            setSmallIcon(R.drawable.ic_tiger)
            setContentTitle(title)
            setContentText(body)
            setAutoCancel(true)
            color = ContextCompat.getColor(this@FCMService, R.color.colorPrimary)
            setDefaults(NotificationCompat.DEFAULT_ALL)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
        }

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            val channel = NotificationChannel(getString(R.string.default_notification_channel_id), "Promotion", NotificationManager.IMPORTANCE_DEFAULT)
            with(channel){
                description = "Delivery Tiger offers and promotions"
                setShowBadge(true)
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        when(type){
            "0" -> { // Default
                notificationManager.notify(notificationId, builder.build())
            }
            "1" -> { // Big text
                builder.setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                notificationManager.notify(notificationId, builder.build())
            }
            "2" -> { //Banner
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
            "3" -> { // BigTextWithSideImage
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
            else -> {

                // Notification message handle
                timber.log.Timber.d("Notification message handle called")
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

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        SessionManager.firebaseToken = p0
    }
}