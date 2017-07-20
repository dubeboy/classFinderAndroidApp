package za.co.metalojiq.classfinder.someapp.service

import android.app.Notification
import android.content.Intent

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.support.v4.app.NotificationCompat
import za.co.metalojiq.classfinder.someapp.R
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.util.Log
import za.co.metalojiq.classfinder.someapp.activity.ChatActivity
import za.co.metalojiq.classfinder.someapp.activity.LoginActivity
import za.co.metalojiq.classfinder.someapp.activity.MainActivity
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList
import android.media.RingtoneManager
import java.util.*


/**
 * Created by divine on 2017/07/01.
 */

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("MyFirebaseMessaging", "msg is : $remoteMessage")
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setTicker(remoteMessage.notification?.title.toString())
                .setContentTitle(remoteMessage.notification?.title.toString())
                .setContentText(remoteMessage.notification?.body.toString())
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(remoteMessage.notification?.body.toString()))

        //todo: there should be button of not available here

        val hostId: String? = remoteMessage.data["host_id"]
        val roomId: String? = remoteMessage.data["room_id"]
        val roomLocation: String? = remoteMessage.data["room_location"]
        val senderId: String? = remoteMessage.data["sender_id"]
        val isOpenByHost: String? = remoteMessage.data["is_open_by_host"]
        if(hostId != null) {
            //set up a laucher here to start the chats activityv
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(AccomList.POST_INT_HOST_ID, hostId.toInt())
            intent.putExtra(ChatActivity.ROOM_LOC, roomLocation)
            intent.putExtra(ChatActivity.SENDER_ID, senderId!!.toInt()) //cannot be null
            intent.putExtra(ChatActivity.IS_OPEN_BY_HOST, isOpenByHost!!.toBoolean())
            intent.putExtra(ChatActivity.CHAT_ROOM_ID, roomId)
            intent.putExtra(LoginActivity.LOGIN_PREF_EMAIL, remoteMessage.data["sender_email"])
            val taskStackBuilder = TaskStackBuilder.create(this)
            taskStackBuilder.addParentStack(MainActivity::class.java);
            taskStackBuilder.addNextIntent(intent)
            val pendingIntent = taskStackBuilder.getPendingIntent(Random().nextInt(), PendingIntent.FLAG_UPDATE_CURRENT )
            mBuilder.setContentIntent(pendingIntent)
        }

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(1, mBuilder.build())
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }
}
