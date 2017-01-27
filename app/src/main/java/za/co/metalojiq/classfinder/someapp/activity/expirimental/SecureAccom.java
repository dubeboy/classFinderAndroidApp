package za.co.metalojiq.classfinder.someapp.activity.expirimental;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import za.co.metalojiq.classfinder.someapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecureAccom extends DialogFragment {

//    public static SecureAccom newInstance()

    public SecureAccom() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return inflater.inflate(R.layout.fragment_secure_accom, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}

