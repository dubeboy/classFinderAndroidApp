package za.co.metalojiq.classfinder.someapp.model.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by divine on 3/20/17.
 */
public class NetworkPostsResponse implements Serializable {

    @SerializedName("network_posts")
    private ArrayList<NetworkPostModel> networkPosts;



    public ArrayList<NetworkPostModel> getNetworkPosts() {
        return networkPosts;
    }


    public void setNetworkPosts(ArrayList<NetworkPostModel> networkPosts) {
        this.networkPosts = networkPosts;
    }


}
