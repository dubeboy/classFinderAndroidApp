package za.co.metalojiq.classfinder.someapp.util

import android.app.Activity
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import za.co.metalojiq.classfinder.someapp.R
import za.co.metalojiq.classfinder.someapp.activity.ChatsViewHolder
import za.co.metalojiq.classfinder.someapp.adapter.ChatsAdapter
import za.co.metalojiq.classfinder.someapp.model.ChatMessage
import za.co.metalojiq.classfinder.someapp.model.ChatsList

/**
 * Created by divine on 2017/07/10.
 */

//singleton class
object KtUtils {
    val TAG = "__KUtils__"

    fun displayChatMessages(uniqueHostAndStudentRoomId: String,
                            listOfMessagesRecyclerView: RecyclerView,
                            context: Context,
                            hostUserId: Int,
                            studentId: Int, onPopulated: (isPopulated: Boolean) -> Unit) {

        Log.d(TAG, "the uniques $uniqueHostAndStudentRoomId")

        Log.d(TAG, "we are int the kUtils: KtUtils()")

        val linerLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
        val hostDatabaseReference = FirebaseDatabase.getInstance().reference.child(hostUserId.toString())


        val chatListArrayList: ArrayList<ChatMessage> = arrayListOf()
        hostDatabaseReference.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(dbError: DatabaseError?) {
                Log.d(TAG, "oops an error happend here is the error $dbError")
            }

            override fun onDataChange(dbSnapShot: DataSnapshot?) {
                Log.d(TAG, "the dataSnap is: $dbSnapShot")
//                Log.d(TAG, "__CHILD_the dataSnap and the children is: ${dbSnapShot?.children}")
                dbSnapShot?.children?.forEach { chatsList ->
                    val chatId = chatsList.key  //TODO HAVE TO PASS IT FORWARD
                    val chatMessage = chatsList.children?.first()?.getValue(ChatMessage::class.java)
                    if (chatMessage != null)
                        chatListArrayList.add(chatMessage)
                }
            }
        })

        val chatsAdapter: ChatsAdapter = ChatsAdapter(chatListArrayList, {

        })
//        val recyclerViewAdapter: FirebaseRecyclerAdapter<ChatsList, ChatsViewHolder> =
//                object : FirebaseRecyclerAdapter<ChatsList, ChatsViewHolder>(ChatsList::class.java,
//                                                                                R.layout.list_item_chat,
//                                                                                ChatsViewHolder::class.java,
//                                                                                hostDatabaseReference) {
//
//                    override fun populateViewHolder(viewHolder: ChatsViewHolder, model: ChatsList, position: Int) {
//                        Log.d(TAG, "Called man and the model is $model")
//                        viewHolder.messageText.text = model.chatsList!!.messageText
//                        viewHolder.messageTime.text = DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.chatsList!!.messageTime)
//                        viewHolder.messageUser.text = model.chatsList!!.messageUser
//                    }
//
//                    override fun onBindViewHolder(viewHolder: ChatsViewHolder, position: Int) {
//                        super.onBindViewHolder(viewHolder, position)
//                        Log.d(TAG, "called many times + $position")
//                    }
//
//                }
//        recyclerViewAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                super.onItemRangeInserted(positionStart, itemCount)
//                val numChats: Int = recyclerViewAdapter.itemCount
//                val lastVisiblePosition: Int = linerLayoutManager.findLastCompletelyVisibleItemPosition()
//                if (lastVisiblePosition == -1 ||
//                        (positionStart >= (numChats - 1) &&
//                                lastVisiblePosition == (positionStart - 1))) {
//                    listOfMessagesRecyclerView.scrollToPosition(positionStart);
//                }
//            }
//        })
//        listOfMessagesRecyclerView.layoutManager = linerLayoutManager
//        listOfMessagesRecyclerView.adapter = recyclerViewAdapter
//        onPopulated(recyclerViewAdapter.itemCount > 0)
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
}