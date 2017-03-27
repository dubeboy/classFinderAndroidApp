package za.co.metalojiq.classfinder.someapp.activity.fragment;


import android.content.SharedPreferences;
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
import za.co.metalojiq.classfinder.someapp.activity.LoginActivity;
import za.co.metalojiq.classfinder.someapp.adapter.EndlessRecyclerViewScrollListener;
import za.co.metalojiq.classfinder.someapp.adapter.NetworkPostAdapter;
import za.co.metalojiq.classfinder.someapp.model.NetworkPostModel;
import za.co.metalojiq.classfinder.someapp.model.NetworkPostsResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;
import za.co.metalojiq.classfinder.someapp.util.Utils;

import java.util.ArrayList;

import static za.co.metalojiq.classfinder.someapp.util.Utils.*;

public class NetworkPost extends Fragment implements NetworkPostAdapter.OnNetworkPostClickListener {
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
    private ArrayList<NetworkPostModel> networksPosts;

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
                fetchNetworkPostData(getArguments().getInt(ARG_PARAM_CAT_ID), page);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        swipeRefreshLayout = (SwipeRefreshLayout) linearLayout.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNetworkPostData(getArguments().getInt(ARG_PARAM_CAT_ID), 1);
                Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setLayoutManager(linearLayoutManager);
        networksPosts = new ArrayList<NetworkPostModel>();
        Log.d(TAG, "Number of elements =" + networksPosts.size()); // = 0
        // I need to load the recycler view only if there are items to load!!!
        networkPostAdapter = new NetworkPostAdapter(networksPosts, R.layout.list_row_networks_post, getActivity().getApplicationContext(), this);
        recyclerView.setAdapter(networkPostAdapter);
        textViewError.setVisibility(View.GONE);
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
        fetchNetworkPostData(getArguments().getInt(ARG_PARAM_CAT_ID), 1);
        return linearLayout;
    }

    private void showNewNetworkDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        NewNetworkPost newNetworkPost = NewNetworkPost.newInstance(mParamCatId, mParamCatName);
        newNetworkPost.show(fm, "fragment_new_network_post");
    }


    private void fetchNetworkPostData(final int catId, final int page) {
        Log.d(TAG, "you scrolling to page: " + page); // page will always be >= 1
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<NetworkPostsResponse> call = apiService.getAllNetworksPost(catId + 1, page);
        Log.d(TAG, "The page is here is more than 0 " + page);
        //FIXME I am not sure if there are new item this top item will show thos new items
        call.enqueue(new Callback<NetworkPostsResponse>() {
            @Override
            public void onResponse(Call<NetworkPostsResponse> call, Response<NetworkPostsResponse> response) {
                if (response.body() != null) {
                    if (response.body().getNetworkPosts().size() != 0) {
                        ArrayList<NetworkPostModel> netPosts;
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
                        Snackbar.make(linearLayout, "No more network topics to load. ", Snackbar.LENGTH_SHORT).show();
                        call.cancel();
                    }
                } else {
                    makeToast("Server Error!...", getContext());
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


    @Override
    public void onNetworkPostClick(NetworkPostModel networkPostModel, NetworkPostAdapter.ITEM_TYPE_CLICK typeClick) {
        switch (typeClick) {
            case POST_IMAGE:
                // TODO this should show an image slider overlay view
                break;
            case LIKE_BUTTON:
                SharedPreferences userSharedPreferences = Utils.getUserSharedPreferences(getContext());
                int userId = userSharedPreferences.getInt(LoginActivity.LOGIN_PREF_USER_ID, 0);
                networkPostAdapter.like();
                like(userId, networkPostModel.getId());
                break;
            case COMMENT_BUTTON:
                //this should show a bottom sheet fragment where i could comment
                break;
        }
    }

    private void like(int userID, int postId) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<NetworkPostsResponse> call = apiService.likeNetworkPost(mParamCatId, postId, userID);
        call.enqueue(new Callback<NetworkPostsResponse>() {
            @Override
            public void onResponse(Call<NetworkPostsResponse> call, Response<NetworkPostsResponse> response) {
                if(response.body() != null) {
                    if (response.body().isLiked()) {
                       // networkPostAdapter.like();
                        makeToast("Liked", getContext());
                    }
                } else {
                    makeToast("Server Error", getContext());
                    networkPostAdapter.unLike();
                }
            }
            @Override
            public void onFailure(Call<NetworkPostsResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
                makeToast("Please connect to the internet", getContext());
                networkPostAdapter.unLike();
            }
        });
    }
}
