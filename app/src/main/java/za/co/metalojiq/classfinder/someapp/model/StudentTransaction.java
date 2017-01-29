package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by divine on 1/28/17.
 */
public class StudentTransaction {
    @SerializedName("accom_id")
    private String accomId;
    @SerializedName("runner_email")
    private String email;
    @SerializedName("runner_contact")
    private String runnerContacts;
    @SerializedName("location")
    private String location;
    @SerializedName("room_type")
    private String roomType;
    @SerializedName("view_date")
    private String viewDate;
    @SerializedName("view_time")
    private String viewTime;

}


