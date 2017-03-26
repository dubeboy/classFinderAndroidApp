package za.co.metalojiq.classfinder.someapp.model.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by divine on 3/20/17.
 */
public class NetworkPostModel implements Serializable {
    @SerializedName("poster_img_url")
    private String posterImgUrl;
    @SerializedName("poster_name")
    private String name;
    @SerializedName("comments")
    private String[] comments;   // this should be deleted but for now lets see what we can do
    @SerializedName("post_img_url")
    private String postImageUrl;
    @SerializedName("netComments") // mmmmh // this one should be used
    private List<NetworkPostComments> networkPostComments;
    @SerializedName("time")
    private String time;
    @SerializedName("likes")
    private int likes;
    @SerializedName("status")
    private boolean status ; // status indicating a succes on any networkPost post!



    public String getPosterImgUrl() {
        return posterImgUrl;
    }

    public void setPosterImgUrl(String posterImgUrl) {
        this.posterImgUrl = posterImgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }


    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String[] getComments() {
        return comments;
    }

    public void setComments(String[] comments) {
        this.comments = comments;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
