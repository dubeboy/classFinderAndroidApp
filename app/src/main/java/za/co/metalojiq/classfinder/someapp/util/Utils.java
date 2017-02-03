package za.co.metalojiq.classfinder.someapp.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.ArrayRes;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.*;
import za.co.metalojiq.classfinder.someapp.activity.LoginActivity;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by divine on 1/28/17.
 */
public class Utils {

    public static final String[] LOCATIONS = {"Auckland Park", "Braamfontein", "Doornfontein", "Soweto"};
    public static final String AUCK_AREA_PREFIX = "Auckland Park, ";
    public static Spinner setupSpinner(AppCompatActivity context, @IdRes int resId, @ArrayRes int arrResId) {
        Spinner spinner = (Spinner) context.findViewById(resId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(context, arrResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        return spinner;
    }

    public static SharedPreferences getUserSharedPreferences(Context context) {
        return context.getSharedPreferences(LoginActivity.LOGIN_PREF_FILENAME, MODE_PRIVATE);
    }

    public static void makeToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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
