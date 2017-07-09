package za.co.metalojiq.classfinder.someapp.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.*

/**
 * Created by divine on 2017/07/07.
 */

//sets up the getters and setters etc
class ChatMessage : Serializable {
    var messageTime: Long = Date().time
    var messageText: String? = null
    var messageUser: String? = null

    constructor(messageText: String, messageUser: String) {
        this.messageText = messageText
        this.messageUser = messageUser
    }

    constructor() {}

    override fun toString(): String {
        return "{$messageText, : $messageUser}"
    }
}