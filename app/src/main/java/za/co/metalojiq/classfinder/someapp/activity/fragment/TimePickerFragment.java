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
public class TimePickerFragment extends BottomSheetDialogFragment {
    private static final String TAG = TimePickerFragment.class.getSimpleName();
    private final String[] times = {"8:00", "10:00", "12:00", "14:00", "16:00"};
    private  OnSelectTime listener;

    public interface OnSelectTime {
        void onTimeSelected(String time);
    }



    public TimePickerFragment() {
        // Required empty public constructor

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Select Time");
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return inflater.inflate(R.layout.fragment_time_picker, container);
    }

    public  void setListener(OnSelectTime listener) {
        this.listener = listener;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView timeList = (ListView) view.findViewById(R.id.timePickerList);
        Log.d(TAG, "The the timelist " + timeList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, times);
        timeList.setAdapter(adapter);
        timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onTimeSelected(times[position]);
                dismiss();
            }
        });
    }
}
