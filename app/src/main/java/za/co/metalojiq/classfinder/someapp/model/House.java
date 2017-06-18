package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by divine on 2017/06/15.
 */

public class House {
    @SerializedName("address")
    private String address;
    @SerializedName("wifi")
    private String wifi;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("nsfas")
    private boolean nsfas;
    @SerializedName("common")
    private String common;
    @SerializedName("prepaid_elec")
    private boolean isPrepaidElectrity;
//    @SerializedName("accommodation_id")
//    private int accomId;

    @SerializedName("results")
    private ArrayList<Accommodation> results;


    @SerializedName("status")
    private boolean status;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isNsfas() {
        return nsfas;
    }

    public void setNsfas(boolean nsfas) {
        this.nsfas = nsfas;
    }

    public String getCommon() {
        return common;
    }

    public void setCommon(String common) {
        this.common = common;
    }

    public boolean isPrepaidElectrity() {
        return isPrepaidElectrity;
    }

    public void setPrepaidElectrity(boolean prepaidElectrity) {
        isPrepaidElectrity = prepaidElectrity;
    }

    public ArrayList<Accommodation> getResults() {
        return results;
    }

    public void setResults(ArrayList<Accommodation> results) {
        this.results = results;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
