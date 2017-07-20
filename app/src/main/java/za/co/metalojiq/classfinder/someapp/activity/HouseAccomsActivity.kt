package za.co.metalojiq.classfinder.someapp.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.metalojiq.classfinder.someapp.R
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList
import za.co.metalojiq.classfinder.someapp.activity.fragment.HouseActivityFragment
import za.co.metalojiq.classfinder.someapp.model.AccommodationResponse
import za.co.metalojiq.classfinder.someapp.rest.ApiClient
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface


class HouseAccomsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // reuse the main activity

        title = "Your Accommodations" // more natural u see ...
        val intent: Intent = intent
        val fragmentManager = supportFragmentManager
        val progressBar = findViewById(R.id.accomLoad) as ProgressBar
        progressBar.visibility = View.VISIBLE

        val houseId = intent.getIntExtra(HouseActivityFragment.HOUSE_ID, -1)
        val apiClient = ApiClient.getClient().create(ApiInterface::class.java)
        val apiCall = apiClient.getAccommodationsForHouse(houseId)
        apiCall.enqueue(object: Callback<AccommodationResponse?> {
            override fun onResponse(call: Call<AccommodationResponse?>, response: Response<AccommodationResponse?>) {
                progressBar.visibility = View.GONE //Hide the progress bar man

                if(response.body() != null && response.body()!!.results != null) {
                    val accoms = AccomList.newInstance(ArrayList(response.body()!!.results), houseId)
                    runOnUiThread({  // I think its always good practise man
                        val fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.add(R.id.activity_main, accoms, "HOUSE_ACCOMS")
                        fragmentTransaction.commit()
                    })
                }
            }
            override fun onFailure(call: Call<AccommodationResponse?>?, t: Throwable?) {
                Toast.makeText(this@HouseAccomsActivity,
                                "ClassFinder error, please try again latter",
                                Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
        })
    }
}
