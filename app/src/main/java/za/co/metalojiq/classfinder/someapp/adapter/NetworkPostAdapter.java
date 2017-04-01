package za.co.metalojiq.classfinder.someapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.model.network.NetworkPostModel;
import za.co.metalojiq.classfinder.someapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by divine on 3/20/17.
 */
public class NetworkPostAdapter extends RecyclerView.Adapter<NetworkPostAdapter.NetPostViewHolder> {
    private final NetworkPostAdapter.OnNetworkPostClickListener listener;
    private int rowLayout;
    private Context context;
    private List<NetworkPostModel> networkPostModels;
    private ImageView likeButton;
    private ImageView commentButton;
    private NetPostViewHolder viewHolder;
    private int position;

    private String TAG = NetworkPostAdapter.class.getSimpleName();

    public NetworkPostAdapter(List<NetworkPostModel> networkPostModels, int rowLayout, Context context, NetworkPostAdapter.OnNetworkPostClickListener listener) {
        this.networkPostModels = networkPostModels;
        this.rowLayout = rowLayout;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public NetPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(rowLayout, parent, false);
        return new NetPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NetPostViewHolder holder, int position) {
        viewHolder = holder;
        this.position = position;
        holder.bind(networkPostModels.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return networkPostModels.size();
    }

    public void clear() {
        networkPostModels.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<NetworkPostModel> networkPosts) {
        this.networkPostModels.addAll(networkPosts);
        notifyDataSetChanged();
    }

    public void like() {
        Log.d(TAG, "Like: ");
        int likes = networkPostModels.get(position).getLikes();
        networkPostModels.get(position).setLikes(likes + 1);
        notifyDataSetChanged();
    }

    public void unLike() {
        Log.d(TAG, "unLike: ");
        int likes = networkPostModels.get(position).getLikes();
        networkPostModels.get(position).setLikes(likes - 1);
        notifyDataSetChanged();
    }

    public enum ITEM_TYPE_CLICK {
        POST_IMAGE,
        COMMENT_BUTTON,
        LIKE_BUTTON
    }

    public interface OnNetworkPostClickListener {
        void onNetworkPostClick(NetworkPostModel networkPostModel, ITEM_TYPE_CLICK typeClick);
    }

    class NetPostViewHolder extends RecyclerView.ViewHolder {
        ImageView netProPic, postImage;
        TextView tvPosterName, tvPostedTime, tvLikesCount, tvComments, tvPostDesc;

        NetPostViewHolder(View itemView) {
            super(itemView);
            netProPic = (ImageView) itemView.findViewById(R.id.network_pro_pic_image);
            postImage = (ImageView) itemView.findViewById(R.id.post_image);
            tvPosterName = (TextView) itemView.findViewById(R.id.name);
            tvPostedTime = (TextView) itemView.findViewById(R.id.time);
            tvLikesCount = (TextView) itemView.findViewById(R.id.likes_count);
            tvComments = (TextView) itemView.findViewById(R.id.comment);
            tvPostDesc = (TextView) itemView.findViewById(R.id.post_desc);
            likeButton = (ImageView) itemView.findViewById(R.id.btn_like);
            commentButton = (ImageView) itemView.findViewById(R.id.btn_comment);
        }

        void bind(final NetworkPostModel networkPostModel, final OnNetworkPostClickListener listener) {
            //The poster`s Image
            if (networkPostModel.getPosterImgUrl().equals("profile.jpg")) {
                netProPic.setImageDrawable(Utils.getTextDrawable(networkPostModel.getName()));
            } else {
                Picasso.with(itemView.getContext())
                        .load(networkPostModel.getPosterImgUrl()).into(netProPic);
            }
            // the post Image Confusing yoh!!
            if (networkPostModel.getPostImageUrl().equals("")) {
                postImage.setVisibility(View.GONE);
            } else {
                Picasso.with(itemView.getContext())
                        .load(networkPostModel.getPostImageUrl()).into(postImage);
            }

            tvPosterName.setText(networkPostModel.getName());
            tvPostedTime.setText(networkPostModel.getTime() + " ago");
            tvLikesCount.setText(networkPostModel.getLikes() + " likes");
            tvComments.setText(networkPostModel.getComments().length == 0 ? "" : networkPostModel.getComments()[0]); //TODO: should change as time goes on
            tvPostDesc.setText(networkPostModel.getDescription());
            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNetworkPostClick(networkPostModel, ITEM_TYPE_CLICK.POST_IMAGE);
                }
            });
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //should return the id and user_id of the post so that when we post we can Id the post and then just increment the likes
                    listener.onNetworkPostClick(networkPostModel, ITEM_TYPE_CLICK.LIKE_BUTTON);
                }
            });

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNetworkPostClick(networkPostModel, ITEM_TYPE_CLICK.COMMENT_BUTTON);

                }
            });

        }
    }
}

