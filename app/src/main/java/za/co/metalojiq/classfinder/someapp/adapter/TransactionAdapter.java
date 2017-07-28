package za.co.metalojiq.classfinder.someapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.model.Transaction;

/**
 * Created by divine on 1/18/17.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.UserViewHolder> {
    private static final String TAG = TransactionAdapter.class.getSimpleName();
    private final OnClickListener onClick;
    //row row of data
    private int rowLayout;

    private List<Transaction> transactions;
    private Context context;


    public TransactionAdapter(List<Transaction> transactions, int rowLayout, Context context, OnClickListener onClick) {
        Log.d(TAG, "here are the transactions" + transactions);
        this.rowLayout = rowLayout;
        this.transactions = transactions;
        this.context = context;
        this.onClick = onClick;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        Log.d(TAG, "GEtting the transaction for item: " + transactions.get(position).getStudentEmail());
        holder.bind(transactions.get(position));
        holder.setOnClickListener(onClick);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "the number of eleemts for runner is " + transactions.size());
        return transactions.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView runnerLocation;
        TextView runnerStudentEmail;
        TextView runnerStudentCell;
        TextView runnerTime;
        OnClickListener onClickListener;

        UserViewHolder(View itemView) {
            super(itemView);
            runnerLocation = (TextView) itemView.findViewById(R.id.runnerLocation);
            runnerStudentEmail = (TextView) itemView.findViewById(R.id.runnerEmail);
            runnerStudentCell = (TextView) itemView.findViewById(R.id.runnerCell);
            runnerTime = (TextView) itemView.findViewById(R.id.runnerTime);
        }

        void bind(final Transaction transaction) {
            Log.d(TAG, "Loading runner transactions " + transaction.getStudentEmail());
            runnerLocation.setText(transaction.getLocation());
            runnerStudentEmail.setText(transaction.getStudentEmail());
            runnerStudentCell.setText(transaction.getStudentContact());
            runnerTime.setText(transaction.getViewTime());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.click(transaction);
                }
            });
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }


    }

    public void addAll(ArrayList<Transaction> transactions) {
        transactions.addAll(transactions);
        notifyDataSetChanged();
    }


    public interface OnClickListener {
        void click(Transaction transaction);
    }
}
