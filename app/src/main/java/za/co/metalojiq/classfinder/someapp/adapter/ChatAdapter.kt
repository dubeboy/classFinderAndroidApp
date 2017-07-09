package za.co.metalojiq.classfinder.someapp.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import za.co.metalojiq.classfinder.someapp.R
import za.co.metalojiq.classfinder.someapp.adapter.ChatAdapter.ChatMessageAdapter
import za.co.metalojiq.classfinder.someapp.model.ChatMessage

/**
 * Created by divine on 2017/07/09.
 */
class ChatAdapter(val chats: ArrayList<ChatMessage>) : RecyclerView.Adapter<ChatMessageAdapter>() {

    override fun onBindViewHolder(holder: ChatMessageAdapter, position: Int) {
        holder.bind(chats.)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChatMessageAdapter {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    class ChatMessageAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text) as TextView
        val messageUser: TextView = itemView.findViewById(R.id.message_user) as TextView
        val messageTime: TextView = itemView.findViewById(R.id.message_time) as TextView
        fun bind(chats: ChatMessage) {

        }
    }

}