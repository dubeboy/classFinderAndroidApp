package za.co.metalojiq.classfinder.someapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.model.User;

/**
 * Created by divine on 1/18/17.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    //row row of data
    private int rowLayout;
    private List<User> users;
    private Context context;


    public UserAdapter(List<User> users, int rowLayout, Context context) {
        this.rowLayout = rowLayout;
        this.users = users;
        this.context = context;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

     static class UserViewHolder extends RecyclerView.ViewHolder {
         TextView runnerLocation;
         TextView runnerEmail;
         TextView runnerCell;
         TextView runnerTime;
         public UserViewHolder(View itemView) {
             super(itemView);
             runnerLocation = (TextView) itemView.findViewById(R.id.runnerLocation);
             runnerEmail = (TextView) itemView.findViewById(R.id.runnerEmail);
             runnerCell = (TextView) itemView.findViewById(R.id.runnerCell);
             runnerTime = (TextView) itemView.findViewById(R.id.runnerTime);
         }

         void bind(User user) {
            runnerLocation.setText(user.getLocation);
            runnerEmail.setText(user.getEmail());
            runnerCell.setText(user.getCell);
            runnerTime.setText(user.getTime());
         }
     }
}
