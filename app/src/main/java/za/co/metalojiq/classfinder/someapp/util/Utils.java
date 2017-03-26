package za.co.metalojiq.classfinder.someapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.ArrayRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import za.co.metalojiq.classfinder.someapp.activity.LoginActivity;
import za.co.metalojiq.classfinder.someapp.activity.fragment.BookSearchFaculty;

import static android.content.Context.MODE_PRIVATE;
import static za.co.metalojiq.classfinder.someapp.activity.LoginActivity.LOGIN_IS_RUNNER;
import static za.co.metalojiq.classfinder.someapp.activity.LoginActivity.LOGIN_PREF_USER_ID;

/**
 * Created by divine on 1/28/17.
 */
public class Utils {

    public static final String[] LOCATIONS = {"Auckland Park", "Braamfontein", "Doornfontein", "Soweto"};
    public static final String[] INSTITUTIONS = {"Wits", "UJ-Auckland Park", "UJ-DFC", "UJ-Soweto", "Other"};
    public static final String AUCK_AREA_PREFIX = "Auckland Park, ";

    /**
     *
     * this method setup a general spinner
     *
     * @param context - the activity which is going to render this spinner
     * @param resId - the spinner that we dealing with :)
     * @param arrResId - the xml res ID which as an array of elements to populate the array with
     * @return - the spinner which has been created, so that we can make any required modification :)
     */

    public static Spinner setupSpinner(AppCompatActivity context, @IdRes int resId, @ArrayRes int arrResId) {
        Spinner spinner = (Spinner) context.findViewById(resId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(context, arrResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        return spinner;
    }

    //These 2 can be combined
    public static int genIDForSelectedFaculty(String faculty) {
        for (int i = 0; i < BookSearchFaculty.FACULTIES.length; i++) {
            if(BookSearchFaculty.FACULTIES[i].equals(faculty))
                return i+2; // the server is 1 based an the first element is accommodation
        }
        return -1;
    }

    public static int genIDForSelectedInstitution(String institution) {
        for (int i = 0; i < INSTITUTIONS.length; i++) {
            if(INSTITUTIONS[i].equals(institution))
                return i+1;
        }
        return -1;
    }


    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        Log.d("Utils", password);
        return password.length() >= 4;
    }

    @NonNull
    public static GoogleApiClient getGoogleSignUp(Context context,
                                                  GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("928443959691-bvf8ptbuphctke0sfcifocms8ejogchi.apps.googleusercontent.com")
                .requestEmail()
                .build();

        return new GoogleApiClient.Builder(context)
                .enableAutoManage((FragmentActivity) context /* FragmentActivity */, onConnectionFailedListener /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    //fixme please really bad!!!
    public static boolean isEmailValid(String email) {
        //TODO: Replace regex NB!!!!!!!!
        return email.contains("@");
    }

    public static SharedPreferences getUserSharedPreferences(Context context) {
        return context.getSharedPreferences(LoginActivity.LOGIN_PREF_FILENAME, MODE_PRIVATE);
    }

    public static boolean isLoggedIn(Context context) {
        return (getUserSharedPreferences(context).getInt(LOGIN_PREF_USER_ID, 0) > 0) ;
    }


    public static boolean isRunner(Context context) {
        return getUserSharedPreferences(context).getBoolean(LOGIN_IS_RUNNER, false);
    }

    public static void makeToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void l(String msg) {
        Log.d("Debug", msg);
    }

    public static void logIt(String id, String msg) {
        Log.d(id, msg);
    }

    public static String getMimeType(String image) {
        int indexOf = image.lastIndexOf(".") + 1;
        String s = image.substring(indexOf);
        if (s.equals("jpg")) {
            s = "jpeg";
        }
        return "image/" + s;
    }

    public static void getUserInfo() {

    }
    public static class LocationItemListener implements AdapterView.OnItemSelectedListener {

        TextView tvAuck;
        Spinner auckAreaSpinner;

        public LocationItemListener(TextView tvAuck, Spinner auckAreaSpinner) {
            this.tvAuck = tvAuck;
            this.auckAreaSpinner = auckAreaSpinner;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selected = (String) parent.getItemAtPosition(position);
            if (selected.equals(LOCATIONS[0])) {
                tvAuck.setVisibility(View.VISIBLE);
                auckAreaSpinner.setVisibility(View.VISIBLE);
                auckAreaSpinner.setSelection(-1);
            } else {
                tvAuck.setVisibility(View.GONE);
                auckAreaSpinner.setVisibility(View.GONE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }



}
