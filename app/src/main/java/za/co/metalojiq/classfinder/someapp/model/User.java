package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by divine on 1/15/17.
 */

//if the user authenticates using the gmail them then user we get that user id and check if its there on our servers
public class User {
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("time_slots")
    private ArrayList<TimeSlot> timeSlots;


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

    @SerializedName("id")
    private int id; //todo: should be a long bra

    public User(String email, String password) {
        this.email = email;
        this.password = password;
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
