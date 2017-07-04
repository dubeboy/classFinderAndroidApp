package za.co.metalojiq.classfinder.someapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.Builder
import za.co.metalojiq.classfinder.someapp.R
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.content.Context
import android.util.Log


/**
 * Created by divine on 2017/07/01.
 */

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d("MyFirebaseMessaging", "msg is : ${remoteMessage}")
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                .setContentTitle("You got a view from: ${remoteMessage?.from}")
                .setContentText("messages body is ${remoteMessage?.notification?.body}")

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(1, mBuilder.build());
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }
}
