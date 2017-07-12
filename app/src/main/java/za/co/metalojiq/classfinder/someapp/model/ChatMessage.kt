package za.co.metalojiq.classfinder.someapp.model

import java.io.Serializable
import java.util.*

/**
 * Created by divine on 2017/07/07.
 */

class ChatMessage : Serializable {
    var messageTime: Long = Date().time
    var messageText: String? = null
    var messageUser: String? = null
    var senderId: Int = 0
    var receiverId: Int = 0

    constructor(messageText: String, messageUser: String, senderId: Int, receiverId: Int) {
        this.messageText = messageText
        this.messageUser = messageUser
        this.senderId = senderId
        this.receiverId = receiverId
    }

    constructor() {}

    override fun toString(): String {
        return "{$messageText, : $messageUser}"
    }
}