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

        // Inflate the layout for this fragment
        final RecyclerView recyclerView = (RecyclerView) linearLayout.findViewById(R.id.recycler_view);
        final TextView textViewError = (TextView) linearLayout.findViewById(R.id.accomListTvError);
        progressBar = (ProgressBar) linearLayout.findViewById(R.id.accomLoad);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "Called");
                progressBar.setVisibility(View.VISIBLE);
                fetchNetworkData(page, getArguments().getInt(ARG_PARAM_CAT_ID));
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
                fetchNetworkData(0, getArguments().getInt(ARG_PARAM_CAT_ID));
                Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(linearLayoutManager);

        //// TODO: 1/13/17  make it better please move this the onCreate Hook
        final ArrayList<NetworkPostModel> networksPosts =
                (ArrayList<NetworkPostModel>) getArguments().getSerializable(ARGS_NETWORKS_POSTS);

        if (networksPosts != null) {  //I think its redundant.
            Log.d(TAG, "Number of elemets =" + networksPosts.size());
            // I need to load the recycler view only if there are items to load!!!
            if (networksPosts.size() > 0) {
                networkPostAdapter = new NetworkPostAdapter(networksPosts, R.layout.list_row_networks_post, getActivity().getApplicationContext(), null);
                recyclerView.setAdapter(networkPostAdapter);
            } else {
                //Todo(FRAGMENT COMMIT ERROR) Should be an If statement here to Id which fragment
                //TODO: should always load the fragment
                //so that we can change the text to match wheather a person is just loading all accommodation
                textViewError.setVisibility(View.VISIBLE);
            }
        }

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


    private void fetchNetworkData(final int page, int catId) {
        Log.d(TAG, "you scrolling to page: " + page);
//        if (page == 1) {
//            return;
//        }  // dont do anything if page is equal to one

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<NetworkPostsResponse> call;
        //
        if (page <= 1) {
            Log.d(TAG, "The page is here " + page);
            call = apiService.getAllNetworkPosts(1, catId);
        } else {
            Log.d(TAG, "The page is here > 0 " + page);
            call = apiService.getAllNetworkPosts(page, catId);
        }
        //FIXME I am not sure if there are new item this top item will show thos new items
        call.enqueue(new Callback<NetworkPostsResponse>() {
            @Override
            public void onResponse(Call<NetworkPostsResponse> call, Response<NetworkPostsResponse> response) {
                if (response.body().getNetworkPosts().size() != 0) {
                    ArrayList<NetworkPostModel> networkPosts;
                    ArrayList<NetworkPostModel> networksPostsResults = response.body().getNetworkPosts();
                    if (page <= 0) {
                        Log.d(TAG, "Page is here reload");
                        networkPostAdapter.clear();
                        networkPostAdapter.addAll(networksPostsResults);
                        scrollListener.resetState();
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        Log.d(TAG, "Page is here loadMore");
                        networkPosts = (ArrayList<NetworkPostModel>) getArguments().getSerializable(ARGS_NETWORKS_POSTS);
                        Log.d(TAG, "onResponse: Accommodations" + networkPosts.size());
                        //TODO sould use addALL
                        for (NetworkPostModel networkPost : networksPostsResults) {
                            networkPosts.add(networkPost);
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
                Snackbar.make(linearLayout, "Oops... failing to load more items. Please try connecting to the internet.",
                        Snackbar.LENGTH_SHORT).show();
            }
        });

    }
}
