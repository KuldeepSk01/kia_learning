package com.app.kiyalearning.firebase

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.kiyalearning.R
import com.app.kiyalearning.chat.pojo.NotificationGroup
import com.app.kiyalearning.splash.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("MyTag", "From: ${remoteMessage.from}")

        Log.d("MyTag", "From FCM data: ${remoteMessage.data}")

        Handler(Looper.getMainLooper()).post {
            if (remoteMessage.data.isNotEmpty()) {
                Log.d("MyTag", "Message data not empty")
                Log.d("MyTag", "firebase messaging service: " + remoteMessage.data["data_type"])
                if (remoteMessage.data["data_type"] == "notification") {
                    val notiIntent = Intent(applicationContext, SplashActivity::class.java)
                    notiIntent.putExtra("data_type", remoteMessage.data["data_type"])
                    //  notiIntent.putExtra("IS_NOTIFICATION",true)


//                    val intent = Intent(this, NotificationActivity::class.java)
//                    intent.putExtra("data_type",remoteMessage.data["data_type"])
                    //    intent.putExtra("appointment_of",remoteMessage.data["appointment_of"])
//                    intent.flags = (FLAG_ACTIVITY_NEW_TASK
//                            or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    val pendingIntent: PendingIntent =
                        PendingIntent.getActivity(
                            this,
                            0,
                            notiIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    remoteMessage.notification?.let {

                        val channelId = "notifications"
                        val builder = NotificationCompat.Builder(applicationContext, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(it.title)
                            .setContentText(it.body)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setStyle(
                                NotificationCompat.BigTextStyle()
                                    .bigText(it.body)
                            )

                        createNotificationChannel()
                        val notificationId = 121
                        with(NotificationManagerCompat.from(this)) {
                            // notificationId is a unique int for each notification that you must define
                            if (ActivityCompat.checkSelfPermission(
                                    applicationContext,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                            }
                            notify(notificationId, builder.build())
                        }

                    }

//                    val intent=Intent("MY_CALL_RECEIVER")
//                    intent.putExtra("MY_ACTION",remoteMessage.data["data_type"])
//                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
//                    Log.d("MyTag", "onMessageReceived:  call_disconnected send broadcast")
//                    Log.d("MyTag", "firebase messaging service: running_call_disconnected")
                } else if (remoteMessage.data["data_type"] == "message") {
                    Log.d("Notification", "Group info from notif Message")

                    val notiIntent = Intent(applicationContext, SplashActivity::class.java)
                    notiIntent.putExtra("data_type", remoteMessage.data["data_type"])
                    notiIntent.putExtra("group_data", remoteMessage.data["group_data"])
                    notiIntent.putExtra("group_icon", remoteMessage.data["group_icon"])


                 /*   if (remoteMessage.data["group_data"] != null) {
                        val data = remoteMessage.data["group_data"]
                        val gson = Gson()
                        val nData = gson.fromJson(data, NotificationGroup::class.java)


                        Log.d("Notification", "Group info from notif $data")
                        Log.d("Notification", "converted data from notif $nData")
                         notiIntent.putExtra("groupModel",nData)
                    }*/


                    Log.d("MyTag", "onMessageReceived: " + remoteMessage.data)

                    val pendingIntent: PendingIntent =
                        PendingIntent.getActivity(
                            this,
                            0,
                            notiIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    remoteMessage.notification?.let {

                        val channelId = "notifications"
                        val builder = NotificationCompat.Builder(applicationContext, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(it.title)
                            .setContentText(it.body)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setStyle(
                                NotificationCompat.BigTextStyle()
                                    .bigText(it.body)
                            )

                        createNotificationChannel()
                        val notificationId = 121
                        with(NotificationManagerCompat.from(this)) {
                            // notificationId is a unique int for each notification that you must define
                            if (ActivityCompat.checkSelfPermission(
                                    applicationContext,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                            }
                            notify(notificationId, builder.build())
                        }
                    }

//                    val intent=Intent("MY_CALL_RECEIVER")
//                    intent.putExtra("MY_ACTION",remoteMessage.data["data_type"])
//                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
//                    Log.d("MyTag", "onMessageReceived:  call_disconnected send broadcast")
//                    Log.d("MyTag", "firebase messaging service: running_call_disconnected")
                } else if (remoteMessage.data["data_type"] == "join_class") {
                    val intent2 = Intent("join_class")
                    intent2.putExtra("message", "Welcome To App")
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent2)

                    val notiIntent = Intent(applicationContext, SplashActivity::class.java)
                    notiIntent.putExtra("data_type", remoteMessage.data["data_type"])
                    //  notiIntent.putExtra("IS_NOTIFICATION",true)


//                    val intent = Intent(this, NotificationActivity::class.java)
//                    intent.putExtra("data_type",remoteMessage.data["data_type"])
                    //    intent.putExtra("appointment_of",remoteMessage.data["appointment_of"])
//                    intent.flags = (FLAG_ACTIVITY_NEW_TASK
//                            or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    val pendingIntent: PendingIntent =
                        PendingIntent.getActivity(
                            this,
                            0,
                            notiIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    remoteMessage.notification?.let {

                        val channelId = "notifications"
                        val builder = NotificationCompat.Builder(applicationContext, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(it.title)
                            .setContentText(it.body)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setStyle(
                                NotificationCompat.BigTextStyle()
                                    .bigText(it.body)
                            )

                        createNotificationChannel()
                        val notificationId = 121
                        with(NotificationManagerCompat.from(this)) {
                            // notificationId is a unique int for each notification that you must define
                            if (ActivityCompat.checkSelfPermission(
                                    applicationContext,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                            }
                            notify(notificationId, builder.build())
                        }

                    }

                }
            } else {
                remoteMessage.notification?.let {
                    Log.d("MyTag", "Message Notification Body: ${it.body}")

                    val channelId = "notifications"
                    val builder = NotificationCompat.Builder(applicationContext, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(it.title)
                        .setContentText(it.body)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setStyle(
                            NotificationCompat.BigTextStyle()
                                .bigText(it.body)
                        )

                    createNotificationChannel()
                    val notificationId = 121
                    with(NotificationManagerCompat.from(this)) {
                        // notificationId is a unique int for each notification that you must define
                        if (ActivityCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.

                        }
                        notify(notificationId, builder.build())
                    }

                }
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("MyTag", "Message dfNotification Body: ${it.body}")
        }


    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val CHANNEL_ID = "notifications"
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}