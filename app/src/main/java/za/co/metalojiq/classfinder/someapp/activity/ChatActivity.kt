package za.co.metalojiq.classfinder.someapp.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import com.firebase.ui.database.FirebaseListAdapter
import za.co.metalojiq.classfinder.someapp.R
import kotlinx.android.synthetic.main.activity_chat.*
import za.co.metalojiq.classfinder.someapp.model.ChatMessage
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList
import za.co.metalojiq.classfinder.someapp.model.User
import za.co.metalojiq.classfinder.someapp.model.UserResponse
import za.co.metalojiq.classfinder.someapp.rest.ApiClient
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface
import za.co.metalojiq.classfinder.someapp.util.Utils

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var hostUser: User? = User()
        val senderUser: User = User()
        senderUser.id = Utils.getUserId(this)
        //todo: set a progress bar here
        getHostUserDetails {
           when(it) { //looks nicer
               null -> {
                   hostUser = null
               }
               else -> {
                   hostUser = it
                   getMessageFromFireBaseUser(senderUser, hostUser!!)
               }
           }
        }

        fab_send.setOnClickListener {
            Log.d(TAG, "Sending msg")
            val text = input.text.toString()
            if (hostUser != null) {
                val chat = ChatMessage(text)
                sendMessageToFireBaseUser(this@ChatActivity, chat, senderUser, hostUser!!)
                input.setText("")
            } else {
                Toast.makeText(this@ChatActivity,
                        "Fails to get hostUser data please make you are connected to the internet, will retry in 30 seconds", Toast.LENGTH_LONG).show()
            }
        }

        displayChatMessages()
    }

    fun displayChatMessages() {
        val adapter: FirebaseListAdapter<ChatMessage> =
                object : FirebaseListAdapter<ChatMessage>(this@ChatActivity,
                        ChatMessage::class.java, R.layout.list_item_chat, FirebaseDatabase.getInstance().reference) {
                    override fun populateView(v: View, model: ChatMessage, position: Int) {
                        val messageText = v.findViewById(R.id.message_text) as TextView
                        val messageUser = v.findViewById(R.id.message_user) as TextView
                        val messageTime = v.findViewById(R.id.message_time) as TextView

                        // Set their text
                        messageText.text = model.messageText
                        messageUser.text = model.messageUser

                        // Format the date before showing it
                        messageTime.text = DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                                model.messageTime)
                    }
                }
        list_of_messages.adapter = adapter
    }

    private fun sendMessageToFireBaseUser(context: Context, chat: ChatMessage, senderUser: User, recieverUser: User) {
        val roomType1: String = "cf_${recieverUser.id}_${senderUser.id}"
        val roomType2: String = "cf_${senderUser.id}_${recieverUser.id}"

        val dbReference = FirebaseDatabase.getInstance().reference
        dbReference.child(ARG_CHAT_ROOMS).ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(dbError: DatabaseError?) {
                Toast.makeText(this@ChatActivity, "Failed to send msg please try again", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapShot: DataSnapshot?) {
                if (dataSnapShot != null) {
                    if (dataSnapShot.hasChild(roomType1)) {
                        /*if the room exists okay create a new field(key) = timestamp -> always create a new one therefore creating log of chat neat!! */

                        Log.e(TAG, "send msg to fb user $roomType1 exists")
                        dbReference
                                .child(ARG_CHAT_ROOMS)
                                .child(roomType1)
                                .child(chat.messageTime.toString()).setValue(chat)
                    } else if (dataSnapShot.hasChild(roomType2)) {
                        Log.e(TAG, "send msg to fb user $roomType2 exists")
                        dbReference
                                .child(ARG_CHAT_ROOMS)
                                .child(roomType2)
                                .child(chat.messageTime.toString()).setValue(chat)
                    } else {
                        dbReference
                                .child(ARG_CHAT_ROOMS)
                                .child(roomType1)
                                .child(chat.messageTime.toString()).setValue(chat)
                    }
                }
            }
        })

    }

    private fun getMessageFromFireBaseUser(senderUser: User, recieverUser: User) {
        val roomType1: String = "cf_${recieverUser.id}_${senderUser.id}"
        val roomType2: String = "cf_${senderUser.id}_${recieverUser.id}"

        val dbReference = FirebaseDatabase.getInstance().reference

        dbReference.child(ARG_CHAT_ROOMS).ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(this@ChatActivity, "Failed to send msg please try again", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dbSnapShot: DataSnapshot) {  //its a snapshot
                if (dbSnapShot.hasChild(roomType1)) {
                    Log.e(TAG, "geting msg form rooom $roomType1")

                    FirebaseDatabase
                            .getInstance()
                            .getReference(ARG_CHAT_ROOMS)
                            .child(roomType1)
                            .addChildEventListener(object : ChildEventListener {
                                override fun onCancelled(p0: DatabaseError?) {
                                    Log.e(TAG, "sorry could not get msg $p0")

                                }

                                override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                                }

                                override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                                }

                                override fun onChildAdded(dataSnapShot: DataSnapshot, s: String?) {
                                    val chatMessage: ChatMessage? = dataSnapShot.getValue(ChatMessage::class.java)
                                    Log.d(TAG, "The msg for room 1 is: $chatMessage")
                                }

                                override fun onChildRemoved(p0: DataSnapshot?) {
                                }
                            })
                } else if (dbSnapShot.hasChild(roomType2)) {
                    FirebaseDatabase
                            .getInstance()
                            .getReference(ARG_CHAT_ROOMS)
                            .child(roomType2)
                            .addChildEventListener(object : ChildEventListener {
                                override fun onCancelled(p0: DatabaseError?) {
                                    Log.e(TAG, "sorry could not get msg $p0")

                                }

                                override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                                }

                                override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                                }

                                override fun onChildAdded(dataSnapShot: DataSnapshot, s: String?) {
                                    val chatMessage: ChatMessage? = dataSnapShot.getValue(ChatMessage::class.java)
                                    Log.d(TAG, "The msg room 2 is: $chatMessage")
                                }

                                override fun onChildRemoved(p0: DataSnapshot?) {
                                }
                            })
                }
            }
        })
    }

    private fun getHostUserDetails(onUserResponse: (user: User?) -> Unit) { //allowing null probably a bad idea but ...
        val hostId: String = intent.getIntExtra(AccomList.POST_INT_HOST_ID, 0).toString()
        val client: ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        client.getUser(hostId)
                .enqueue(object : Callback<UserResponse?> {

                    override fun onFailure(call: Call<UserResponse?>?, t: Throwable?) {
                        onUserResponse(null)
                        Toast.makeText(this@ChatActivity, "Please connect to the internet tu!", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<UserResponse?>?, response: Response<UserResponse?>) {
                        if (response.body() != null) {
                            if (response.body()?.isStatus!!) {
                                onUserResponse(response.body()?.user!!)
                                return
                            }
                        }
                        onUserResponse(null)
                    }
                })
    }

    companion object {
        const val TAG = "ChatActivity"
        const val ARG_CHAT_ROOMS = "chat_rooms"
    }
}
