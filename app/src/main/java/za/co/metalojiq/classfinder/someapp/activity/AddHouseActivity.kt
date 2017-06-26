package za.co.metalojiq.classfinder.someapp.activity

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
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

class AddHouseActivity : AppCompatActivity() {

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
    }

    //Function to persist the accommodation owners house
    private fun saveHouse(userId: Int, address: String,
                          city: String,
                          location: String,
                          common: String,
                          nsfas: Boolean,
                          wifi: Boolean,
                          prepaid: Boolean) {

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Adding your house to classfinder, please wait.")
        progressDialog.show() // show the dialog

        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val call = apiService.postHouse(userId, address, location, city, common, nsfas, wifi, prepaid)

        call.enqueue(object : Callback<House?> {
            override fun onResponse(call: Call<House?>?, response: Response<House?>) {
                progressDialog.hide()
                if (response.body() != null) {
                    if (response.body()!!.isStatus) {
                        makeText(this@AddHouseActivity,
                                "House saved",
                                LENGTH_LONG).show()
                        val intent = Intent(this@AddHouseActivity, NewAccommodation::class.java)
                        intent.putExtra(HOUSE_ID, response.body()?.id)
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

    //my static members
    companion object {
        private val TAG = "AddHouseActivity"
        private val HOUSE_ID = "HouseId"

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
