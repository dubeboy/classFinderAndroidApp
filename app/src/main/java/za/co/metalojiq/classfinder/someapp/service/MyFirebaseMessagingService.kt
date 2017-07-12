package za.co.metalojiq.classfinder.someapp.service

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
import za.co.metalojiq.classfinder.someapp.activity.MainActivity
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList


/**
 * Created by divine on 2017/07/01.
 */

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("MyFirebaseMessaging", "msg is : $remoteMessage")
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(remoteMessage.notification?.title.toString())
                .setContentText(remoteMessage.notification?.body.toString())

        val hostId: String? = remoteMessage.data["host_id"]
        val roomId: String? = remoteMessage.data["room_id"]
        val roomLocation: String? = remoteMessage.data["room_location"]
        val senderId: String? = remoteMessage.data["sender_id"]
        val isOpenByHost: String? = remoteMessage.data["is_open_by_host"]
        if(hostId != null) {
            //set up a laucher here to start the chats activityv
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(AccomList.POST_INT_HOST_ID, hostId)
            intent.putExtra(ChatActivity.ROOM_LOC, roomLocation)
            intent.putExtra(ChatActivity.SENDER_ID, senderId)
            intent.putExtra(ChatActivity.IS_OPEN_BY_HOST, isOpenByHost)
            val taskStackBuilder = TaskStackBuilder.create(this)
            taskStackBuilder.addParentStack(MainActivity::class.java);
            taskStackBuilder.addNextIntent(intent)
            val pendingIntent = taskStackBuilder.getPendingIntent(10, PendingIntent.FLAG_UPDATE_CURRENT )
            mBuilder.setContentIntent(pendingIntent)
        }

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(1, mBuilder.build())
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }
}
