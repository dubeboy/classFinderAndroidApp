package za.co.metalojiq.classfinder.someapp.activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.database.FirebaseDatabase
import za.co.metalojiq.classfinder.someapp.R
import kotlinx.android.synthetic.main.activity_chat.*
import za.co.metalojiq.classfinder.someapp.model.ChatMessage
import za.co.metalojiq.classfinder.someapp.util.Utils
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList
import za.co.metalojiq.classfinder.someapp.model.User
import za.co.metalojiq.classfinder.someapp.model.UserResponse
import za.co.metalojiq.classfinder.someapp.rest.ApiClient
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface
import java.util.*

class ChatActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        fab_send.setOnClickListener {
            Log.d(TAG, "Sending msg")
            val text = input.text.toString()
            FirebaseDatabase
                    .getInstance()
                    .reference
                    .push()
                    .setValue(ChatMessage(text, Utils.getUserSharedPreferences(this@ChatActivity)
                            .getString(LoginActivity.LOGIN_PREF_EMAIL, "")))
            input.setText("")
        }

        displayChatMessages()
    }

    fun displayChatMessages() {
        val adapter: FirebaseListAdapter<ChatMessage> =
                object: FirebaseListAdapter<ChatMessage>(this@ChatActivity,
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

    private fun sendMessageToFireBaseUser(context: Context, chat: ChatMessage, recieverFBToken: String) {
        val roomType1: String = "${null}_${Utils.getUserId(context)}"
    }

    fun getHostUserDetails(onUserResponse: (user: User?) -> Unit) { //allowing null probably a bad idea but ...
        val hostId: String = intent.getStringExtra(AccomList.POST_INT_HOST_ID)
        val client: ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        client.getUser(hostId)
                .enqueue(object: Callback<UserResponse?> {

                    override fun onFailure(call: Call<UserResponse?>?, t: Throwable?) {
                        onUserResponse(null)
                        Toast.makeText(this@ChatActivity, "Please connect to the internet tu!", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<UserResponse?>?, response: Response<UserResponse?>) {
                        if(response.body() != null){
                            if(response.body()?.isStatus!!) {
                                onUserResponse( response.body()?.user!!)
                                return
                            }
                        }
                        onUserResponse(null)
                    }
                })
    }

    companion object {
        const val TAG = "ChatActivity"
    }
}
