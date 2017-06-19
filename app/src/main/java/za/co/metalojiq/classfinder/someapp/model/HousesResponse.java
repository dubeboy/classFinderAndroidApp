package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by divine on 2017/06/15.
 */

public class HousesResponse {
    @SerializedName("houses")
    private  List<House> houses;

    public List<House> getHouses() {
        return houses;
    }

    public void setHouses(List<House> houses) {
        this.houses = houses;
    }
}
