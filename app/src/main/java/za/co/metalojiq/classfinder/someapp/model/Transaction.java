package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by divine on 1/18/17.
 */


//Transaction is a very good mixed up object which gets data from an Accommodation and User
public class Transaction {
    //Todo these mapping should change because they are now generic
    @SerializedName("accom_id")
    private String id;
    @SerializedName("student_email")
    private String email;
    @SerializedName("student_contact")
    private String StudentContact;
    @SerializedName("location")
    private String location;
    @SerializedName("room_type")
    private String roomType;
    @SerializedName("accom_views")
    private int accomViews;
    @SerializedName("host_name")
    private String hostName;
    @SerializedName("view_date")
    private String viewDate;
    @SerializedName("view_time")
    private String viewTime;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentContact() {
        return StudentContact;
    }

    public void setStudentContact(String studentContact) {
        StudentContact = studentContact;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getAccomViews() {
        return accomViews;
    }

    public void setAccomViews(int accomViews) {
        this.accomViews = accomViews;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getViewDate() {
        return viewDate;
    }

    public void setViewDate(String viewDate) {
        this.viewDate = viewDate;
    }

    // todo this should include month
    public String getViewTime() {
        return viewTime;
    }

    public void setViewTime(String viewTime) {
        this.viewTime = viewTime;
    }
}