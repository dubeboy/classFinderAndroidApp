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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.adapter.TransactionAdapter;
import za.co.metalojiq.classfinder.someapp.model.Transaction;
import za.co.metalojiq.classfinder.someapp.model.TransactionResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;

import java.util.ArrayList;

public class StudentPanel extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ID = "id";

    // TODO: Rename and change types of parameters
    private int mId;

    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private TextView tvError;

    public StudentPanel() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static StudentPanel newInstance(int id) {
        StudentPanel fragment = new StudentPanel();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getInt(ARG_ID);
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
        getActivity().setTitle("Your bookings");
        load(mId);
        return view;

    }

    private void load(final int studentId) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<TransactionResponse> call = apiService.getBookings(studentId);
        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                ArrayList<Transaction> transactions = response.body().getTransactions();
                if (transactions.size() == 0) {
                    tvError.setText("Nothing to show.");
                    recyclerView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    return;
                }
                recyclerView.setAdapter(new TransactionAdapter(transactions, R.layout.list_item_runner, getActivity()));
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                tvError.setText("Sorry please connect to the internet ");
                recyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
            }
        });

    }
}
