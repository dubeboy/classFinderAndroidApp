package za.co.metalojiq.classfinder.someapp.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import za.co.metalojiq.classfinder.someapp.adapter.EndlessRecyclerViewScrollListener;
import za.co.metalojiq.classfinder.someapp.adapter.MyNetworkTopicRecyclerViewAdapter;
import za.co.metalojiq.classfinder.someapp.model.NetworkPostModel;
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
    private static final String TAG_FRAGMENT = "net_posts";
    // TODO: Customize parameters
    private int mNetworkCatId = 1;
    private MyNetworkTopicRecyclerViewAdapter.OnListFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EndlessRecyclerViewScrollListener scrollListener;
    private MyNetworkTopicRecyclerViewAdapter adapter;
    private String mNetworkName;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NetworkTopicFragment() {
    }

    public static NetworkTopicFragment newInstance(int networkCatId, String netWorksName) {
        NetworkTopicFragment fragment = new NetworkTopicFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NETWORK_CAT_ID, networkCatId);
        args.putString(ARG_NETWORKS_NAME, netWorksName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mNetworkCatId = getArguments().getInt(ARG_NETWORK_CAT_ID);
            mNetworkName = getArguments().getString(ARG_NETWORKS_NAME);
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

                fetchMoreNetworks(1);
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
                fetchMoreNetworks(page);
            }
        };


        // Set the adapter
        //  Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.networks_topics_list);
        recyclerView.addOnScrollListener(scrollListener); //TODO test if position matters
        recyclerView.setLayoutManager(linearLayout);

        adapter = new MyNetworkTopicRecyclerViewAdapter(
                new ArrayList<Network>(), new MyNetworkTopicRecyclerViewAdapter.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Network item) {
                NetworkPost networkPost = NetworkPost.newInstance(mNetworkCatId, mNetworkName, new ArrayList<NetworkPostModel>());
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.list, networkPost, TAG_FRAGMENT)
                        .addToBackStack(null)
                        .commit();
            }
        });
        recyclerView.setAdapter(adapter);
        fetchMoreNetworks(1);
        return view;
    }

    //get all network caetogory networks ++
    private void fetchMoreNetworks(final int page) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<NetworkResponse> call = apiService.getAllNetworks(page, mNetworkCatId);

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
