package za.co.metalojiq.classfinder.someapp.model

import java.util.*

/**
 * Created by divine on 2017/07/07.
 */

//sets up the getters and setters etc
data class ChatMessage(var messageText: String = "", var messageUser: String = "") {
    var messageTime: Long = Date().time
}