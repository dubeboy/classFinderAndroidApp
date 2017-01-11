package za.co.metalojiq.classfinder.someapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.adapter.AccomImageAdapter;

/**
 * Created by divine on 1/11/17.
 */

public class AccomImageSlider extends AppCompatActivity{



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accom_image_slider_activity);

        //
        Intent intent = getIntent();
        ArrayList<String> stringArrayList = intent.getStringArrayListExtra(MainActivity.PICTURES_ARRAY_EXTRA);


        ViewPager mPager = (ViewPager) findViewById(R.id.viewPageAccom);
        AccomImageAdapter adapterView = new AccomImageAdapter(getApplicationContext(), stringArrayList);
        mPager.setAdapter(adapterView);


    }
}
