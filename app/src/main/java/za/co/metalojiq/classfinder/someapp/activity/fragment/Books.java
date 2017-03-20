package za.co.metalojiq.classfinder.someapp.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import za.co.metalojiq.classfinder.someapp.activity.NewBooks;
import za.co.metalojiq.classfinder.someapp.adapter.BooksAdapter;
import za.co.metalojiq.classfinder.someapp.adapter.EndlessRecyclerViewScrollListener;
import za.co.metalojiq.classfinder.someapp.model.Book;
import za.co.metalojiq.classfinder.someapp.model.BooksResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;

import java.util.ArrayList;

import static za.co.metalojiq.classfinder.someapp.util.Utils.makeToast;

public class Books extends Fragment {

    private static final String BOOKS_BUNDLE = "Book";

    // TODO: Rename and change types of parameters

    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private TextView tvError;

    private EndlessRecyclerViewScrollListener scrollListener;
    private String TAG = Books.class.getSimpleName();
    private ProgressBar progressBar;
    private BooksAdapter booksAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean onRefresh = false;

    public Books() {
        // Required empty public constructor
    }

    public static Books newInstance(ArrayList<Book> books) {
        Books fragment = new Books();
        Bundle args = new Bundle();
        args.putSerializable(BOOKS_BUNDLE, books);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_runner, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.runner_list);
        mProgressBar = (ProgressBar) view.findViewById(R.id.runnerTransLoad); // Todo should rename
        progressBar = (ProgressBar) view.findViewById(R.id.runnerLoadMore);

        tvError = (TextView) view.findViewById(R.id.runnerErrorMsg);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);

        getActivity().setTitle("Books");

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), NewBooks.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        //TODO not working man on emulator
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchAccomData(1, true);
                Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
            }
        });

//        load();
        booksAdapter = new BooksAdapter((ArrayList<Book>) getArguments().getSerializable(BOOKS_BUNDLE), R.layout.list_row_books, getActivity(), null);
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "Called");
                progressBar.setVisibility(View.VISIBLE);
                fetchAccomData(page, false);
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setAdapter(booksAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        return view;
    }

    /**
     *
     * @param page - this is the page of the next item to load. total element is 6 * page
     * @param onRefresh
     */
    private void fetchAccomData(final int page, final boolean onRefresh) {
        Log.d(TAG, "you scrolling to page: " + page);
//        if (page == 1) {
//            return;
//        }  // dont do anything if page is equal to one

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<BooksResponse> call;
        //

        if (page > 1 || onRefresh) {
            //FIXME I am not sure if there are new item this top item will show thos new items
            call = apiService.getBooks(page);
            call.enqueue(new Callback<BooksResponse>() {
                @Override
                public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
               //    booksAdapter = new BooksAdapter((ArrayList<Book>) getArguments().getSerializable(BOOKS_BUNDLE), R.layout.list_row_books, getActivity(), null);
                    if (response.body().getBooks().size() != 0) {
                        ArrayList<Book> books;
                        ArrayList<Book> booksResult = response.body().getBooks();
                        Log.d(TAG, "onResponse: the size of books is " + booksResult.size());
                        if (onRefresh) {
                            Log.d(TAG, "Page is here reload");
                            booksAdapter.clear();
                            booksAdapter.addAll(booksResult);
                            scrollListener.resetState();
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            Log.d(TAG, "Page is here loadMore");
                            books = (ArrayList<Book>) getArguments().getSerializable(BOOKS_BUNDLE);
                            Log.d(TAG, "onResponse: Accommodations" + books.size());
                            //TODO sould use addALL
                            for (Book book : booksResult) {
                                books.add(book);
                            }
                            booksAdapter.notifyItemRangeInserted(booksAdapter.getItemCount() + 1, response.body().getBooks().size());
                        }
                        progressBar.setVisibility(View.GONE);
                    } else {
                        makeToast("No more books to load. ", getActivity());
                        call.cancel();
                    }
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onFailure(Call<BooksResponse> call, Throwable t) {
                    Log.d(TAG, t.toString());
                    makeToast("Please connect to the internet.", getActivity());
                }
            });
        }
    }

   // @Deprecated
//    private void load() {
//        Log.d("Books", "load: onLoad" + books);
//        booksAdapter = new BooksAdapter(books, R.layout.list_row_books, getActivity(), null);
//        if (books.size() == 0) {
//            tvError.setText("Nothing to show...");
//            recyclerView.setVisibility(View.GONE);
//            mProgressBar.setVisibility(View.GONE);
//        } else {
//            Log.d("Books", "load: hello there  inhere");
//            recyclerView.setAdapter(booksAdapter);
//            mProgressBar.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);
//            mProgressBar.setVisibility(View.GONE);
//        }
//
//    }
}
