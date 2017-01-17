package za.co.metalojiq.classfinder.someapp.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.expirimental.SettingsActivity;
import za.co.metalojiq.classfinder.someapp.activity.expirimental.Tabbed;
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList;
import za.co.metalojiq.classfinder.someapp.activity.fragment.GetAccomFailed;
import za.co.metalojiq.classfinder.someapp.model.Accommodation;
import za.co.metalojiq.classfinder.someapp.model.AccommodationResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;


//// TODO: 1/11/17   this class should use fragments to display the activity based on the which callback
public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, Search.class);
                startActivity(intent);
                return true;
            case R.id.action_login:
                Intent login = new Intent(this, LoginActivity.class);
                startActivity(login);
                return true;
            case R.id.action_settings:
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
            return true;
        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AccommodationResponse> call = apiService.getAllAccommodations();

        call.enqueue(new Callback<AccommodationResponse>() {

            //todo: please make this wait a bit so that it does not time out fast
            @Override
            public void onResponse(Call<AccommodationResponse> call, Response<AccommodationResponse> response) {
                ArrayList<Accommodation> accommodations = response.body().getResults();
                AccomList accomList = AccomList.newInstance(accommodations);
                setTitle(R.string.app_name);
                fragmentTransaction.add(R.id.activity_main, accomList, "ACCOM_LIST_FRAGMENT");
                fragmentTransaction.commit();
            }

            @Override
            public void onFailure(Call<AccommodationResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                GetAccomFailed getAccomFailed = new GetAccomFailed();
                setTitle(R.string.app_name);
                fragmentTransaction.add(R.id.activity_main, getAccomFailed, "ACCOM_FAIL_FRAGMENT");
                fragmentTransaction.commit();
            }
        });

    }
}
