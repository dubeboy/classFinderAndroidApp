package za.co.metalojiq.classfinder.someapp.util

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.util.Log
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.FirebaseDatabase
import za.co.metalojiq.classfinder.someapp.R
import za.co.metalojiq.classfinder.someapp.activity.ChatActivity
import za.co.metalojiq.classfinder.someapp.activity.ChatsViewHolder
import za.co.metalojiq.classfinder.someapp.model.ChatMessage

/**
 * Created by divine on 2017/07/10.
 */
 class KtUtils {
    companion object {
        const val TAG = "KUtils"

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
                            Log.d(ChatActivity.TAG, "Called man and the model is $model")
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
    }
    // not really useful!! can be done with lambdas yoh!!!!
    interface OnItemClickListener {
        //fun onItemClicked(itemView: View)
    }
}