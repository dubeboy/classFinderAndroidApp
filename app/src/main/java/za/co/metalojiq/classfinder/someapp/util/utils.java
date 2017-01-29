package za.co.metalojiq.classfinder.someapp.util;

import android.support.annotation.ArrayRes;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by divine on 1/28/17.
 */
public class utils {

    public static Spinner setupSpinner(AppCompatActivity context, @IdRes int resId, @ArrayRes int arrResId) {
        Spinner spinner = (Spinner) context.findViewById(resId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(context, arrResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        return spinner;
    }

    public static void getUserInfo() {

    }
}
