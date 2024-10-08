package com.app.myrickshawparent.myfirebaseservice

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.app.myrickshawparent.R
import com.app.myrickshawparent.myfirebaseservice.response.FirebaseResponse
import com.app.myrickshawparent.network.utility.NotifyMe
import com.app.myrickshawparent.network.utility.NotifyType
import com.app.myrickshawparent.prefrence.MyDataStore
import com.app.myrickshawparent.ui.DialogActivity
import com.app.myrickshawparent.ui.splash.SplashActivity
import com.app.myrickshawparent.ui.track.response.PopupResponse
import com.app.myrickshawparent.util.Constants
import com.app.myrickshawparent.util.printLog
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class MyFirebaseMsgService : FirebaseMessagingService() {

    var message: String = ""
    private var intent: Intent? = null

    override fun onCreate() {

    }

    override fun handleIntent(intent: Intent?) {
        printLog("MyFirebaseMsgService", "call handleIntent")

        var firebaseResponse = FirebaseResponse()

        if (intent != null) {
            val title = intent.getStringExtra("title")
            val message = intent.getStringExtra("body")
            val type = intent.getStringExtra("type")
            val data = intent.getStringExtra("data").toString()
            if (data != "null" && data.isNotEmpty()) {
                firebaseResponse = Gson().fromJson(data, FirebaseResponse::class.java)
            }

            printLog(
                "MyFirebaseMsgService",
                "call handleIntent Title :-  $title  Type :- $type Body :-  $message  Data :- $data"
            )
            printLog("notification Type", "type : $type")

            if (title != null && message != null) {
                sendNotification(type, title, message, firebaseResponse)
            }

        }

    }

    private fun sendNotification(type: String?, title: String?, message: String?, firebaseResponse: FirebaseResponse) {

        if (Constants.isAppRunning) {
            when (type) {
                "logout" -> {
                    startActivity(
                        Intent(
                            this,
                            DialogActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    return
                }

                "live_trip_stop" -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val popupResponse = PopupResponse(title.toString(), message.toString(), firebaseResponse)
                        val sendResponse = Gson().toJson(popupResponse)
                        Constants.notifyMutableStateFlow.emit(
                            NotifyMe.Notify(
                                sendResponse,
                                NotifyType.LIVE_TRIP_STOP
                            )
                        )
                    }
                }
            }

        }

        CoroutineScope(Dispatchers.Main).launch {
            when (type) {
                "logout" -> {
                    withContext(Dispatchers.IO) {
                        MyDataStore(this@MyFirebaseMsgService).logoutUser()
                    }
                    withContext(Dispatchers.Main) {
                        intent = Intent(this@MyFirebaseMsgService, SplashActivity::class.java)
                        intent!!.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                }

                else -> {
                    intent = Intent(this@MyFirebaseMsgService, SplashActivity::class.java)
                }
            }

            val pendingIntent = PendingIntent.getActivity(
                this@MyFirebaseMsgService,
                randomInt(),
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            // Use the extension to create the notification
            createNotification(
                title = title,
                message = message,
                intent = pendingIntent
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun createNotification(
        importance: Int = NotificationManager.IMPORTANCE_HIGH,
        title: String?,
        message: String?,
        intent: PendingIntent?,
    ) {

        val channelId = resources.getString(R.string.default_notification_channel_id)
        val channelName = resources.getString(R.string.default_notification_channel_id)

        val notificationSoundURI =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, importance).apply {
                    enableLights(true)
                    lightColor = Color.RED
                    enableVibration(true)
                    vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                }
            notificationManager.createNotificationChannel(notificationChannel)

            val notification: Notification = Notification.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_stat_ic_notification
                    )
                )
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
                .setSound(notificationSoundURI)
                .setContentIntent(intent)
                .setGroupSummary(true)
                .setGroup("KEY_NOTIFICATION_GROUP")
                .build()

            notificationManager.notify(randomInt(), notification)

        } else {
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_stat_ic_notification
                    )
                )
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
                .setSound(notificationSoundURI)
                .setContentIntent(intent)
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
                .setGroupSummary(true)
                .setGroup("KEY_NOTIFICATION_GROUP")

            notificationManager.notify(randomInt(), notificationBuilder.build())
        }
    }

    private fun randomInt(): Int {
        return java.util.Random().nextInt(9999 - 1000) + 1000
    }

}
