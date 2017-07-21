package za.co.metalojiq.classfinder.someapp.activity

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.android.synthetic.main.activity_add_house.*


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
    private var location: String = ""
    private var nearTo = ""
//    private var tvAuck: TextView? = null
    private var ckBox: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ckBox = findViewById(R.id.ck_box_search_nsfas) as CheckBox

        Log.d(TAG, "Created the search activity")

//        locationSpinner = setupSpinner(this, R.id.search_location_spinner, R.array.locations_array)
        roomTypeSpinner = setupSpinner(this, R.id.search_spinner_room_type, R.array.room_type)
        auckAreaSpinner = setupSpinner(this, R.id.search_spinner_auck_areas, R.array.auck_areas)


//        locationSpinner!!.onItemSelectedListener = Utils.LocationItemListener(auckAreaSpinner)
        priceFrom = findViewById(R.id.search_price_from) as EditText
        priceTo = findViewById(R.id.search_price_to) as EditText

        val autocompleteFragment = fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment
        autocompleteFragment.setHint("Johannesburg or Brixton etc")
        autocompleteFragment.setBoundsBias(LatLngBounds(LatLng(-34.833232300000006,16.4535919),
                LatLng(-22.1254241,32.8909911)))
        val typeFilter: AutocompleteFilter = AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .setCountry("ZA")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                .build()
        autocompleteFragment.setFilter(typeFilter)
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.d(TAG, "The pleace object is: " + place)
                location = place.name.toString()
                autocompleteFragment.setText(place.address.toString())
            }

            override fun onError(status: Status) {
                Toast.makeText(this@Search, "sorry something went wrong, please try again", Toast.LENGTH_LONG).show()
                Log.d(TAG, "An error occurred: " + status)
            }
        })


        val autoPlaceNearTocompleteFragment = fragmentManager.findFragmentById(R.id.place_nearto_autocomplete_fragment) as PlaceAutocompleteFragment
        autoPlaceNearTocompleteFragment.setHint("Places of Interest")
        val typeFilter2: AutocompleteFilter = AutocompleteFilter.Builder()  // todo: bad can be done better all of this aouto complete code
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .setCountry("ZA")
                .build()
        autoPlaceNearTocompleteFragment.setFilter(typeFilter2)
        autoPlaceNearTocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val name: String = place.name.toString()
                nearTo = name
                Log.d(TAG, "the place data is:  $place")
            }

            override fun onError(status: Status) {
                Toast.makeText(this@Search, "sorry something went wrong, please try again", Toast.LENGTH_LONG).show()
                Log.d(TAG, "An error occurred on nearby: " + status)
            }
        })

    }

    fun search(view: View) {
        dialog = ProgressDialog.show(this@Search, "",
                "Finding your perfect accommodation please wait....", true)
        val nsfas = ckBox!!.isChecked

//        val location = locationSpinner!!.selectedItem as String
        Log.d(TAG, "location is")
        if(location.isBlank()) {
            Toast.makeText(this, "Please select a location", Toast.LENGTH_LONG).show()
            dialog!!.hide()
            return
        }


        val roomType = roomTypeSpinner!!.selectedItem as String

        val priceT = Integer.valueOf(if (TextUtils.isEmpty(priceTo!!.text.toString())) "0" else priceTo!!.text.toString())!!
        val priceF = Integer.valueOf(if (TextUtils.isEmpty(priceFrom!!.text.toString())) "0" else priceFrom!!.text.toString())!!

        //put this in a runner so that we will just get the result from thread
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val apiCall = apiService.searchAccommodations(location, nearTo, roomType, priceF, priceT, nsfas)

        Log.d(TAG, apiCall.request().toString())
        val intent = Intent(this, SearchResults::class.java)
        apiCall.enqueue(object : Callback<AccommodationResponse> {
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
        private val TAG = "__Search__"
        val INTENT_RESPONSE_EXTRA = TAG + ".response"
    }
}
