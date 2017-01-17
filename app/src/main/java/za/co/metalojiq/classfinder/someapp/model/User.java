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
    @SerializedName("transactions")
    private ArrayList<Transaction> transactions;

    public ArrayList<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(ArrayList<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
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

    private class Transaction {
        @SerializedName("id")
        private int id;
        @SerializedName("host_id")
        private int hostId;
        @SerializedName("user_id")
        private int userId;
        @SerializedName("runner_id")
        private int runnerId;
        @SerializedName("accommodation_id")
        private int accommodationId;
        @SerializedName("paid")
        private boolean paid;
        @SerializedName("booking_type")
        private boolean bookingType;
        @SerializedName("std_confirm")
        private boolean stdConfirm;
        @SerializedName("month")
        private String month;
        @SerializedName("time")
        private String time;

        public Transaction(int id, int hostId, int userId, int runnerId, int accommodationId,
                           boolean paid, boolean bookingType, boolean stdConfirm, String month, String time) {
            this.id = id;
            this.hostId = hostId;
            this.userId = userId;
            this.runnerId = runnerId;
            this.accommodationId = accommodationId;
            this.paid = paid;
            this.bookingType = bookingType;
            this.stdConfirm = stdConfirm;
            this.month = month;
            this.time = time;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getHostId() {
            return hostId;
        }

        public void setHostId(int hostId) {
            this.hostId = hostId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getRunnerId() {
            return runnerId;
        }

        public void setRunnerId(int runnerId) {
            this.runnerId = runnerId;
        }

        public int getAccommodationId() {
            return accommodationId;
        }

        public void setAccommodationId(int accommodationId) {
            this.accommodationId = accommodationId;
        }

        public boolean isPaid() {
            return paid;
        }

        public void setPaid(boolean paid) {
            this.paid = paid;
        }

        public boolean isBookingType() {
            return bookingType;
        }

        public void setBookingType(boolean bookingType) {
            this.bookingType = bookingType;
        }

        public boolean isStdConfirm() {
            return stdConfirm;
        }

        public void setStdConfirm(boolean stdConfirm) {
            this.stdConfirm = stdConfirm;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
