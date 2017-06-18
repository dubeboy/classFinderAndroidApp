package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by divine on 2017/06/15.
 */

public class HousesResponse {
    @SerializedName("houses")
    public  List<House> houses;
}
