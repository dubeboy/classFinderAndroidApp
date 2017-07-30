package za.co.metalojiq.classfinder.someapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.firebase.auth.FirebaseAuth;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList;
import za.co.metalojiq.classfinder.someapp.activity.network.Networks;
import za.co.metalojiq.classfinder.someapp.activity.expirimental.SettingsActivity;
import za.co.metalojiq.classfinder.someapp.model.Accommodation;
import za.co.metalojiq.classfinder.someapp.model.AccommodationResponse;
import za.co.metalojiq.classfinder.someapp.model.StatusRespose;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;
import za.co.metalojiq.classfinder.someapp.util.Utils;

import java.io.IOException;
import java.util.ArrayList;

import static za.co.metalojiq.classfinder.someapp.util.Utils.getGoogleSignUp;
import static za.co.metalojiq.classfinder.someapp.util.Utils.makeToast;


//// TODO: 1/11/17   this class should use fragments to display the activity based on the which callback
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    public static final String TAG = "__MainActivity__";
    public static final String USER_ID = "userId";
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences sharedPreferences;
    private int userId = 0;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    EditText etOwnerPhone;

    //fragment injection required code
    FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        userId = sharedPreferences.getInt(LoginActivity.LOGIN_PREF_USER_ID, 0);  //need this for action menu invalidation
        Log.d(TAG, "The ID of the user  is: " + userId);
        boolean isHost = sharedPreferences.getBoolean(LoginActivity.USER_LOGIN_IS_HOST, false);
        Log.d(TAG, "is the user a host? " + isHost);
        MenuItem itemLogin = menu.findItem(R.id.action_login);
        MenuItem itemRunner = menu.findItem(R.id.action_runner);
        MenuItem itemSignOut = menu.findItem(R.id.action_sign_out);
        MenuItem itemHouses = menu.findItem(R.id.action_houses);
        if (userId == 0) { // means that the user is not logged in !!!
            itemRunner.setVisible(false);
            itemLogin.setVisible(true);
            itemSignOut.setVisible(false);
        } else if (isHost) {
            itemRunner.setVisible(false);
            itemLogin.setVisible(false);
            itemSignOut.setVisible(true);
            itemHouses.setVisible(true);
        } else {  // this is when the user is signed in but is not a host!
            itemRunner.setVisible(true);
            itemSignOut.setVisible(true);
            itemHouses.setVisible(false);
        }
        return true;
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
            case R.id.action_sign_out:
                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.LOGIN_PREF_FILENAME, MODE_PRIVATE);
                signOut(sharedPreferences);
                return true;
            case R.id.action_houses:
                Intent housesIntent = new Intent(this, HouseActivity.class);
                housesIntent.putExtra(USER_ID, userId);
                startActivity(housesIntent);

        }
        return true;
    }

    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        //Fixme remove at production
                        makeToast("Sign out" + status, getApplicationContext());
                    }
                });

    }

    private void signOut(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(LoginActivity.LOGIN_PREF_USER_ID);
        editor.remove(LoginActivity.USER_LOGIN_TOKEN);
        editor.commit(); // commit the changes and then invalidate option menu
        this.supportInvalidateOptionsMenu();
        googleSignOut();  // todo: reactivate soon
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.accomLoad);
        supportInvalidateOptionsMenu();  //TODO Called in the wrong places

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        mGoogleApiClient = getGoogleSignUp(this, this);

        sharedPreferences = getSharedPreferences(LoginActivity.LOGIN_PREF_FILENAME, MODE_PRIVATE);
        userId = sharedPreferences.getInt(LoginActivity.LOGIN_PREF_USER_ID, 0);  // getting the user Id yey !

        //@author - https://www.learn2crack.com/2015/10/android-floating-action-button-animations.html
        Log.d(TAG, "onCreate: hello tye fcm token is: " + Utils.getUserSharedPreferences(this).getAll());
        // it was exactly what i needed
        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setVisibility(View.VISIBLE); // should return the state of this to false the wy it was
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fab_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fab_back);
        if (Utils.getUserId(this) != 0) {
            fab.setOnClickListener(this);
            fab1.setOnClickListener(this);
            fab2.setOnClickListener(this);
        } else {  // todo : id this really required man
            makeToast("Please sign in first", this);
            startActivity(new Intent(this, LoginActivity.class));
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AccommodationResponse> call = apiService.getAllAccommodations(1); //just get the first 6 elements

        call.enqueue(new Callback<AccommodationResponse>() {

            @Override
            public void onResponse(Call<AccommodationResponse> call, Response<AccommodationResponse> response) {
                progressBar.setVisibility(View.GONE);

                try {
                    if (response.errorBody() != null)
                        Log.d(TAG, "RAW RESPONSE OF UNSUCCESSFUL ==>" + response.errorBody().string());
                    Snackbar.make(((MainActivity.this).findViewById(android.R.id.content)),
                            "Opps!, an Error happened!", Snackbar.LENGTH_LONG);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//
//              TODO  should refactor this so that we can just pass another argument id from which class so if an error happens
//                so that we just show the msg
                if (response.body() != null) {
                    ArrayList<Accommodation> accommodations;
                    accommodations = response.body().getResults();
                    if (accommodations.size() > 0) {
                        Log.d(TAG, "host id " + accommodations.get(0).getHostId());
                        final AccomList accomList = AccomList.newInstance(accommodations, -1);
                        setTitle(R.string.app_name);
                        startAccomListActivity(accomList, fragmentTransaction);
                    }
                }
            }

            @Override
            public void onFailure(Call<AccommodationResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                ArrayList<Accommodation> accommodations = new ArrayList<>();
                final AccomList accomList = AccomList.newInstance(accommodations, -1);
                //    final HouseActivityFragment myHousesList = HouseActivityFragment.Companion.newInstance(userId);
                // start the activity tu
             //   setTitle("Error");
                Snackbar.make(findViewById(android.R.id.content), "Please connect to the internet, swipe down to refresh", Snackbar.LENGTH_INDEFINITE).show();
                startAccomListActivity(accomList, fragmentTransaction);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        supportInvalidateOptionsMenu(); //todo: should work
    }

    private void startAccomListActivity(AccomList accomList, FragmentTransaction fragmentTransaction) {
        fragmentTransaction.add(R.id.activity_main, accomList, "ACCOM_LIST_FRAGMENT");
        fragmentTransaction.commitAllowingStateLoss(); //this is causing problem #Fixme
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        makeToast("Please connect to the internet first to use google sign in or update google sign in", this);
    }

    public void animateFAB() {

        if (isFabOpen) {
            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {
            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
            Log.d("Raj", "open");

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fab1:

                Log.d(TAG, "Fab 1");
                break;
            case R.id.fab2:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.fragment_invite_accommodation_owner, null);
                builder.setView(dialogView);
                builder.setTitle("Invite landLord to Checkinn (free)");
                etOwnerPhone = (EditText) dialogView.findViewById(R.id.accom_owner_phone);
                builder.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phoneNumber = etOwnerPhone.getText().toString();
                        if(validateNumber(phoneNumber.trim())) {
                            makeToast("Inviting Landlord (free)", MainActivity.this);
                            ApiClient.getClient().create(ApiInterface.class)
                                    .sendSms(phoneNumber, Utils.getUserId(MainActivity.this))
                                    .enqueue(new Callback<StatusRespose>() {
                                        @Override
                                        public void onResponse(Call<StatusRespose> call, Response<StatusRespose> response) {
                                            if (response.body() != null) {
                                                if (response.body().isStatus()) {
                                                    Toast.makeText(MainActivity.this,
                                                            "sent invitation to landlord", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(MainActivity.this,
                                                            "Sorry could not invite landload", Toast.LENGTH_LONG).show();

                                                }
                                            } else {
                                                Toast.makeText(MainActivity.this,
                                                        "Ops this should not happen", Toast.LENGTH_LONG).show(); //useless

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<StatusRespose> call, Throwable t) {
                                            Toast.makeText(MainActivity.this,
                                                    "To invite a landlord you need to be connected to the internet", Toast.LENGTH_LONG).show();
                                        }
                                    });
                            dialog.dismiss();
                        } else makeToast("Please validate your input", MainActivity.this);
                    }

                    private boolean validateNumber(String phoneNumber) {
                        return (!TextUtils.isEmpty(phoneNumber)) && phoneNumber.length() == 10;
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                animateFAB(); // close the animation
                break;
        }
    }
}
