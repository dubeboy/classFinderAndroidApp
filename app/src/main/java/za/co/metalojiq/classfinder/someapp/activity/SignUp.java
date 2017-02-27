package za.co.metalojiq.classfinder.someapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.model.UserResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;

import static za.co.metalojiq.classfinder.someapp.activity.LoginActivity.*;
import static za.co.metalojiq.classfinder.someapp.util.Utils.*;

public class SignUp extends AppCompatActivity implements Callback<UserResponse>, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = SignUp.class.getSimpleName();
    private EditText mEmail;
    private EditText mName;
    private EditText mPassword;
    private EditText mPhone;
    private EditText mConfPassword;
    private String email;
    private String name;
    private String googleUserToken;
    private ProgressDialog dialog;
    private ImageButton mRunnerQuestion;
    private CheckBox mCheckIsRunner;
    private boolean isGoogleUser;
    private CheckBox mCheckBox8;
    private CheckBox mCheckBox10;
    private CheckBox mCheckBox12;
    private CheckBox mCheckBox14;
    private CheckBox mCheckBox16;
    HorizontalScrollView horScroll;
    //array of timeSlot the are like ids of time slots!!! 1 means that the time is selected 0 off
    private byte times[] = {0,0,0,0,0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmail = (EditText) findViewById(R.id.signUpEmail);
        mName = (EditText) findViewById(R.id.signUpName);
        mPhone = (EditText) findViewById(R.id.signUpPhoneNumber);
        mPassword = (EditText) findViewById(R.id.signUpPassWord);
        mConfPassword = (EditText) findViewById(R.id.signUpConfirmPassWord);
        mRunnerQuestion = (ImageButton) findViewById(R.id.signUpHelpBtn);
        mCheckIsRunner = (CheckBox) findViewById(R.id.signUpIsRunner);
        Button signIn = (Button) findViewById(R.id.signUpLogin);
        Button signUp = (Button) findViewById(R.id.signUpBtn);

        mCheckBox8 = (CheckBox) findViewById(R.id.signUpRadio8);
        mCheckBox10 = (CheckBox) findViewById(R.id.signUpRadio10);
        mCheckBox12 = (CheckBox) findViewById(R.id.signUpRadio12);
        mCheckBox14 = (CheckBox) findViewById(R.id.signUpRadio14);
        mCheckBox16 = (CheckBox) findViewById(R.id.signUpRadio16);

        mCheckBox8.setOnCheckedChangeListener(this);
        mCheckBox10.setOnCheckedChangeListener(this);
        mCheckBox12.setOnCheckedChangeListener(this);
        mCheckBox14.setOnCheckedChangeListener(this);
        mCheckBox16.setOnCheckedChangeListener(this);

        //hide runner info
        toggleShowRunnerInfo(false);
        final Spinner locationsSpinner =  setupSpinner(this, R.id.signUpSpinnerTime, R.array.locations_array);

        mRunnerQuestion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "As an Accommodation Assistant you are expected to take clients to view rooms around" +
                             "an area you are familiar with.Select an area around which you are available to take clients to view rooms.";
                showTooltip(s, mRunnerQuestion);
            }
        });

        mCheckIsRunner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckIsRunner.isChecked()) {
                    toggleShowRunnerInfo(true);
                } else {
                    toggleShowRunnerInfo(false);
                }
            }
        });


        Intent intent = getIntent();
        isGoogleUser = intent.getBooleanExtra(IS_GOOGLE_LOGIN, false);

        if (isGoogleUser) {
            email = intent.getStringExtra(LoginActivity.GOOGLE_USER_EMAIL);
            name = intent.getStringExtra(LoginActivity.GOOGLE_USER_NAME);
            googleUserToken = intent.getStringExtra(LoginActivity.GOOGLE_USER_TOKEN);
            hideAllFieldsExceptPhone();

            signUp.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkPhone()) {
                        if (mCheckIsRunner.isChecked()) {
                            //this makes sure atleast one is checked and then sets message if not checked
                            if (!isSelectedAtleastOneTimePeriod()) {
                                return;
                            }
                            googleSignUp(googleUserToken, email, name,
                                    mPhone.getText().toString().trim(),
                                    mCheckIsRunner.isSelected(), times, ((String) locationsSpinner.getSelectedItem()));
                        } else { // this part is for when a user does not want to be a runner
                            googleSignUp(googleUserToken, email, name,
                                    mPhone.getText().toString().trim(),
                                    mCheckIsRunner.isSelected(), null, null);
                        }
                    }

                }
            });  //END GOOGLE USER
        }  else {
            final String email = mEmail.getText().toString().trim();
            final String name = mName.getText().toString().trim();
            signUp.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkPassword()) {
                        if (mCheckIsRunner.isChecked()) {
                            //this makes sure atleast one is checked and then sets message if not checked
                            if (!isSelectedAtleastOneTimePeriod()) {
                                return;
                            }
                            signUp(email, name,  mPhone.getText().toString().trim(),
                                    mPassword.getText().toString(), mCheckIsRunner.isSelected(), times,
                                    ((String) locationsSpinner.getSelectedItem()));
                        } else { // this part is for when a user does not want to be a runner
                            signUp(email, name,  mPhone.getText().toString().trim(),
                                    mPassword.getText().toString(), mCheckIsRunner.isChecked(), null,
                                   null);
                        }
                    }
                }
            });
        }

        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void toggleShowRunnerInfo(boolean show) {
         horScroll = (HorizontalScrollView) findViewById(R.id.SignUpHorizontalTimes);
        TextView tvSelectTime = (TextView)  findViewById(R.id.signUpTvSelectTime);
        TextView signUpSelectTime = (TextView)  findViewById(R.id.signUpSelectTime);
        Spinner spinner = (Spinner) findViewById(R.id.signUpSpinnerTime);

        if (show) {
            tvSelectTime.setVisibility(View.VISIBLE);
            horScroll.setVisibility(View.VISIBLE);
            signUpSelectTime.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
        } else {
            tvSelectTime.setVisibility(View.GONE);
            horScroll.setVisibility(View.GONE);
            signUpSelectTime.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
        }
    }

    private boolean checkPassword() {
        Log.d(TAG, "checkPassword: the password is " + mPassword.getText().toString());
        if (isPasswordValid(mPassword.getText().toString())) {
            mPassword.setError("Password too short");
            return false;
        } else if (!(mPassword.getText().toString().equals(mConfPassword.getText().toString()))) {
            mPassword.setError("Passwords don`t match");
            return false;
        } else {
            return true;
        }
    }


    private boolean isSelectedAtleastOneTimePeriod() {
        for (byte i : times) {
            if (i == 1) {
                Log.d(TAG, "isSelectedAtleastOneTimePeriod: someting selected");
                return true;
            }
        }
        Log.d(TAG, "isSelectedAtleastOneTimePeriod: uhhm did not loop");
        showTooltip("Please Select at least one assistant time at which you available", horScroll);
        return false;
    }

    private void showTooltip(String msg, View anchorView) {
        new SimpleTooltip.Builder(this)
                .anchorView(anchorView)
                .text(msg)
                .gravity(Gravity.BOTTOM)
                .animated(true)
                .transparentOverlay(true)
                .build()
                .show();
    }

    private boolean checkPhone() {
         if (mPhone.getText().toString().trim().length() != 10) {
            mPhone.setError("Phone number should be 10 digits");
            return false;
         }
         return true;
    }


    private void hideAllFieldsExceptPhone() {
        mPassword.setVisibility(View.GONE);
        mConfPassword.setVisibility(View.GONE);
        mEmail.setVisibility(View.GONE);
        mName.setVisibility(View.GONE);
    }


    // these function are very useless

    private void signUp(String email, String name, String phone, String password, boolean selected, byte[] times, String selectedItem) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UserResponse> call = apiService.signUp(email, name, phone, password, selected, times, selectedItem);
        call.enqueue(this);
        showProgressDialog();

    }

    private void googleSignUp(String token, String email, String name, String phone, boolean selected, byte[] times, String selectedItem) {
        //call retrofit
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UserResponse> call = apiService.googleUserSignUp(email, name, phone, token, selected, times, selectedItem);
        call.enqueue(this);
        showProgressDialog();

    }

    private void showProgressDialog() {
        dialog = ProgressDialog.show(this, "", "Signing you up!, just hold on...", true);
    }
    private void hideProgressDialog() {
        dialog.hide();
    }


    @Override
    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
        if (response.body().isStatus()) {
            saveUserInfo(email, response.body().getUser().getId(), response.body().getUser().isRunner(), isGoogleUser);
            makeToast("Thank you you have signed up successfully", this);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            makeToast("Please try again this account already exists, or rather sign in", this);
        }
        hideProgressDialog();
    }


    public void saveUserInfo(String email, int id, boolean isRunner, boolean isGoogle) {
        SharedPreferences sharedPreferences  = getUserSharedPreferences(this) ;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGIN_PREF_EMAIL, email);
        editor.putInt(LOGIN_PREF_USER_ID, id);
        editor.putBoolean(LOGIN_IS_RUNNER, isRunner);
        editor.putBoolean(IS_GOOGLE_LOGIN, isGoogle);
        editor.apply(); //fixme does this backgroud thing effect any thing
    }

    @Override
    public void onFailure(Call<UserResponse> call, Throwable t) {
        makeToast("Please connect to the internet", this);
        Log.d(TAG, "onFailure: " + t.getMessage());
        hideProgressDialog();
    }

    //fixme make it neater please!!!!!
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.signUpRadio8:
//                if (mCheckBox8.isChecked()) {
//                    Log.d(TAG, "this one should have been selected8");
//                    times[0] = 1;
//                } else {
//                    Log.d(TAG, "this one should have been unselected8");
//                    times[0] = 0;
//                }
//                break;
//            case R.id.signUpRadio10:
//                if (mCheckBox10.isChecked()) {
//                    Log.d(TAG, "this one should have been selected10");
//                    times[1] = 1;
//                } else {
//                    Log.d(TAG, "this one should have been unselected10");
//                    times[1] = 0;
//                }
//                break;
//            case R.id.signUpRadio12:
//
//                if (mCheckBox12.isChecked()) {
//                    Log.d(TAG, "this one should have been selected12");
//                    times[2] = 1;
//                } else {
//                    Log.d(TAG, "this one should have been unselected10");
//                    times[2] = 0;
//                }
//                break;
//            case R.id.signUpRadio14:
//                if (mCheckBox12.isChecked()) {
//                    Log.d(TAG, "this one should have been selected14");
//                    times[3] = 1;
//                } else {
//                    Log.d(TAG, "this one should have been unselected14");
//                    times[3] = 0;
//                }
//                break;
//            case R.id.signUpRadio16:
//
//                break;
//        }
//    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = genIndexfromResId(buttonView.getId());
        Log.d(TAG, "onCheckedChanged: id of button is" + id);
        if (isChecked) {
            Log.d(TAG, "this one should have been ON");
            times[id] = 1;
        } else {
            Log.d(TAG, "this one should have been OFF");
            times[id] = 0;
        }
        for (byte i : times ) {
            Log.d(TAG, "onCheckedChanged: now the state is" + i);
        }
    }

    private int genIndexfromResId(int resId) {
        switch (resId) {
            case R.id.signUpRadio8:
                return 0;
            case R.id.signUpRadio10:
                return 1;
            case R.id.signUpRadio12:
                return 2;
            case R.id.signUpRadio14:
                return 3;
            case R.id.signUpRadio16:
                return 4;
            default:
                throw new IllegalArgumentException("the selected item is not in range");

        }
    }
}
