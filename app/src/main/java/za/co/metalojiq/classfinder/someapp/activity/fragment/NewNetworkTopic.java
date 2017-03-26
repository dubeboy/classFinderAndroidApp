package za.co.metalojiq.classfinder.someapp.activity.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.model.network.NetworkResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;

import static za.co.metalojiq.classfinder.someapp.util.Utils.makeToast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewNetworkTopic#newInstance} factory method to
 * A network topic is a Network
 */
public class NewNetworkTopic extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_ID = "param1";
    private static final String ARG_NETWORK_CATEGORY = "param2";
    ProgressDialog dialog;

    // TODO: Rename and change types of parameters
    private int mUserId;
    private int mNetworkCategoryId;
    private String TAG = NewNetworkTopic.class.getSimpleName();

    public NewNetworkTopic() {
        // Required empty public constructor
    }

    /**
     * @param userId          the required to be sent to server so that we can tell who create the topic
     * @param networkCategory the category in which the network belongs
     * @return new network topic fragment
     */
    public static NewNetworkTopic newInstance(int userId, int networkCategory) {
        NewNetworkTopic newNetworkTopic = new NewNetworkTopic();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        args.putInt(ARG_NETWORK_CATEGORY, networkCategory);
        newNetworkTopic.setArguments(args);
        return newNetworkTopic;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getInt(ARG_USER_ID);
            mNetworkCategoryId = getArguments().getInt(ARG_NETWORK_CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().setTitle("Add new network topic");
        return inflater.inflate(R.layout.fragment_new_network_topic, container, false);
    }


    // Set reference to the widgets once the view has been created
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Button btnNew;
        btnNew = (Button) view.findViewById(R.id.btn_add_new_network_topic);
        final EditText etNetworkName;
        etNetworkName = (EditText) view.findViewById(R.id.etNewTopic);
        final EditText etDescription;
        etDescription = (EditText) view.findViewById(R.id.etDescription);

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getText().toString();
                String networkName = etNetworkName.getText().toString();
                if (isFormValid(description, networkName)) {
                    //set dialog first
                    dialog = ProgressDialog.show(getContext(), "", "Creating new network topic.");
                    // make network call here
                    addNetworkTopic(mNetworkCategoryId, networkName, description, mUserId);
                }  else {
                    makeToast("Please fill in all fields", getContext());
                }
            }
        });
    }

    private boolean isFormValid(String desc, String networkName) {
        return !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(networkName);
    }


    private void addNetworkTopic(int network_cat_id, String topic, String descripition, int userId) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<NetworkResponse> call = apiInterface.postNewNetwork(userId, network_cat_id, topic, descripition);

        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                if (response.body() != null) {
                    Toast.makeText(getContext(), "Successfully created new topic", Toast.LENGTH_SHORT).show();
                    //TODO : we should start a new activity leading to that new networks page where they can now post
                } else {
                    Log.d(TAG, "onResponse: " + "server error");
                }
                dialog.dismiss();
                dismiss();
            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Please connect to the internet.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        });
    }
}
