package za.co.metalojiq.classfinder.someapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.model.network.NetworkPostComments;

import java.util.List;

/**
 * Created by divine on 3/31/17.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {


    private int rowLayout;
    private List<NetworkPostComments> comments;
    private Context mContext;

    public CommentsAdapter(int rowLayout, List<NetworkPostComments> comments, Context mContext) {
        this.rowLayout = rowLayout;
        this.comments = comments;
        this.mContext = mContext;
    }

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new CommentsViewHolder(v);
    }

    @Override

    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        holder.bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName, tvComment;
        CommentsViewHolder(View v) {
            super(v);
            tvUserName = (TextView) v.findViewById(R.id.tv_user_name);
            tvComment = (TextView) v.findViewById(R.id.tv_comment);
        }

         void bind(NetworkPostComments networkPostComments) {
            tvComment.setText(networkPostComments.getComment());
            tvUserName.setText(networkPostComments.getUserName());
        }
    }

    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<NetworkPostComments> networkPostComments) {
        comments.addAll(networkPostComments);
        notifyDataSetChanged();
    }
}
