package za.co.metalojiq.classfinder.someapp.activity.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.AccomImageSlider;
import za.co.metalojiq.classfinder.someapp.activity.MainActivity;
import za.co.metalojiq.classfinder.someapp.adapter.AccomAdapter;
import za.co.metalojiq.classfinder.someapp.model.Accommodation;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccomList extends Fragment {


    public  static final String PICTURES_ARRAY_EXTRA = MainActivity.TAG + ".PICTURES_ARRAY_LIST";
    public  static final String DOUBLE_PRICE_EXTRA = MainActivity.TAG + ".DOUBLE_PRICE";
    public  static final String STRING_ROOM_TYPE_EXTRA = MainActivity.TAG + ".STRING_ROOM_TYPE";
    public  static final String STRING_ROOM_LOCATION_EXTRA = MainActivity.TAG + ".STRING_ROOM_LOCATION";
    private static final String TAG = AccomList.class.getSimpleName();
    public static final String ACCOM_BUNDLE_KEY = TAG + ".ACCOM_KEY";

    public AccomList() {
        // Required empty public constructor
    }

    public static AccomList newInstance(ArrayList<Accommodation> accommodations) {
        Log.d(TAG, "Inherererereererere");
        Bundle args = new Bundle();
        args.putSerializable(ACCOM_BUNDLE_KEY, accommodations);
        AccomList fragment = new AccomList();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View linearLayout = inflater.inflate(R.layout.fragment_accom_list, container, false);

        final RecyclerView recyclerView = (RecyclerView) linearLayout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        //// TODO: 1/13/17  make it better please
        ArrayList<Accommodation> accommodations = (ArrayList<Accommodation>) getArguments().getSerializable(ACCOM_BUNDLE_KEY);

        Log.d(TAG, "Number of elemets =" + accommodations.size());

        recyclerView.setAdapter(new AccomAdapter(accommodations,
                R.layout.list_item_accom, getActivity().getApplicationContext(), new AccomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Accommodation accommodation) {
                Intent intent = new Intent(getActivity().getApplicationContext(), AccomImageSlider.class);
                intent.putStringArrayListExtra(PICTURES_ARRAY_EXTRA, accommodation.getImagesUrls()); // TODO: 1/11/17 google how to add an arraylist to a put extra
                intent.putExtra(DOUBLE_PRICE_EXTRA, accommodation.getPrice());
                intent.putExtra(STRING_ROOM_TYPE_EXTRA, accommodation.getRoomType());
                intent.putExtra(STRING_ROOM_LOCATION_EXTRA, accommodation.getLocation());
                startActivity(intent);
            }
        }));
        return  linearLayout;

    }
}
