package za.co.metalojiq.classfinder.someapp.activity;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList;
import za.co.metalojiq.classfinder.someapp.activity.fragment.CardInputFragment;
import za.co.metalojiq.classfinder.someapp.activity.fragment.DatePickerFragment;
import za.co.metalojiq.classfinder.someapp.activity.fragment.ListBottomSheet;
import za.co.metalojiq.classfinder.someapp.activity.fragment.TimePickerFragment;
import za.co.metalojiq.classfinder.someapp.adapter.AccomImageAdapter;
import za.co.metalojiq.classfinder.someapp.model.AccommodationResponse;
import za.co.metalojiq.classfinder.someapp.model.House;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;
import za.co.metalojiq.classfinder.someapp.util.Utils;

import static za.co.metalojiq.classfinder.someapp.activity.LoginActivity.GOOGLE_USER_EMAIL;
import static za.co.metalojiq.classfinder.someapp.activity.LoginActivity.LOGIN_PREF_EMAIL;

/**
 * Created by divine on 1/11/17.
 */

public class AccomImageSlider extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerFragment.OnSelectTime {

    private static final String TAG = AccomImageSlider.class.getSimpleName();
    //Final because we want to marry this calendar instance for ever till onDestroy() of activity , lol lol!!!!
    private final Calendar c = Calendar.getInstance();
    private int hostId;
    private int advertId;
    private Button btnRent;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accom_image_slider, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chat:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra(AccomList.POST_INT_HOST_ID, hostId);
                intent.putExtra(AccomList.POST_ADVERT_ID, advertId);
                startActivity(intent);
                return true;
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accom_image_slider_activity);
        //setStatusBarTranslucent(true);
        //get passed in intent
        Intent intent = getIntent();
        ArrayList<String> stringArrayList =
                intent.getStringArrayListExtra(AccomList.PICTURES_ARRAY_EXTRA);

        String price = intent.getStringExtra(AccomList.DOUBLE_PRICE_EXTRA);
        String roomType = intent.getStringExtra(AccomList.STRING_ROOM_TYPE_EXTRA);
        String location = intent.getStringExtra(AccomList.STRING_ROOM_LOCATION_EXTRA);
        String description = intent.getStringExtra(AccomList.STRING_ROOM_DESC);
        String address = intent.getStringExtra(AccomList.STRING_ROOM_ADDRESS_EXTRA);
        String city = intent.getStringExtra(AccomList.STRING_ROOM_CITY_EXTRA);
        House house = (House) intent.getSerializableExtra(AccomList.OBJECT_ROOM_HOUSE);
        final String deposit = intent.getStringExtra(AccomList.DOUBLE_ROOM_DEPOSIT_EXTRA);
        hostId = intent.getIntExtra(AccomList.POST_INT_HOST_ID, 0);


        SharedPreferences sharedPreferences = Utils.getUserSharedPreferences(this);

        Log.d(TAG, "the host ID is this one " + hostId);
        advertId = intent.getIntExtra(AccomList.POST_ADVERT_ID, 0);
        String email = sharedPreferences.getString(LOGIN_PREF_EMAIL, "");
        Log.d(TAG, "onCreate: the email is: " + email);
        if (email.equals("")) {
            email = sharedPreferences.getString(GOOGLE_USER_EMAIL, "");
        }

        TextView tvPrice = (TextView) findViewById(R.id.tv_price);
        TextView tvRoomType = (TextView) findViewById(R.id.tv_num_people);
        TextView tvLocation = (TextView) findViewById(R.id.tv_room_location);
        TextView tvDesc = (TextView) findViewById(R.id.tv_desc);
        Button btnSecureAccom = (Button) findViewById(R.id.btnSecureAccom);
        //group holder a group of items where one at a time can be active
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.imageCountIndicator);
        ImageButton btnShare = (ImageButton) findViewById(R.id.btn_share);

        String p = "R " + price;
        tvPrice.setText(p);

        tvRoomType.setText(roomType);
        String wifi = house.getWifi().equals("wifi") ? "Wifi: Yes" : "Wifi: No";
        String common = TextUtils.isEmpty(house.getCommon()) ? "" : house.getCommon() + "\n";

        tvDesc.setText(
                "City: " + city + "\n"
                + wifi + "\n"
                + "Address: " + house.getAddress() + "\n"
                + common
                        + description + "\n");

        //TODO the parsing activity should be the one sanitising this info!!!!
        //this should be in array somewhere I think
        String s = "Location: " + location;
        tvLocation.setText(s);

        //Trick inspired by @Akuru
        final RadioButton[] radioButtons = new RadioButton[stringArrayList.size()];

        for (int i = 0; i < stringArrayList.size(); i++) {
            radioButtons[i] = new RadioButton(this);
            radioButtons[i].setClickable(false); //todo make it able to change the image when clicked and also change style
            radioGroup.addView(radioButtons[i]);
        }


        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = Utils.shareButtonIntent(advertId, getApplicationContext());
                if(shareIntent != null)
                    startActivity(shareIntent);
            }
        });

        ViewPager mPager = (ViewPager) findViewById(R.id.viewPageAccom);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                radioButtons[position].setChecked(true);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        AccomImageAdapter adapterView = new AccomImageAdapter(getApplicationContext(), stringArrayList);
        mPager.setAdapter(adapterView);

        btnSecureAccom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.getUserId(AccomImageSlider.this) != 0) {
                    DatePickerFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                    // calls back @onDateSet
                } else {
                    Toast.makeText(AccomImageSlider.this, "Please login first", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(AccomImageSlider.this, LoginActivity.class));
                }
            }
        });

        final String finalEmail = email; // copy of the email var, coz email var cannot be final :(
        btnRent = (Button) findViewById(R.id.btnRentRoom);


        btnRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: the email is " + finalEmail);
                CardInputFragment cardInputFragment
                        = CardInputFragment.
                        Companion.newInstance(advertId, finalEmail, deposit);
               cardInputFragment.show(getSupportFragmentManager(),
                       "dialog");
            }
        });


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        startTimePickerFrag();
    }

    private void startTimePickerFrag() {
        ListBottomSheet timePickerFragment = ListBottomSheet.newInstance("Select view time", TimePickerFragment.times);
        timePickerFragment.setListener(this);
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void secureRoom(int advertId, int hostId, int studentId, String month, String time) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AccommodationResponse> call = apiService.secureRoom(advertId, hostId, studentId, 0, month, time); //just get the first 6 elements

        call.enqueue(new Callback<AccommodationResponse>() {
            @Override
            public void onResponse(Call<AccommodationResponse> call, Response<AccommodationResponse> response) {
                if (response.body() != null && response.body().isStatus()) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Congratulations you have successfully booked to view this room." + //todo NB remove this
                                    "Please send Confirmation message(Text/WhatsApp) to 0823114484 " +
                                    "with name and surname to confirm your booking ", Snackbar.LENGTH_INDEFINITE).show();

                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Sorry request cannot be processed, " +
                            "because you have already booked this room." +
                            " Have you sent the confirmation message to 0823114484?", Snackbar.LENGTH_INDEFINITE).show();
                }
            }

            @Override
            public void onFailure(Call<AccommodationResponse> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Sorry classfinder error, we will be back soon",
                        Snackbar.LENGTH_INDEFINITE);
            }
        });
    }

    private static String pad(int c) {
        if (c >= 10) {
            return String.valueOf(c);
        } else {
            return "0" + String.valueOf(c);
        }
    }

    @Override
    public void onItemSelected(String time) {
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        String month =// Month is 0 based so add 1 mm/dd/yy
                String.valueOf(mMonth + 1) + "/" +
                        mDay + "/" +
                        mYear + " ";
        Log.d(TAG, "The String month is " + month);
        Log.d(TAG, "The time is " + time);
        final int studentId = Utils.getUserId(this);
        Log.d(TAG, "onItemSelected: the student id is: " + studentId);
        secureRoom(advertId, hostId, studentId, month, time);
    }
}
