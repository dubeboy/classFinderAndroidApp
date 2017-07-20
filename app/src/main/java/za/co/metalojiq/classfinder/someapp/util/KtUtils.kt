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

import com.google.firebase.database.FirebaseDatabase
import za.co.metalojiq.classfinder.someapp.R
import za.co.metalojiq.classfinder.someapp.activity.ChatActivity
import za.co.metalojiq.classfinder.someapp.activity.ChatsViewHolder
import za.co.metalojiq.classfinder.someapp.model.ChatMessage

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

        Log.d(TAG, "we are int the kUtils: KtUtils()")

        val linerLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
        val recyclerViewAdapter: FirebaseRecyclerAdapter<ChatMessage, ChatsViewHolder> =
                object : FirebaseRecyclerAdapter<ChatMessage, ChatsViewHolder>(
                        ChatMessage::class.java,
                        R.layout.list_item_chat,
                        ChatsViewHolder::class.java,
                        FirebaseDatabase.getInstance().reference.child(hostUserId.toString()).child(uniqueHostAndStudentRoomId)) {

                    override fun populateViewHolder(viewHolder: ChatsViewHolder, model: ChatMessage, position: Int) {
                        Log.d(TAG, "Called man and the model is $model")
                        viewHolder.messageText.text = model.messageText
                        viewHolder.messageTime.text = DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.messageTime)
                        viewHolder.messageUser.text = model.messageUser
                    }

                    override fun onBindViewHolder(viewHolder: ChatsViewHolder, position: Int) {
                        super.onBindViewHolder(viewHolder, position)
                        Log.d(ChatActivity.TAG, "called many times + $position")
                    }

                }
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
        onPopulated(recyclerViewAdapter.itemCount > 0)

    }

    // not really useful!! can be done with lambdas yoh!!!!
    interface OnItemClickListener {
        //fun onItemClicked(itemView: View)
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