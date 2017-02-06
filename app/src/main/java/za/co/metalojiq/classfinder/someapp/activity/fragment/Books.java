package za.co.metalojiq.classfinder.someapp.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.TextView;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.adapter.BooksAdapter;
import za.co.metalojiq.classfinder.someapp.model.Book;

import java.util.ArrayList;

public class Books extends Fragment {

    private static final String BOOKS_BUNDLE = "Book";

    // TODO: Rename and change types of parameters

    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private TextView tvError;

   private ArrayList<Book> books;

    public Books() {
        // Required empty public constructor
    }

    public static Books newInstance(ArrayList<Books> books) {
        Books fragment = new Books();
        Bundle args = new Bundle();
        args.putSerializable(BOOKS_BUNDLE, books);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            books = (ArrayList<Book>) getArguments().getSerializable(BOOKS_BUNDLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_runner, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.runner_list);
        mProgressBar = (ProgressBar) view.findViewById(R.id.runnerTransLoad); // Todo should rename
        tvError = (TextView) view.findViewById(R.id.runnerErrorMsg);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getActivity().setTitle("All books");
        load();
        return view;
    }

    private void load() {
        if (books.size() == 0) {
            tvError.setText("Nothing to show...");
            recyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            return;
        } else {
            recyclerView.setAdapter(new BooksAdapter(books, R.layout.list_row_books, getActivity(), null ));
            mProgressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        }

    }
}
