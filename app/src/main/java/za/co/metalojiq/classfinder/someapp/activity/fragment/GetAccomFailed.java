package za.co.metalojiq.classfinder.someapp.activity.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import za.co.metalojiq.classfinder.someapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetAccomFailed extends Fragment {


    public GetAccomFailed() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_accom_failed, container, false);
    }

}
