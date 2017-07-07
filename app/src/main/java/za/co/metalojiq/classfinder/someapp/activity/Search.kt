package za.co.metalojiq.classfinder.someapp.activity

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.metalojiq.classfinder.someapp.R
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList
import za.co.metalojiq.classfinder.someapp.model.AccommodationResponse
import za.co.metalojiq.classfinder.someapp.rest.ApiClient
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface
import za.co.metalojiq.classfinder.someapp.util.Utils

import za.co.metalojiq.classfinder.someapp.util.Utils.*

class Search : AppCompatActivity() {

    private var dialog: ProgressDialog? = null

    enum class INTENT_RESPONSE {
        SUCCESS, FAILURE
    }


    private var auckAreaSpinner: Spinner? = null
    private var roomTypeSpinner: Spinner? = null
    private var locationSpinner: Spinner? = null
    private var priceFrom: EditText? = null
    private var priceTo: EditText? = null
//    private var tvAuck: TextView? = null
    private var ckBox: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
//        tvAuck = findViewById(R.id.search_tv_auck_areas) as TextView
        ckBox = findViewById(R.id.ck_box_search_nsfas) as CheckBox

        locationSpinner = setupSpinner(this, R.id.search_location_spinner, R.array.locations_array)
        roomTypeSpinner = setupSpinner(this, R.id.search_spinner_room_type, R.array.room_type)
        auckAreaSpinner = setupSpinner(this, R.id.search_spinner_auck_areas, R.array.auck_areas)


//        locationSpinner!!.onItemSelectedListener = Utils.LocationItemListener(auckAreaSpinner)
        priceFrom = findViewById(R.id.search_price_from) as EditText
        priceTo = findViewById(R.id.search_price_to) as EditText
    }

    fun search(view: View) {
        dialog = ProgressDialog.show(this@Search, "",
                "Finding your perfect accommodation please wait....", true)
        var auckArea = ""
        val nsfas = ckBox!!.isChecked

        val location = locationSpinner!!.selectedItem as String

//        if (location == LOCATIONS[0]) {
//            val rawArea = auckAreaSpinner!!.selectedItem as String
//           // auckArea = AUCK_AREA_PREFIX + rawArea //YAK MAN bad code need better thought man.
//        }

        val roomType = roomTypeSpinner!!.selectedItem as String

        val priceT = Integer.valueOf(if (TextUtils.isEmpty(priceTo!!.text.toString())) "0" else priceTo!!.text.toString())!!
        val priceF = Integer.valueOf(if (TextUtils.isEmpty(priceFrom!!.text.toString())) "0" else priceFrom!!.text.toString())!!

        //put this in a runner so that we will just get the result from thread
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.searchAccommodations(location, roomType, priceF, priceT, nsfas)

        Log.d(TAG + "dsldsdsadsd request", call.request().toString())
        val intent = Intent(this, SearchResults::class.java)
        call.enqueue(object : Callback<AccommodationResponse> {
            override fun onResponse(call: Call<AccommodationResponse>, response: Response<AccommodationResponse>) {
                if (response.body() != null) {
                    intent.putExtra(AccomList.ACCOM_BUNDLE_KEY, response.body()!!.results)
                    intent.putExtra(INTENT_RESPONSE_EXTRA, INTENT_RESPONSE.SUCCESS)
                    dialog!!.hide()
                    startActivity(intent)
                } else {
                    makeToast("Nothing was found!", this@Search)
                }
                dialog!!.dismiss()
            }

            override fun onFailure(call: Call<AccommodationResponse>, t: Throwable) {
                Log.e(TAG + " RRRRR", t.toString())
                intent.putExtra(INTENT_RESPONSE_EXTRA, INTENT_RESPONSE.FAILURE)
                startActivity(intent)
                dialog!!.dismiss()
            }
        })
    }

    companion object {
        private val TAG = Search::class.java.simpleName
        val INTENT_RESPONSE_EXTRA = TAG + ".response"
    }
}
