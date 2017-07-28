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
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList
import android.media.RingtoneManager
import za.co.metalojiq.classfinder.someapp.activity.*
import za.co.metalojiq.classfinder.someapp.util.KtUtils
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
//      @Deprecated  val roomId: String? = remoteMessage.data["room_id"]   // TODO: should remove these
        val senderId: String? = remoteMessage.data["sender_id"]
        val isOpenByHost: String? = remoteMessage.data["is_open_by_host"]
        val search: String? = remoteMessage.data["search"]
        // extra data for the accommodations
        val roomAddress: String? = remoteMessage.data["room_address"]
        val roomPrice: String? = remoteMessage.data["room_price"]
        val roomType: String? = remoteMessage.data["room_address"]

        val for_host = remoteMessage.data["for_host"]
        val taskStackBuilder = TaskStackBuilder.create(this)
        if(hostId != null) {
            //set up a laucher here to start the chats activityv
//            val intent = Intent(this, ChatActivity::class.java)
//            intent.putExtra(AccomList.POST_INT_HOST_ID, hostId.toInt())
////            intent.putExtra(AccomList.STRING_ROOM_ADDRESS_EXTRA, roomLocation)
//            intent.putExtra(ChatActivity.SENDER_ID, senderId!!.toInt()) //cannot be null
//            intent.putExtra(ChatActivity.IS_OPEN_BY_HOST, isOpenByHost!!.toBoolean())
//            intent.putExtra(LoginActivity.LOGIN_PREF_EMAIL, remoteMessage.data["sender_email"])

            val intent = KtUtils.setIntentForChatActivity(
                    this,hostId.toInt(),
                    senderId!!.toInt(), roomAddress!!,
                    /*true*/ isOpenByHost!!.toBoolean(),
                    remoteMessage.data["sender_email"]!!,
                    roomPrice!!.toDouble(),  roomType!! )

            //TODO: replace with the utility class

            taskStackBuilder.addParentStack(ChatActivity::class.java)
            taskStackBuilder.addNextIntent(intent)
            val pendingIntent = taskStackBuilder.getPendingIntent(Random().nextInt(), PendingIntent.FLAG_UPDATE_CURRENT )
            mBuilder.setContentIntent(pendingIntent)
        } else if (for_host != null) {
            val pendingIntent = taskStackBuilder.getPendingIntent(Random().nextInt(), PendingIntent.FLAG_UPDATE_CURRENT )
            if(for_host == "no") {
                val runner = Intent(this, Runner::class.java)
                taskStackBuilder.addParentStack(Runner::class.java)
                taskStackBuilder.addNextIntent(runner)
                mBuilder.setContentIntent(pendingIntent)
            } else {
                val intent = Intent(this, HostPanel::class.java)
                taskStackBuilder.addParentStack(HostPanel::class.java)
                taskStackBuilder.addNextIntent(intent)
                mBuilder.setContentIntent(pendingIntent)
            }
        }
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(1, mBuilder.build())
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }
}
