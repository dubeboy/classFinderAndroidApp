package za.co.metalojiq.classfinder.someapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity

import za.co.metalojiq.classfinder.someapp.R
import za.co.metalojiq.classfinder.someapp.activity.fragment.HouseActivityFragment
import za.co.metalojiq.classfinder.someapp.util.Utils

class HouseActivity : AppCompatActivity() {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house)
        val fab = findViewById(R.id.fab) as FloatingActionButton

        //fragment injection required code
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()
        val userId = Utils.getUserId(this) //todo User id is already in the prefs so no need for it
        fragmentTransaction.add(R.id.activity_house,
                                HouseActivityFragment.newInstance(userId),
                                "ACTIVITY_HOUSE_FRAGMENT")
        fragmentTransaction.commit()
        fab.setOnClickListener {
            val intent = Intent(this@HouseActivity, AddHouseActivity::class.java)
            startActivity(intent)
        }
    }

}
