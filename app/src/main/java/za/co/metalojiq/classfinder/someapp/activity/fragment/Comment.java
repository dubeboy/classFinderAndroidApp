package za.co.metalojiq.classfinder.someapp.activity.fragment;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import za.co.metalojiq.classfinder.someapp.adapter.CommentsAdapter;
import za.co.metalojiq.classfinder.someapp.adapter.EndlessRecyclerViewScrollListener;
import za.co.metalojiq.classfinder.someapp.model.PostResponse;
import za.co.metalojiq.classfinder.someapp.model.network.NetworkPostComments;
import za.co.metalojiq.classfinder.someapp.model.network.NetworkPostModel;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;

import java.util.ArrayList;

import static za.co.metalojiq.classfinder.someapp.util.Utils.makeToast;


//this is the fragment that we will always call comments or comments
public class Comment extends BottomSheetDialogFragment {
    private static final String ARG_USER_ID = "param1";
    private static final String ARG_POST_ID = "param2";
    private static final String ARG_NETWORK_ID = "param3";

    // TODO: Rename and change types of parameters
    private int mUserId;
    private int mPostId;
    private Button btnComment;
    private EditText etComment;
    private CommentsAdapter commentsAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int mNetworkId;


    public Comment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Comment newInstance(int networkId, int userId, int postId) {
        Comment fragment = new Comment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        args.putInt(ARG_POST_ID, postId);
        args.putInt(ARG_NETWORK_ID, networkId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getInt(ARG_USER_ID);
            mPostId = getArguments().getInt(ARG_POST_ID);
            mNetworkId = getArguments().getInt(ARG_NETWORK_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        btnComment = (Button) view.findViewById(R.id.btn_frag_comment);
        etComment = (EditText) view.findViewById(R.id.et_comment);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        RecyclerView commentsList = (RecyclerView) view.findViewById(R.id.comments_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        commentsAdapter = new CommentsAdapter(R.layout.row_item_comment,
                new ArrayList<NetworkPostComments>(), getContext());

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getAllComments(page);
            }
        };
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllComments(1);
                Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
            }
        });

        commentsList.setLayoutManager(linearLayoutManager);
        commentsList.setAdapter(commentsAdapter);
        commentsList.addOnScrollListener(scrollListener);


        // this is where the user comments
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm(etComment.getText().toString())) {
                    postComment(mNetworkId, mUserId, mPostId, etComment.getText().toString());
                } else {
                    etComment.setError("You can not have a blank comment!");
                }
            }
        });
       // getAllComments(1);
        return view;
    }

    private boolean validateForm(String comment) {
        return !TextUtils.isEmpty(comment);
    }

    private void getAllComments(final int page) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<NetworkPostModel> call = apiService.getAllComments(mNetworkId, mUserId, mPostId);
        call.enqueue(new Callback<NetworkPostModel>() {
            @Override
            public void onResponse(Call<NetworkPostModel> call, Response<NetworkPostModel> response) {
                if (response.body() != null) {
                    if (page == 1) {  // this when we refreshing the page!
                        commentsAdapter.clear();
                        commentsAdapter.addAll(response.body().getNetworkPostComments());
                        scrollListener.resetState();
                    } else {
                        commentsAdapter.addAll(response.body().getNetworkPostComments());
                    }
                } else {
                    makeToast("Server Error.", getContext());
                }
            }
            @Override
            public void onFailure(Call<NetworkPostModel> call, Throwable t) {

            }
        });

    }

    private void postComment(int networkId, int userId, int postId, String comment) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PostResponse> call = apiService.postComment(networkId, postId,userId, comment);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.body() != null) {
                    if (response.body().isStatus()) {
                        makeToast("Comment shared ", getContext());
                    } else {
                        makeToast("Sorry could not share comment.", getContext());
                    }
                } else {
                    makeToast("Server Error!!", getContext());
                }
            }
            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                makeToast("Please connect to the internet to comment.", getContext());
            }
        });
    }
}
