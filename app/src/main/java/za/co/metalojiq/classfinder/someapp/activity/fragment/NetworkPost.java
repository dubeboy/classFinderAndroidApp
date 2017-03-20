package za.co.metalojiq.classfinder.someapp.activity.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import za.co.metalojiq.classfinder.someapp.R;

public class NetworkPost extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_CAT_ID = "param1";
    private static final String ARG_PARAM_CAT_NAME = "param2"; //one of them is not needed

    // TODO: Rename and change types of parameters
    private int mParamCatId;
    private String mParamCatName;


    public NetworkPost() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static NetworkPost newInstance(int cat, String categoryName) {
        NetworkPost fragment = new NetworkPost();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_CAT_ID, cat);
        args.putString(ARG_PARAM_CAT_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamCatId = getArguments().getInt(ARG_PARAM_CAT_ID);
            mParamCatName = getArguments().getString(ARG_PARAM_CAT_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_network_post, container, false);

        return view;
    }

    private void getData(int cat) {

    }
}
