package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by divine on 1/18/17.
 */

public class TransactionResponse {

    @SerializedName("transactions")
    private ArrayList<Transaction> transactions;
    @SerializedName("is_runner")
    private boolean isRunner;
    @SerializedName("time_slots")
    private List<String> timeSlots;

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public boolean isRunner() {
        return isRunner;
    }

    public void setRunner(boolean runner) {
        isRunner = runner;
    }

    public String getTimeSlots() {
        String s = "";
        for (String t : timeSlots) {
            s += s.concat(t).concat(", ");
        }

        return s.substring(0, s.length()-1);
    }

    public void setTimeSlots(List<String> timeSlots) {
        this.timeSlots = timeSlots;
    }
}
