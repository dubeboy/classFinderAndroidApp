package za.co.metalojiq.classfinder.someapp.model.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by divine on 3/20/17.
 */
public class NetworkPostModel implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("poster_img_url")
    private String posterImgUrl;
    @SerializedName("description")
    private String description;
    @SerializedName("poster_name")
    private String name;
    @SerializedName("post_img_url")
    private String postImageUrl;
    @SerializedName("time")
    private String time;
    @SerializedName("likes")
    private int likes;
    @SerializedName("status")
    private boolean status ; // status indicating a succes on any networkPost post!

    //Handle the all that comments that come in where they have a Post or its just comments on their own!
    @SerializedName("comments") // mmmmh // this one should be used
    private List<NetworkPostComments> networkPostComments;




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
        String[] coms = new String[networkPostComments.size()];
        for (int i = 0; i < networkPostComments.size(); i++) {
            coms[i] = networkPostComments.get(i).getComment();
        }
        return coms;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<NetworkPostComments> getNetworkPostComments() {
        return networkPostComments;
    }

    public void setNetworkPostComments(List<NetworkPostComments> networkPostComments) {
        this.networkPostComments = networkPostComments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
