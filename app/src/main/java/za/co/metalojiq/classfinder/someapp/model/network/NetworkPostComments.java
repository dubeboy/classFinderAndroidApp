package za.co.metalojiq.classfinder.someapp.model.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by divine on 3/21/17.
 */
// this is a relational class thich belongs to the networkPostModel class
public class NetworkPostComments implements Serializable {
    @SerializedName("comment")
    private String comment;
    @SerializedName("user_name")
    private String userName;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
