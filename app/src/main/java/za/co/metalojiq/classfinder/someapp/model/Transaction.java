package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by divine on 1/18/17.
 */


//Transaction is a very good mixed up object which gets data from an Accommodation and User
public class Transaction implements Serializable {
    //Todo these mapping should change because they are now generic
    @SerializedName("accom_id")
    private String id;
    @SerializedName("email")
    private String studentEmail;
    @SerializedName("contact")
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

    @SerializedName("host_id")
    private int hostId;
    @SerializedName("student_id")
    private int studentId;
    @SerializedName("room_address")
    private String roomAddress;
    @SerializedName("price")
    private double price;


    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
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

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getRoomAddress() {
        return roomAddress;
    }

    public void setRoomAddress(String roomAddress) {
        this.roomAddress = roomAddress;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}