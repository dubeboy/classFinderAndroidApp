package za.co.metalojiq.classfinder.someapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.model.NetworkPost;

import java.util.List;

/**
 * Created by divine on 3/20/17.
 */
public class NetworkPostAdapter extends RecyclerView.Adapter<NetworkPostAdapter.NetPostViewHolder> {
    private int rowLayout;
    private Context context;
    private List<NetworkPost> networkPosts;
    private final NetworkPostAdapter.OnNetworkPostClickListener listener;

    public NetworkPostAdapter(int rowLayout, Context context, NetworkPostAdapter.OnNetworkPostClickListener listener) {
        this.rowLayout = rowLayout;
        this.context = context;
        this.listener = listener;
    }

    public interface OnNetworkPostClickListener {
        void onNetworkPostClick();
    }

    @Override
    public NetPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(rowLayout, parent, false);
        return new NetPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NetPostViewHolder holder, int position) {
        holder.bind(networkPosts.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class NetPostViewHolder extends RecyclerView.ViewHolder {
        ImageView netProPic, postImage;
        TextView tvPosterName, tvPostedTime, tvLikesCount, tvComments;

        public NetPostViewHolder(View itemView) {
            super(itemView);
            netProPic = (ImageView) itemView.findViewById(R.id.network_pro_pic_image);
            postImage = (ImageView) itemView.findViewById(R.id.post_image);
            tvPosterName = (TextView) itemView.findViewById(R.id.name);
            tvPostedTime = (TextView) itemView.findViewById(R.id.time);
            tvLikesCount = (TextView) itemView.findViewById(R.id.likes_count);
            tvComments = (TextView) itemView.findViewById(R.id.comments);
        }

        public void bind(NetworkPost networkPost, OnNetworkPostClickListener listener) {

        }
    }
}
