package za.co.metalojiq.classfinder.someapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

import java.util.ArrayList

/**
 * Created by divine on 2017/06/15.
 */

//maybe should have been a data class!:todo
class House: Serializable {
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
    var results: ArrayList<Accommodation>? = null
    @SerializedName("status")
    var isStatus: Boolean = false
    @SerializedName("city")
    var city: String? = null
    @SerializedName("location")
    var location: String? = null
    @SerializedName("pictures")
    var pictures: List<Picture>? = null
}
