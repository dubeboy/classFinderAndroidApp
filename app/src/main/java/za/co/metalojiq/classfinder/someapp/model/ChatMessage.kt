package za.co.metalojiq.classfinder.someapp.model

import java.io.Serializable
import java.util.*

/**
 * Created by divine on 2017/07/07.
 */

class ChatMessage : Serializable {
    var messageTime: Long = Date().time
    var messageText: String = ""
    var messageUser: String = ""
    var senderId: Int = 0
    var price: Double = 0.0
    var roomType: String = ""
    var houseAddress: String = ""

    var receiverId: Int = 0  //this should be the host ID::

    constructor(messageText: String,
                messageUser: String,
                senderId: Int,
                receiverId: Int,
                price: Double,
                roomType: String, houseAddress: String) {

        this.messageText = messageText
        this.messageUser = messageUser
        this.senderId = senderId
        this.receiverId = receiverId
        this.price = price
        this.roomType = roomType
        this.houseAddress = houseAddress
    }

    constructor() //default constructor for firebase

    override fun toString(): String {
        return "{$messageText, : $messageUser}"
    }
}