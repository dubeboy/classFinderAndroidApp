package za.co.metalojiq.classfinder.someapp.activity.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import za.co.metalojiq.classfinder.someapp.R;

/**
 * Created by divine on 2/23/17.
 */
public class ListBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = ListBottomSheet.class.getSimpleName();
    private static final String ITEMS = "items";
    private static final String NAME = "sheet_name";
    private  String[] items;
    private ListBottomSheet.OnSelectTime listener;

    public interface OnSelectTime {
        void onItemSelected(String time);
    }


    public static ListBottomSheet newInstance(String name, String[] its) {
        Bundle args = new Bundle();
        args.putSerializable(ListBottomSheet.ITEMS, its);
        args.putString(ListBottomSheet.NAME, name);
        ListBottomSheet listBottomSheet = new ListBottomSheet();
        listBottomSheet.setArguments(args);
        return listBottomSheet;
    }

    public ListBottomSheet() {
        // Required empty public constructor

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
//        dialog.setTitle(getArguments().getString(ListBottomSheet.NAME));
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return inflater.inflate(R.layout.fragment_time_picker, container);
    }

    public  void setListener(ListBottomSheet.OnSelectTime listener) {
        this.listener = listener;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView timeList = (ListView) view.findViewById(R.id.timePickerList);
        TextView tvTitle = (TextView) view.findViewById(R.id.title_frag_time);
        tvTitle.setText(getArguments().getString(ListBottomSheet.NAME));
        items = (String[]) getArguments().getSerializable(ListBottomSheet.ITEMS);
        Log.d(TAG, "The the Generic Bottom sheet list " + timeList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);
        timeList.setAdapter(adapter);
        timeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onItemSelected(items[position]);
                Log.d(TAG, "onItemClick: ont the bottom of the page");
                dismiss();
            }
        });
    }
}
