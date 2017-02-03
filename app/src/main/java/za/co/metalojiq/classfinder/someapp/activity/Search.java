package za.co.metalojiq.classfinder.someapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList;
import za.co.metalojiq.classfinder.someapp.model.AccommodationResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;
import za.co.metalojiq.classfinder.someapp.util.Utils;

import static za.co.metalojiq.classfinder.someapp.util.Utils.AUCK_AREA_PREFIX;
import static za.co.metalojiq.classfinder.someapp.util.Utils.LOCATIONS;
import static za.co.metalojiq.classfinder.someapp.util.Utils.setupSpinner;

public class Search extends AppCompatActivity {

    private static final String TAG = Search.class.getSimpleName();
    public static final String INTENT_RESPONSE_EXTRA = TAG + ".response";

    public enum INTENT_RESPONSE {SUCCESS, FAILURE}

    ;

    private Spinner auckAreaSpinner;
    private Spinner roomTypeSpinner;
    private Spinner locationSpinner;
    private EditText priceFrom;
    private EditText priceTo;
    private TextView tvAuck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        tvAuck = (TextView) findViewById(R.id.search_tv_auck_areas);

        locationSpinner = setupSpinner(this, R.id.search_location_spinner, R.array.locations_array);
        roomTypeSpinner = setupSpinner(this, R.id.search_spinner_room_type, R.array.room_type);
        auckAreaSpinner = setupSpinner(this, R.id.search_spinner_auck_areas, R.array.auck_areas);


        locationSpinner.setOnItemSelectedListener(new Utils.LocationItemListener(tvAuck, auckAreaSpinner));
        priceFrom = (EditText) findViewById(R.id.search_price_from);
        priceTo = (EditText) findViewById(R.id.search_price_to);
    }

    public void search(View view) {
        String auckArea = "";

        String location = (String) locationSpinner.getSelectedItem();

        if (location.equals(LOCATIONS[0])) {
            String rawArea = (String) auckAreaSpinner.getSelectedItem();
            auckArea = AUCK_AREA_PREFIX + rawArea; //YAK MAN bad code need better thought man.
        }

        String roomType = (String) roomTypeSpinner.getSelectedItem();

        int priceT = Integer.valueOf(TextUtils.isEmpty(priceTo.getText().toString()) ? "0" : priceTo.getText().toString());
        int priceF = Integer.valueOf(TextUtils.isEmpty(priceFrom.getText().toString()) ? "0" : priceFrom.getText().toString());

        //put this in a runner so that we will just get the result from thread
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AccommodationResponse> call = apiService.searchAccommodations(location, roomType, auckArea, priceF, priceT);

        Log.d(TAG + "dsldsdsadsd request", call.request().toString());
        final Intent intent = new Intent(this, SearchResults.class);
        call.enqueue(new Callback<AccommodationResponse>() {
            @Override
            public void onResponse(Call<AccommodationResponse> call, Response<AccommodationResponse> response){
                intent.putExtra(AccomList.ACCOM_BUNDLE_KEY, response.body().getResults());
                intent.putExtra(INTENT_RESPONSE_EXTRA, INTENT_RESPONSE.SUCCESS);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<AccommodationResponse> call, Throwable t) {
                Log.e(TAG + " RRRRR", t.toString());
                intent.putExtra(INTENT_RESPONSE_EXTRA, INTENT_RESPONSE.FAILURE);
                startActivity(intent);
            }
        });
    }
}
