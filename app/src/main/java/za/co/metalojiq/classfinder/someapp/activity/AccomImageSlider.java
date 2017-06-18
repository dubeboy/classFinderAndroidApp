package za.co.metalojiq.classfinder.someapp.activity;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList;
import za.co.metalojiq.classfinder.someapp.activity.fragment.DatePickerFragment;
import za.co.metalojiq.classfinder.someapp.activity.fragment.ListBottomSheet;
import za.co.metalojiq.classfinder.someapp.activity.fragment.TimePickerFragment;
import za.co.metalojiq.classfinder.someapp.adapter.AccomImageAdapter;
import za.co.metalojiq.classfinder.someapp.model.AccommodationResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;

/**
 * Created by divine on 1/11/17.
 */

public class AccomImageSlider extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener , TimePickerFragment.OnSelectTime {

    private static final String TAG = AccomImageSlider.class.getSimpleName();
    //Final because we want to marry this calendar instance for ever till onDestroy() of activity , lol lol!!!!
    private final Calendar c = Calendar.getInstance();
    private int hostId;
    private int studentId;
    private int advertId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accom_image_slider_activity);
            //setStatusBarTranslucent(true);
        //
        Intent intent = getIntent();
        ArrayList<String> stringArrayList = intent.getStringArrayListExtra(AccomList.PICTURES_ARRAY_EXTRA);

        String price = intent.getStringExtra(AccomList.DOUBLE_PRICE_EXTRA);
        String roomType = intent.getStringExtra(AccomList.STRING_ROOM_TYPE_EXTRA);
        String location = intent.getStringExtra(AccomList.STRING_ROOM_LOCATION_EXTRA);
        String description = intent.getStringExtra(AccomList.STRING_ROOM_DESC);
        hostId = intent.getIntExtra(AccomList.POST_INT_HOST_ID, 0);

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.LOGIN_PREF_FILENAME, MODE_PRIVATE);

        Log.d(TAG, "the host ID is this one " + hostId);
        studentId = sharedPreferences.getInt(LoginActivity.LOGIN_PREF_USER_ID, 0);
        advertId = intent.getIntExtra(AccomList.POST_ADVERT_ID, 0);


        TextView tvPrice = (TextView) findViewById(R.id.tv_price);
        TextView tvRoomType = (TextView) findViewById(R.id.tv_num_people);
        TextView tvLocation = (TextView) findViewById(R.id.tv_room_location);
        TextView tvDesc = (TextView) findViewById(R.id.tv_desc);
        Button btnSecureAccom = (Button) findViewById(R.id.btnSecureAccom);
        //group holder a group of items where one at a time can be active
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.imageCountIndicator);

        String p = "R " + price;
        tvPrice.setText(p);

        tvRoomType.setText(roomType);

        tvDesc.setText(description);

        //TODO the parsing activity should be the one sanitising this info!!!!
        //this should be in array somewhere I think
        String s = (location.equals("Auckland Park")) ? "" :"Around " + location;
        tvLocation.setText(s);

        //Trick inspired by @Akuru
        final RadioButton[] radioButtons = new RadioButton[stringArrayList.size()];

        for (int i = 0; i < stringArrayList.size(); i++) {
            radioButtons[i] = new RadioButton(this);
            radioButtons[i].setClickable(false);
            radioGroup.addView(radioButtons[i]);
        }

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
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


    }


    //TODO implement later please not good enough
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        View v = findViewById(R.id.accomImageSliderLayout);
        if (v != null) {
            int paddingTop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? getStatusBarHeight(this) : 0;
            TypedValue tv = new TypedValue();
            getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, tv, true);
            paddingTop += TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            v.setPadding(0, makeTranslucent ? paddingTop : 0, 0, 0);
        }

        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private int getStatusBarHeight(AccomImageSlider accomImageSlider) {
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight= contentViewTop - statusBarHeight;

        Log.i("*** AccomImageS :: ", "StatusBar Height= " + statusBarHeight + " , TitleBar Height = " + titleBarHeight);
        return titleBarHeight;
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

    private void  secureRoom(int advertId, int hostId, int studentId, String month, String time) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AccommodationResponse> call = apiService.secureRoom(advertId, hostId, studentId, 0, month, time); //just get the first 6 elements

        call.enqueue(new Callback<AccommodationResponse>() {
            @Override
            public void onResponse(Call<AccommodationResponse> call, Response<AccommodationResponse> response) {
                if (response.body().isStatus()) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Congratulations you have successfully booked to view this room." +
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

            }
        });
    }

    private static String pad(int c ) {
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

        Log.d(TAG,"The String month is " + month);
        Log.d(TAG,"The time is " + time);
        secureRoom(advertId, hostId, studentId, month, time);
    }
}
