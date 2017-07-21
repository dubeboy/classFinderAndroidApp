package za.co.metalojiq.classfinder.someapp.activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.widget.*
import android.widget.Toast.*
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.metalojiq.classfinder.someapp.R
import za.co.metalojiq.classfinder.someapp.model.House
import za.co.metalojiq.classfinder.someapp.rest.ApiClient
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface
import za.co.metalojiq.classfinder.someapp.util.Utils
import kotlinx.android.synthetic.main.activity_add_house.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import za.co.metalojiq.classfinder.someapp.activity.fragment.HouseActivityFragment.Companion.HOUSE_ID
import java.io.File
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.pchmn.materialchips.model.ChipInterface


//todo: should use chips for common areas
class AddHouseActivity : AppCompatActivity(), Utils.OnImagesSelected {


    private lateinit var progressDialog: ProgressDialog
    private lateinit var bitmaps: Array<Bitmap>
    private lateinit var imagesUris: Array<String>
    private lateinit var imagesContainer: LinearLayout
    private var mLocationPermissionGranted: Boolean = false
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 1000
    //    private val nearByLocations: ArrayList<String> = ArrayList(10)
    private var hasSelectedSomething: Boolean = false
    private var inputNearByFilled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_house)
        title = "Add House"
        val userId = Utils.getUserId(this)
        Log.d(TAG, "$TAG the user id is: $userId")
        val etCommon = findViewById(R.id.input_common) as EditText
        //val etLocation = findViewById(R.id.input_location) as EditText
