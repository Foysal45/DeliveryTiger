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
import com.bd.deliverytiger.app.api.model.notification.FCMNotification
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.Timber
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Timber.d("FCMServiceTag", p0.data.toString())

        val title = p0.data["title"] ?: ""
        val description = p0.data["description"] ?: ""
        val type = p0.data["notificationType"] ?: "0"
        val imageLink = p0.data["imageLink"] ?: ""
        val bigText = p0.data["bigText"] ?: ""

        val model = FCMNotification(type, title, description, bigText, imageLink)


        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("data", model)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)


        val builder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
        with(builder){
            setSmallIcon(R.drawable.ic_tiger)
            setContentTitle(title)
            setContentText(description)
            setAutoCancel(true)
            setColor(ContextCompat.getColor(this@FCMService, R.color.colorPrimary))
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
                setDescription("Delivery Tiger offers and promotions")
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
                notificationManager.notify(1, builder.build())
            }
            "1" -> { // Big text
                builder.setStyle(NotificationCompat.BigTextStyle().bigText("$description\n$bigText"))
                notificationManager.notify(1, builder.build())
            }
            "2" -> { //Banner
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(imageLink)
                    .into(object : CustomTarget<Bitmap?>() {

                        override fun onLoadCleared(placeholder: Drawable?) {
                            notificationManager.notify(1, builder.build())
                        }

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                            val notificationStyle = NotificationCompat.BigPictureStyle()
                            notificationStyle.bigPicture(resource)
                            notificationStyle.bigLargeIcon(null)
                            builder.setStyle(notificationStyle)
                            notificationManager.notify(1, builder.build())
                        }
                    })
            }
            "3" -> { // BigTextWithSideImage
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(imageLink)
                    .into(object : CustomTarget<Bitmap?>() {

                        override fun onLoadCleared(placeholder: Drawable?) {
                            notificationManager.notify(1, builder.build())
                        }

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                            builder.setLargeIcon(resource)
                            builder.setStyle(NotificationCompat.BigTextStyle().bigText("$description\n$bigText"))
                            notificationManager.notify(1, builder.build())
                        }
                    })
            }
        }

    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        SessionManager.firebaseToken = p0
    }
}