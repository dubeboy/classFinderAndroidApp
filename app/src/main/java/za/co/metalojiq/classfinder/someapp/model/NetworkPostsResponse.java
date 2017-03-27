package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by divine on 3/20/17.
 */
public class NetworkPostsResponse {

    @SerializedName("network_posts")
    private ArrayList<NetworkPostModel> networkPosts;

    @SerializedName("status")
    private boolean status ; // status indicating a success on any networkPost post!

    @SerializedName("liked")
    private boolean liked ; // status indicating a succes on any networkPost post!

    public ArrayList<NetworkPostModel> getNetworkPosts() {
        return networkPosts;
    }


    public void setNetworkPosts(ArrayList<NetworkPostModel> networkPosts) {
        this.networkPosts = networkPosts;
    }


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
