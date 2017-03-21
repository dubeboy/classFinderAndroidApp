package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by divine on 3/21/17.
 */



// this is a relational class thich belongs to the networkPostModel class
public class NetworkPostComments {
    @SerializedName("comment")
    private String comments;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
