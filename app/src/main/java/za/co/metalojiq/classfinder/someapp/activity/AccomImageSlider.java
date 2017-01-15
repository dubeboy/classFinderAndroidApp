package za.co.metalojiq.classfinder.someapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList;
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
        ArrayList<String> stringArrayList = intent.getStringArrayListExtra(AccomList.PICTURES_ARRAY_EXTRA);

        String price = intent.getStringExtra(AccomList.DOUBLE_PRICE_EXTRA);
        String roomType = intent.getStringExtra(AccomList.STRING_ROOM_TYPE_EXTRA);
        String location = intent.getStringExtra(AccomList.STRING_ROOM_LOCATION_EXTRA);

        TextView tvPrice = (TextView) findViewById(R.id.tv_price);
        TextView tvRoomType = (TextView) findViewById(R.id.tv_room_type);
        TextView tvLocation = (TextView) findViewById(R.id.tv_room_location);

        String p = "R " + price;
        tvPrice.setText(p);
        String j = "Room Type: " + roomType;
        tvRoomType.setText(j);
        String s = "Around " + location;
        tvLocation.setText(s);

        ViewPager mPager = (ViewPager) findViewById(R.id.viewPageAccom);
        AccomImageAdapter adapterView = new AccomImageAdapter(getApplicationContext(), stringArrayList);
        mPager.setAdapter(adapterView);


    }
}
