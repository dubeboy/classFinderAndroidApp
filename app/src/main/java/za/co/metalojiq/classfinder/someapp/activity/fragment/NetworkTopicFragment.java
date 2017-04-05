package za.co.metalojiq.classfinder.someapp.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.network.NETWORK_TYPE;
import za.co.metalojiq.classfinder.someapp.activity.network.NetworkPostActivity;
import za.co.metalojiq.classfinder.someapp.adapter.EndlessRecyclerViewScrollListener;
import za.co.metalojiq.classfinder.someapp.adapter.MyNetworkTopicRecyclerViewAdapter;
import za.co.metalojiq.classfinder.someapp.model.network.Network;
import za.co.metalojiq.classfinder.someapp.model.network.NetworkResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;

import java.util.ArrayList;

import static za.co.metalojiq.classfinder.someapp.util.Utils.makeToast;

public class NetworkTopicFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_NETWORK_CAT_ID = "column-count";
    private static final String TAG = "NetworkTopicFragment";
    private static final String ARG_NETWORKS_NAME = "networks_post";
    private static final String ARG_POST_TYPE = "network_post_type";
    private static final String TAG_FRAGMENT = "net_posts";
    // TODO: Customize parameters
    private int mNetworkCatId = 1;
    private MyNetworkTopicRecyclerViewAdapter.OnListFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EndlessRecyclerViewScrollListener scrollListener;
    private MyNetworkTopicRecyclerViewAdapter adapter;
    private String mNetworkName;
    public static final String NETWORK_CAT_ID = "network_cat_id";
    public static final String NETWORK_NAME = "network_name";
    public static final String NETWORK_ID = "net_id_100";
    private NETWORK_TYPE mPostType;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NetworkTopicFragment() {
    }

    public static NetworkTopicFragment newInstance(int networkCatId, String netWorksName, NETWORK_TYPE postType) {
        NetworkTopicFragment fragment = new NetworkTopicFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NETWORK_CAT_ID, networkCatId);
        args.putString(ARG_NETWORKS_NAME, netWorksName);
        args.putSerializable(ARG_POST_TYPE, postType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mNetworkCatId = getArguments().getInt(ARG_NETWORK_CAT_ID);
            mNetworkName = getArguments().getString(ARG_NETWORKS_NAME);
            mPostType = (NETWORK_TYPE) getArguments().getSerializable(ARG_POST_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network_topic_list, container, false);


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        //TODO not working man on emulator
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                fetchMoreNetworkTopics(1);
                Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
            }
        });

        swipeRefreshLayout.setRefreshing(true);  // make it refreshing at the beginning

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayout) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "Called");
                swipeRefreshLayout.setRefreshing(true);
                fetchMoreNetworkTopics(page);
            }
        };

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.networks_topics_list);
        recyclerView.addOnScrollListener(scrollListener); //TODO test if position matters
        recyclerView.setLayoutManager(linearLayout);

        adapter = new MyNetworkTopicRecyclerViewAdapter(new ArrayList<Network>(),
                                                new MyNetworkTopicRecyclerViewAdapter
                                                        .OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Network item) {
                Intent intent = new Intent(getContext(), NetworkPostActivity.class);
                intent.putExtra(NETWORK_CAT_ID, mNetworkCatId);
                intent.putExtra(NETWORK_NAME, mNetworkName);
                intent.putExtra(NETWORK_ID, item.getId());
                getActivity().startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
        fetchMoreNetworkTopics(1);
        return view;
    }

    //get all network caetogory networks ++
    private void fetchMoreNetworkTopics(final int page) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<NetworkResponse> call = apiService.getAllNetworkTopics(page, mNetworkCatId + 1, mPostType.ordinal()); // +1 because server is 1 based

        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                if (response.body() != null) {
                    if (page == 1) {  // the is when the we reload
                        adapter.clear();
                        scrollListener.resetState();
                    }
                    adapter.addAll(response.body().getNetworks());
                } else {
                    Log.d(TAG, "onResponse: server error ma nikka sorry man");
                    makeToast("Server Error", getContext());
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                makeToast("Please connect to the internet", getContext());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
