package za.co.metalojiq.classfinder.someapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.expirimental.SettingsActivity;
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
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.LOGIN_PREF_FILENAME, MODE_PRIVATE);
        int id = sharedPreferences.getInt(LoginActivity.LOGIN_PREF_USER_ID, 0);
        Log.d(TAG, "The ID of the user  is: " + id);
        boolean isRunner = sharedPreferences.getBoolean(LoginActivity.LOGIN_IS_RUNNER, false);
        MenuItem itemLogin = menu.findItem(R.id.action_login);
        MenuItem itemRunner = menu.findItem(R.id.action_runner) ;
        MenuItem itemSignOut = menu.findItem(R.id.action_sign_out) ;
        if (id == 0)  { // means that the user is not logged in !!!
            itemRunner.setVisible(false);
            itemLogin.setVisible(true);
            itemSignOut.setVisible(false);
        } else if (isRunner){
            itemRunner.setVisible(true);
            itemLogin.setVisible(false);
            itemSignOut.setVisible(true);
        } else {
            itemSignOut.setVisible(true);
        }
        supportInvalidateOptionsMenu();
        return true;
    }


    private void signOut(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(LoginActivity.LOGIN_PREF_USER_ID);
        supportInvalidateOptionsMenu();
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:
                Intent intent = new Intent(this, Search.class);
                startActivity(intent);
                return true;
            case R.id.action_login:
                Intent login = new Intent(this, LoginActivity.class);
                startActivity(login);
                return true;
            case R.id.action_runner:
                Intent runner = new Intent(this, Runner.class);
                startActivity(runner);
                return true;
            case R.id.action_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;
            case R.id.action_sign_out:
                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.LOGIN_PREF_FILENAME, MODE_PRIVATE);
                signOut(sharedPreferences);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

                try {
                    if (response != null && response.errorBody() != null)
                        Log.d(TAG, "RAW RESONSE OF UNSUCCESFUl ==>" + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (response.body() != null) {
                    ArrayList<Accommodation> accommodations = response.body().getResults();
                    AccomList accomList = AccomList.newInstance(accommodations);
                    setTitle(R.string.app_name);
                    fragmentTransaction.add(R.id.activity_main, accomList, "ACCOM_LIST_FRAGMENT");
                    fragmentTransaction.commit();
                }
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
