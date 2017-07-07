package za.co.metalojiq.classfinder.someapp.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by divine on 1/15/17.
 */

//if the user authenticates using the gmail them then user we get that user id and check if its there on our servers
public class User implements Serializable {
    @SerializedName("id")
    private int id; //todo: should be a long bra
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("time_slots")
    private ArrayList<TimeSlot> timeSlots;
    @SerializedName("runner")
    private boolean runner;
    @SerializedName("token")
    private String token;
    @SerializedName("fcm_token")
    private String fcm_token;


    public ArrayList<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(ArrayList<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public User() {

    }


    public User(String email, String password, boolean runner) {
        this.email = email;
        this.password = password;
        this.runner = runner;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRunner() {

        return runner;
    }

    public String getToken() {
        return token;
    }

    public void setToken(@NonNull String token) {
        this.token = token;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    //This class belongs to the user and nowhere else so u can only get to it via a user object.
    private class TimeSlot { // you can picture this class a c++ struct ;)
        @SerializedName("id")
        private int id;
        @SerializedName("time")
        private String time;

        // TODO: 1/17/17 the server sends the created_at fields when sending the time slot data but we do not need it.

        public TimeSlot(int id, String time) {
            this.id = id;
            this.time = time;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }


}
