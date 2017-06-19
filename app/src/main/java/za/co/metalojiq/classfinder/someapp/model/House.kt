package za.co.metalojiq.classfinder.someapp.model

import com.google.gson.annotations.SerializedName

import java.util.ArrayList

/**
 * Created by divine on 2017/06/15.
 */

class House {
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("address")
    var address: String? = null
    @SerializedName("wifi")
    var wifi: String? = null
    @SerializedName("user_id")
    var userId: Int = 0
    @SerializedName("nsfas")
    var isNsfas: Boolean = false
    @SerializedName("common")
    var common: String? = null
    @SerializedName("prepaid_elec")
    var isPrepaidElectricity: Boolean = false

    @SerializedName("results")
    lateinit var results: ArrayList<Accommodation>

    @SerializedName("status")
    var isStatus: Boolean = false
}
