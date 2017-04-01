package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by divine on 3/31/17.
 */
// this class for all post since posts just return boolean values
public class PostResponse implements Serializable{

    @SerializedName("status")
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


}
