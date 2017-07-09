package za.co.metalojiq.classfinder.someapp.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import za.co.metalojiq.classfinder.someapp.R
import za.co.metalojiq.classfinder.someapp.adapter.ChatsAdapter.ChatMessageViewHolder
import za.co.metalojiq.classfinder.someapp.model.ChatMessage
import android.text.format.DateFormat


/**
 * Created by divine on 2017/07/09.
 */
class ChatsAdapter(val chats: ArrayList<ChatMessage>) : RecyclerView.Adapter<ChatMessageViewHolder>() {

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat, parent ,false)
        return ChatMessageViewHolder(view)
    }

    override fun getItemCount(): Int = chats.size

    class ChatMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text) as TextView
        val messageUser: TextView = itemView.findViewById(R.id.message_user) as TextView
        val messageTime: TextView = itemView.findViewById(R.id.message_time) as TextView

        fun bind(chats: ChatMessage) {
            messageText.text = chats.messageText
            messageUser.text = chats.messageUser
            messageTime.text = DateFormat.format("dd-MM-yyyy (HH:mm:ss)", chats.messageTime)
        }
    }

    fun addAll(chatsMessages: ArrayList<ChatMessage>) {
        chats.addAll(chatsMessages)
    }

    fun clear() {
        chats.clear()
    }

}