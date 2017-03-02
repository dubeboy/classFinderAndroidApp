package za.co.metalojiq.classfinder.someapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.fragment.PickTimeFragment;
import za.co.metalojiq.classfinder.someapp.model.Venue;
import za.co.metalojiq.classfinder.someapp.model.VenuesRespose;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;

import java.util.ArrayList;

public class VenueFinder extends AppCompatActivity implements Callback<VenuesRespose> {

    private static final String TAG = VenueFinder.class.getSimpleName();
    ArrayList<String> venues;
    ArrayAdapter<String> adapter;
    private ListView lv = null;
    private ProgressBar progressBar;
    private ApiInterface apiService;
    private TextView venueTvError;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_venues, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_date:
                PickTimeFragment newFragment = new PickTimeFragment();
                newFragment.setOnTimeSetListener(new PickTimeFragment.OnTimeIsSet() {
                    @Override
                    public void timeSet(int hour, int minute) {
                        Log.d(TAG, "timeSet: time: " + convertToTwoDecimal(hour) + ":" + convertToTwoDecimal(minute));
                        Call<VenuesRespose> call = apiService.getVenues(convertToTwoDecimal(hour) + ":" + convertToTwoDecimal(minute));
                        call.enqueue(VenueFinder.this);
                    }
                });
                newFragment.show(getSupportFragmentManager(), "timePicker");
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_finder);
        lv = (ListView) findViewById(R.id.venuesList);
         venueTvError = (TextView) findViewById(R.id.venueTvError);
         progressBar = (ProgressBar) findViewById(R.id.venuesLoad);
         venues = new ArrayList<>();
         adapter = new ArrayAdapter<String>(VenueFinder.this, android.R.layout.simple_list_item_1, venues);
         lv.setAdapter(adapter);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<VenuesRespose> call = apiService.getVenues(null);
        venueTvError.setVisibility(View.GONE);
        call.enqueue(this);
    }


    public void onResponse(Call<VenuesRespose> call, Response<VenuesRespose> response) {
        if (response.body() != null) {
            venues = Venue.genStringArray(response.body().getVenues());
            Log.d(TAG, "onResponse: " + venues.get(0));
            adapter.clear();
            for (String v : venues) {
                adapter.add(v);
            }
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            venueTvError.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "onResponse: oerro");
        }
    }

    @Override
    public void onFailure(Call<VenuesRespose> call, Throwable t) {
        progressBar.setVisibility(View.GONE);
        Log.d(TAG, "onFailure: " + t.getMessage());
        Toast.makeText(VenueFinder.this, "An Error happened please try again", Toast.LENGTH_LONG).show();
        venueTvError.setVisibility(View.VISIBLE);
    }

    private static String convertToTwoDecimal(int time) {
        if (time < 10) {
            return  "0" + time;
        }
        return "" + time;
    }
}
