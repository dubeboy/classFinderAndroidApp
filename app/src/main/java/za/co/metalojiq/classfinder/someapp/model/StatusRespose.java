package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by divine on 2017/06/19.
 */

public class StatusRespose {

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
