package za.co.metalojiq.classfinder.someapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.annotation.NonNull
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_host_panel.*
import za.co.metalojiq.classfinder.someapp.R
import za.co.metalojiq.classfinder.someapp.activity.ChatActivity
import za.co.metalojiq.classfinder.someapp.activity.LoginActivity
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList
import za.co.metalojiq.classfinder.someapp.adapter.ChatsAdapter
import za.co.metalojiq.classfinder.someapp.model.ChatMessage
import java.lang.IllegalArgumentException

/**
 * Created by divine on 2017/07/10.
 */

//singleton class
object KtUtils {
    val TAG = "__KUtils__"


    // should also get the recycler view here as well
    fun displayChatMessages(uniqueHostAndStudentRoomId: String,
                            listOfMessagesRecyclerView: RecyclerView,
                            context: Context,
                            hostUserId: Int,
                            swipeRefreshLayout: SwipeRefreshLayout) {

        Log.d(TAG, "the uniques $uniqueHostAndStudentRoomId")
        Log.d(TAG, "we are int the kUtils: KtUtils()")

        val linerLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
        val hostDatabaseReference = FirebaseDatabase.getInstance().reference.child(hostUserId.toString())

        val chatListArrayList: ArrayList<ChatMessage> = arrayListOf()
        val chatsAdapter: ChatsAdapter = ChatsAdapter(chatListArrayList, object : ChatsAdapter.OnItemClick {
            override fun onItemClick(chat: ChatMessage) {
                val intentForChatActivity = setIntentForChatActivity(
                        context,
                        chat.receiverId,
                        chat.senderId,
                        chat.houseAddress,
                        true,
                        chat.messageUser,
                        chat.price,
                        chat.roomType)

                Toast.makeText(context, "Opening Chat, Loading messages...", Toast.LENGTH_LONG).show()
                context.startActivity(intentForChatActivity)
            }
        })
        hostDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(dbError: DatabaseError?) {
                Toast.makeText(context, "Oops an error happen, please try again", Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
                Log.d(TAG, "oops an error happend here is the error: $dbError")
            }

            override fun onDataChange(dbSnapShot: DataSnapshot?) {
                Log.d(TAG, "the dataSnap is: $dbSnapShot")
                // i get all the msgs at once meaning that i have to clear the chats
                chatListArrayList.clear()
//                Log.d(TAG, "__CHILD_the dataSnap and the children is: ${dbSnapShot?.children}")
                dbSnapShot?.children?.forEach { chatsList ->
                    val chatId = chatsList.key  //TODO HAVE TO PASS IT FORWARD
                    val chatMessage = chatsList.children?.last()?.getValue(ChatMessage::class.java)
                    if (chatMessage != null) {
                        chatListArrayList.add(chatMessage)
                    }
                }
                chatsAdapter.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false
            }
        })

        listOfMessagesRecyclerView.adapter = chatsAdapter
        listOfMessagesRecyclerView.layoutManager = linerLayoutManager
    }


    fun setIntentForChatActivity(context: Context,
                                 hostId: Int,
                                 @NonNull senderId: Int, //should not be null please
                                 roomAddress: String,
                                 isOpenByHost: Boolean,
                                 senderEmail: String,
                                 price: Double,
                                 roomType: String): Intent {

        if (senderId <= 0)
            throw IllegalArgumentException("please provide the right sender id it has to be > 0")

        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra(AccomList.POST_INT_HOST_ID, hostId)
        intent.putExtra(ChatActivity.SENDER_ID, senderId) //cannot be null
        intent.putExtra(ChatActivity.IS_OPEN_BY_HOST, isOpenByHost)
        intent.putExtra(LoginActivity.LOGIN_PREF_EMAIL, senderEmail)
        intent.putExtra(AccomList.STRING_ROOM_ADDRESS_EXTRA, roomAddress)
        intent.putExtra(AccomList.DOUBLE_PRICE_EXTRA, price)
        intent.putExtra(AccomList.STRING_ROOM_TYPE_EXTRA, roomType)
        return intent
    }

    fun signUserInToFirebase(context: Context, jwtToken: String, onResults: (status: Boolean) -> Unit) {
        val mAuth = FirebaseAuth.getInstance()

        Log.d(TAG, "The going to firebase now")
        mAuth.signInWithCustomToken(jwtToken)
                .addOnCompleteListener(context as Activity, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCustomToken:success")
                        val user = mAuth.currentUser
                        Log.d(TAG, "the user information is: " + if (user == null) "__null__" else user.getUid())
                        onResults(true)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCustomToken:failure", task.exception)
                        Toast.makeText(context as Activity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        onResults(false)
                    }
                })
    }


    fun getView(context: Activity): View = context.findViewById(android.R.id.content)
}