package za.co.metalojiq.classfinder.someapp.activity.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import za.co.metalojiq.classfinder.someapp.R;

/**
 * Created by divine on 1/27/17.
 */
public class TimePickerFragment extends ListBottomSheet {
    private static final String TAG = TimePickerFragment.class.getSimpleName();
    public static final String[] times = {"8:00", "10:00", "12:00", "14:00", "16:00"};

    public TimePickerFragment() {
        // Required empty public constructor
        ListBottomSheet.newInstance("Select view time", times);
    }

}
