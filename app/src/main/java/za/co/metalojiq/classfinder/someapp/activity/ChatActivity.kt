package za.co.metalojiq.classfinder.someapp.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import za.co.metalojiq.classfinder.someapp.R
import kotlinx.android.synthetic.main.activity_chat.*
import za.co.metalojiq.classfinder.someapp.model.ChatMessage
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList
import za.co.metalojiq.classfinder.someapp.model.User
import za.co.metalojiq.classfinder.someapp.model.UserResponse
import za.co.metalojiq.classfinder.someapp.rest.ApiClient
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface
import za.co.metalojiq.classfinder.someapp.util.KtUtils
import za.co.metalojiq.classfinder.someapp.util.Utils
import java.util.*

class ChatActivity : AppCompatActivity() {
    //todo: should have single ton that stores user state here

    val msgs: ArrayList<ChatMessage> = ArrayList()
//    lateinit var recyclerViewAdapter: ChatsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val hostUser: User = User()
        hostUser.id = intent.getIntExtra(AccomList.POST_INT_HOST_ID, 0)
        val senderUser: User = User()
        val isOpenByHost = intent.getBooleanExtra(IS_OPEN_BY_HOST, false)
        if(isOpenByHost) { //this means that this intent is opened on the Hosts phone I need the senderUser to send to
            senderUser.id = intent.getIntExtra(SENDER_ID, 0)
            Log.d(TAG, "the sender ID is: ${senderUser.id}")
        } else {
            senderUser.id = Utils.getUserId(this)
        }
        senderUser.email = Utils.getUserSharedPreferences(this).getString(LoginActivity.LOGIN_PREF_EMAIL, "") // the email belong to who ever is sending
        //todo: set a progress bar here
        getMessageFromFireBaseUser(senderUser, hostUser)
        val roomType1 = "cf_${hostUser.id}_${senderUser.id}"
        displayChatMessages(roomType1, list_of_messages, hostUser)
        fab_send.setOnClickListener {
            Log.d(TAG, "Sending msg")
            val text = input.text.toString().trim()
            Log.d(TAG, "the host information is this $hostUser")
            Log.d(TAG, "the user information is this $senderUser")
            Log.d(TAG, "the msg is $text")
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(this@ChatActivity, "Please type something", Toast.LENGTH_SHORT).show()
            } else if (hostUser.id == senderUser.id) {
                Toast.makeText(this@ChatActivity, "You cannot chat to your self boss", Toast.LENGTH_SHORT).show()
            } else {
                val chat = ChatMessage(text, senderUser.email, senderUser.id, hostUser.id)
                sendMessageToFireBaseUser(this@ChatActivity, chat, senderUser, hostUser)
                Utils.notifyHost(roomType1, hostUser.id, senderUser.id, !isOpenByHost) //who ever is on the recieving side should be the opposite of the other
                input.setText("")
            }
        }
    }

    /*sets up the recycler view*/
    fun displayChatMessages(child: String, listOfMessagesRecyclerView: RecyclerView, hostUser: User) {
        val ARG_CHAT_ROOMS = hostUser.id.toString()
        val linerLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
        val recyclerViewAdapter: FirebaseRecyclerAdapter<ChatMessage, ChatsViewHolder> =
                object : FirebaseRecyclerAdapter<ChatMessage, ChatsViewHolder> (
                        ChatMessage::class.java,
                        R.layout.list_item_chat,
                        ChatsViewHolder::class.java,
                        FirebaseDatabase.getInstance().reference.child(ARG_CHAT_ROOMS).child(child)) {

                    override fun populateViewHolder(viewHolder: ChatsViewHolder, model: ChatMessage, position: Int) {
                        Log.d(TAG, "Called man and the model is $model")
                        viewHolder.messageText.text = model.messageText
                        viewHolder.messageTime.text = DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.messageTime)
                        viewHolder.messageUser.text = model.messageUser
                    }

                    override fun onBindViewHolder(viewHolder: ChatsViewHolder, position: Int) {
                        super.onBindViewHolder(viewHolder, position)
                        Log.d(TAG, "called many times + $position")
                    }
                }

        // val recyclerViewAdapter = ChatsAdapter(msgs)
//        list_of_messages.adapter = adapter
//        list_of_messages.itemAnimator = DefaultItemAnimator()
        recyclerViewAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val numChats: Int = recyclerViewAdapter.itemCount
                val lastVisiblePosition: Int = linerLayoutManager.findLastCompletelyVisibleItemPosition()
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (numChats - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    listOfMessagesRecyclerView.scrollToPosition(positionStart);
                }
            }
        })

        listOfMessagesRecyclerView.layoutManager = linerLayoutManager
        listOfMessagesRecyclerView.adapter = recyclerViewAdapter
    }

    private fun sendMessageToFireBaseUser(context: Context, chat: ChatMessage, senderUser: User, recieverUser: User) {
        val roomType1: String = "cf_${recieverUser.id}_${senderUser.id}"
        val roomType2: String = "cf_${senderUser.id}_${recieverUser.id}"
        val ARG_CHAT_ROOMS = recieverUser.id.toString()

        val dbReference = FirebaseDatabase.getInstance().reference
        dbReference.child(ARG_CHAT_ROOMS).ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(dbError: DatabaseError?) {
                Toast.makeText(this@ChatActivity, "Failed to send msg please try again", Toast.LENGTH_LONG).show()
                Log.d(TAG, "failed to send msg please try agaain")
            }

            override fun onDataChange(dataSnapShot: DataSnapshot?) {
                if (dataSnapShot != null) {
                    if (dataSnapShot.hasChild(roomType1)) {
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
        Log.d(TAG, "Called man ")
        val roomType1: String = "cf_${recieverUser.id}_${senderUser.id}"
        val roomType2: String = "cf_${senderUser.id}_${recieverUser.id}"
        val ARG_CHAT_ROOMS = recieverUser.id.toString()

        val dbReference = FirebaseDatabase.getInstance().reference

        dbReference.child(ARG_CHAT_ROOMS).ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(this@ChatActivity, "Failed to send msg please try again", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dbSnapShot: DataSnapshot) {  //its a snapshot
                Log.d(TAG, "On Data Change")
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
                                    msgs.add(chatMessage!!)
//                                    recyclerViewAdapter.notifyDataSetChanged()
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
                                    msgs.add(chatMessage!!)
//                                    recyclerViewAdapter.notifyDataSetChanged()
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
                            if (response.body() != null) {
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
        const val IS_OPEN_BY_HOST = "isForHosT"
        const val SENDER_ID = "senderID"
        const val ROOM_LOC = "roomLoc"
    }
}

class ChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val messageText: TextView = itemView.findViewById(R.id.message_text) as TextView
    val messageUser: TextView = itemView.findViewById(R.id.message_user) as TextView
    val messageTime: TextView = itemView.findViewById(R.id.message_time) as TextView
}

