package za.co.metalojiq.classfinder.someapp.activity.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.adapter.EndlessRecyclerViewScrollListener;
import za.co.metalojiq.classfinder.someapp.adapter.NetworkPostAdapter;
import za.co.metalojiq.classfinder.someapp.model.NetworkPostModel;
import za.co.metalojiq.classfinder.someapp.model.NetworkPostsResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;

import java.util.ArrayList;

import static za.co.metalojiq.classfinder.someapp.util.Utils.isLoggedIn;
import static za.co.metalojiq.classfinder.someapp.util.Utils.logIt;
import static za.co.metalojiq.classfinder.someapp.util.Utils.makeToast;

public class NetworkPost extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_CAT_ID = "param1";
    private static final String ARG_PARAM_CAT_NAME = "param2"; //one of them is not needed
    private static final String TAG = NetworkPost.class.getSimpleName();
    private static final String ARGS_NETWORKS_POSTS = "networks_posts";

    // TODO: Rename and change types of parameters
    private int mParamCatId;
    private String mParamCatName;
    private ProgressBar progressBar;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NetworkPostAdapter networkPostAdapter;
    private View linearLayout;
    private  ArrayList<NetworkPostModel> networksPosts;

    public NetworkPost() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static NetworkPost newInstance(int cat, String categoryName, ArrayList<NetworkPostModel> networkPostModels) {
        NetworkPost fragment = new NetworkPost();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_CAT_ID, cat);
        args.putString(ARG_PARAM_CAT_NAME, categoryName);
        args.putSerializable(ARGS_NETWORKS_POSTS, networkPostModels);
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

        linearLayout = inflater.inflate(R.layout.fragment_network_post, container, false);
        logIt(TAG, "In the oncreate if the method of earcth");

        final RecyclerView recyclerView = (RecyclerView) linearLayout.findViewById(R.id.recycler_view);
        final TextView textViewError = (TextView) linearLayout.findViewById(R.id.accomListTvError);
        progressBar = (ProgressBar) linearLayout.findViewById(R.id.accomLoad);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "Called From Networks");
                progressBar.setVisibility(View.VISIBLE);
                fetchNetworkPostData(page, getArguments().getInt(ARG_PARAM_CAT_ID));
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        swipeRefreshLayout = (SwipeRefreshLayout) linearLayout.findViewById(R.id.swipeContainer);
        //TODO not working man on emulator
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                                                                                    android.R.color.holo_green_light,
                                                                                    android.R.color.holo_orange_light,
                                                                                    android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNetworkPostData(0, getArguments().getInt(ARG_PARAM_CAT_ID));
                Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(linearLayoutManager);

//        final ArrayList<NetworkPostModel> networksPosts =
//                (ArrayList<NetworkPostModel>) getArguments().getSerializable(ARGS_NETWORKS_POSTS);

        networksPosts = new ArrayList<NetworkPostModel>();
        fetchNetworkPostData(1, getArguments().getInt(ARG_PARAM_CAT_ID));

            Log.d(TAG, "Number of elemets =" + networksPosts.size()); // = 0
            // I need to load the recycler view only if there are items to load!!!
                networkPostAdapter = new NetworkPostAdapter(networksPosts, R.layout.list_row_networks_post, getActivity().getApplicationContext(), null);
                recyclerView.setAdapter(networkPostAdapter);
                textViewError.setVisibility(View.GONE);
                //Todo(FRAGMENT COMMIT ERROR) Should be an If statement here to Id which fragment
                //TODO: should always load the fragment
                //so that we can change the text to match wheather a person is just loading all accommodation
              //  textViewError.setVisibility(View.VISIBLE);

        final FloatingActionButton fab = (FloatingActionButton) linearLayout.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View view) {
                if (isLoggedIn(getActivity())) {
                    showNewNetworkDialog();
                } else {
                    fab.setVisibility(View.GONE);
                    makeToast("Please sign in to access this action", getContext());
                }
            }
        });
        return linearLayout;
    }
    private void showNewNetworkDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        NewNetworkPost newNetworkPost = NewNetworkPost.newInstance(mParamCatId, mParamCatName);
        newNetworkPost.show(fm, "fragment_new_network_post");
    }


    private void fetchNetworkPostData(final int page, int catId) {
        Log.d(TAG, "you scrolling to page: " + page); // page will always be >= 1
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<NetworkPostsResponse> call = apiService.getAllNetworksPost(page, catId);
        Log.d(TAG, "The page is here is more than 0 " + page);
        //FIXME I am not sure if there are new item this top item will show thos new items
        call.enqueue(new Callback<NetworkPostsResponse>() {
            @Override
            public void onResponse(Call<NetworkPostsResponse> call, Response<NetworkPostsResponse> response) {
                if (response.body().getNetworkPosts().size() != 0) {
                    ArrayList<NetworkPostModel> netPosts ;
                    ArrayList<NetworkPostModel> networksPostsResults = response.body().getNetworkPosts();
                    if (page == 1) {
                        Log.d(TAG, "Page is here reload");
                        networkPostAdapter.clear();
                        networkPostAdapter.addAll(networksPostsResults);
                        scrollListener.resetState();
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        Log.d(TAG, "Page is here loadMore");
                        netPosts = networksPosts;
                        Log.d(TAG, "onResponse: Networks" + netPosts.size());
                        //TODO sould use addALL
                        for (NetworkPostModel networkPost : networksPostsResults) {
                            netPosts.add(networkPost);
                        }
                        networkPostAdapter.notifyItemRangeInserted(networkPostAdapter.getItemCount() + 1, response.body().getNetworkPosts().size());
                    }
                    progressBar.setVisibility(View.GONE);
                } else {
                    Snackbar.make(linearLayout, "No more accommodationds to load. ", Snackbar.LENGTH_SHORT).show();
                    call.cancel();
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<NetworkPostsResponse> call, Throwable t) {
                Log.d(TAG, t.toString());
                Snackbar.make(linearLayout, "Please connect to the internet.",
                        Snackbar.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}