//        val etCity = findViewById(R.id.input_city) as EditText
        val ckNSFAS = findViewById(R.id.checkbox_nsfas) as CheckBox
        val ckWifi = findViewById(R.id.checkbox_wifi) as CheckBox
        val ckPrepaid = findViewById(R.id.checkbox_prepaid_elec) as CheckBox
        val btnAddHouse = findViewById(R.id.btn_add_house) as Button

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Adding your house to classfinder, please wait.")
        imagesContainer = newImagesHorizontalScroll
        requestFineLocation()  //get the permission to accces where the user is cool!!

        var address = ""


        val autocompleteFragment = fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment
        autocompleteFragment.setHint("Enter Address")
        autocompleteFragment.setBoundsBias(LatLngBounds(LatLng(-34.833232300000006, 16.4535919),
                LatLng(-22.1254241, 32.8909911)))
        val autoCompTypeFilter = AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setCountry("ZA")
                .build()
        autocompleteFragment.setFilter(autoCompTypeFilter)
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                address = place.address.toString()
                autocompleteFragment.setText(place.address.toString())
                Log.d(TAG, "Place: " + place)
                hasSelectedSomething = true
            }

            override fun onError(status: Status) {
                Toast.makeText(this@AddHouseActivity, "sorry something went wrong, please try again", Toast.LENGTH_LONG).show()
                Log.d(TAG, "An error occurred: " + status)
            }
        })



        val autoPlaceNearTocompleteFragment = fragmentManager.findFragmentById(R.id.place_nearto_autocomplete_fragment) as PlaceAutocompleteFragment
        autoPlaceNearTocompleteFragment.setHint("Enter near by places")
        val typeFilter: AutocompleteFilter = AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .setCountry("ZA")
                .build()
        autoPlaceNearTocompleteFragment.setFilter(typeFilter)
        var chipId = 0;
        autoPlaceNearTocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val name: String = place.name.toString()
                chipId += 1
                chips_input.addChip(NearToChip(name, chipId, place.address.toString()))
                inputNearByFilled = true
            }

            override fun onError(status: Status) {
                Toast.makeText(this@AddHouseActivity, "sorry something went wrong, please try again", Toast.LENGTH_LONG).show()
                Log.d(TAG, "An error occurred on nearby: " + status)
            }
        })

        btnAddHouse.setOnClickListener({
            if (validate(userId, address, hasSelectedSomething, inputNearByFilled)) {
                val selectedChipList = chips_input.selectedChipList
                var nearBy: String = ""
                selectedChipList.forEach {
                    if (nearBy.isEmpty()) {
                        nearBy += it.label
                    } else {
                        nearBy += "," + it.label
                    }
                }
                Log.d(TAG, "the near bys are $nearBy")
                saveHouse(userId,
                        address,
                        etCommon.text.toString(),
                        nearBy,
                        ckNSFAS.isChecked,
                        ckWifi.isChecked,
                        ckPrepaid.isChecked)
            } else {
                Toast.makeText(this@AddHouseActivity, "Please Fill in both forms", Toast.LENGTH_LONG).show()
            }
        })

        newBtnAddImages.setOnClickListener({
            Utils.launchImagesPicker(this@AddHouseActivity, supportFragmentManager, imagesContainer, this@AddHouseActivity)
        })
    }

    private fun requestFineLocation() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
        // A step later in the tutorial adds the code to get the device location.
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
    }

    //Function to persist the accommodation owners house
    private fun saveHouse(userId: Int, address: String,
                          common: String,
                          nearTo: String,
                          nsfas: Boolean,
                          wifi: Boolean,
                          prepaid: Boolean) {

        if (bitmaps.isEmpty() && imagesUris.isEmpty()) {
            Toast.makeText(this, "Please at least select one image", Toast.LENGTH_LONG).show()
            return
        }
        val multiPartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (imageUri in imagesUris) {
            val file = File(imageUri)
            val mime = Utils.getMimeType(imageUri)
            Log.d("MIME", "the mime is " + mime)
            val reqFile = RequestBody.create(MediaType.parse(mime), file)
            //                 MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), reqFile);
            multiPartBuilder.addFormDataPart("images[]", file.getName(), reqFile)
        }
        val body: MultipartBody = multiPartBuilder.build()


        progressDialog.show() // show the dialog
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val apiCaller = apiService.postHouse(userId, address, nearTo, common, nsfas, wifi, prepaid, body.parts())

        apiCaller.enqueue(object : Callback<House?> {
            override fun onResponse(call: Call<House?>?, response: Response<House?>) {
                progressDialog.hide()
                if (response.body() != null) {
                    if (response.body()!!.isStatus) {
                        makeText(this@AddHouseActivity,
                                "House saved",
                                LENGTH_LONG).show()
                        val intent = Intent(this@AddHouseActivity, NewAccommodation::class.java)
                        Log.d(TAG, "the house id is: ${response.body()!!.id}")
                        intent.putExtra(HOUSE_ID, response.body()!!.id)
                        startActivity(intent)
                    } else {
                        makeText(this@AddHouseActivity, "failed to add your house to classfinder", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<House?>?, t: Throwable?) {
                progressDialog.hide()
                makeText(this@AddHouseActivity, "Server Error, Please try again server error",
                        Toast.LENGTH_LONG).show()

                Log.d(TAG, t.toString())

            }
        })
    }


    override fun onImagesSelected(bitmaps: Array<Bitmap>, imagesUrls: Array<String>) {
        this.bitmaps = bitmaps // initialize them .
        this.imagesUris = imagesUrls
    }

    //my static members
    companion object {
        private val TAG = "__AddHouseActivity__"

        private fun validate(userId: Int,
                             address: String, hasSelectedSomething: Boolean, inputNearByFilled: Boolean): Boolean {
            if (!hasSelectedSomething) return false
            if (!inputNearByFilled) return false
            if (userId < 1) return false
            if (TextUtils.isEmpty(address)) return false
            return true
        }
    }


    private class NearToChip(val name: String, val id: Int, val address: String) : ChipInterface {

        override fun getAvatarDrawable(): Drawable? {
            return null
        }

        override fun getLabel(): String {
            return this.name
        }

        override fun getId(): Any {
            return this.id
        }

        override fun getAvatarUri(): Uri? {
            return null
        }

        override fun getInfo(): String {
            return this.address
        }

    }
}
