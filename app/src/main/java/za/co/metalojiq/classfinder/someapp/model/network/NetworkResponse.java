package za.co.metalojiq.classfinder.someapp.model.network;

/**
 * Created by divine on 3/25/17.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * used to hold many networks or anything that is not a network
 */

public class NetworkResponse implements Serializable {

    @SerializedName("networks")
    private ArrayList<Network> networks;

    @SerializedName("status")
    private String status;

    public ArrayList<Network> getNetworks() {
        return networks;
    }

    public void setNetworks(ArrayList<Network> networks) {
        this.networks = networks;
    }
}
