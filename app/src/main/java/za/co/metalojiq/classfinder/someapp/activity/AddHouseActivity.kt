package za.co.metalojiq.classfinder.someapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import za.co.metalojiq.classfinder.someapp.R

class AddHouseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_house)

        val etAddress = findViewById(R.id.input_address) as EditText
        val etCommon = findViewById(R.id.input_common) as EditText
        val ckNSFAS =  findViewById(R.id.checkbox_nsfas) as CheckBox
        val ckWifi =  findViewById(R.id.checkbox_wifi) as CheckBox
        val ckPrepaid =  findViewById(R.id.checkbox_prepaid_elec) as CheckBox
        val btnAddHouse =  findViewById(R.id.btn_add_house) as Button



    }
}
