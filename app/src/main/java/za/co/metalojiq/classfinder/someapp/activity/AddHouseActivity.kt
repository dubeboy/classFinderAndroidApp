package za.co.metalojiq.classfinder.someapp.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.LinearGradient
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.Toast.*
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


//todo: should use chips for common areas
class AddHouseActivity : AppCompatActivity(), Utils.OnImagesSelected {


    lateinit var progressDialog: ProgressDialog
    lateinit var bitmaps: Array<Bitmap>
    lateinit var imagesUris: Array<String>
    lateinit var imagesContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_house)

        val userId = Utils.getUserId(this)
        Log.d(TAG, "$TAG the user id is: $userId")
        val etAddress = findViewById(R.id.input_address) as EditText
        val etCommon = findViewById(R.id.input_common) as EditText
        val etLocation = findViewById(R.id.input_location) as EditText
        val etCity = findViewById(R.id.input_city) as EditText
        val ckNSFAS = findViewById(R.id.checkbox_nsfas) as CheckBox
        val ckWifi = findViewById(R.id.checkbox_wifi) as CheckBox
        val ckPrepaid = findViewById(R.id.checkbox_prepaid_elec) as CheckBox
        val btnAddHouse = findViewById(R.id.btn_add_house) as Button
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Adding your house to classfinder, please wait.")
        imagesContainer = newImagesHorizontalScroll


        btnAddHouse.setOnClickListener({
            if (validate(userId, etAddress.text.toString(), etCommon.text.toString())) {
                saveHouse(userId,
                        etAddress.text.toString(),
                        etCity.text.toString(),
                        etLocation.text.toString(),
                        etCommon.text.toString(),
                        ckNSFAS.isChecked,
                        ckWifi.isChecked,
                        ckPrepaid.isChecked)
            } else {
                Toast.makeText(this@AddHouseActivity, "Please Fill in both forms", Toast.LENGTH_LONG).show()
            }
        })

        newBtnAddImages.setOnClickListener({
            Utils.launchImagesPicker(this@AddHouseActivity, supportFragmentManager,  imagesContainer, this@AddHouseActivity)
        })
    }

    //Function to persist the accommodation owners house
    private fun saveHouse(userId: Int, address: String,
                          city: String,
                          location: String,
                          common: String,
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
        val apiCaller = apiService.postHouse(userId, address, location, city, common, nsfas, wifi, prepaid,  body.parts())

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
        private val TAG = "AddHouseActivity"

        private fun validate(userId: Int,
                             address: String,
                             common: String): Boolean {
            if (userId < 1) return false
            if (TextUtils.isEmpty(address)) return false
            if (TextUtils.isEmpty(common)) return false
            return true
        }
    }
}
