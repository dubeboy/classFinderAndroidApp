package za.co.metalojiq.classfinder.someapp.activity.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by divine on 3/1/17.
 */
public class PickTimeFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private OnTimeIsSet onTimeSetListener;

    public interface OnTimeIsSet {
        void timeSet(int hour, int minute );
    }


    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                true);
    }

    public void setOnTimeSetListener(OnTimeIsSet onTimeSetListener) {
        this.onTimeSetListener = onTimeSetListener;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        onTimeSetListener.timeSet(hourOfDay, minute);
    }
}
