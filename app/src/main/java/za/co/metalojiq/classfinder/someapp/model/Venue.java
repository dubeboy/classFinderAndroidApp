package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by divine on 2/28/17.
 */
public class Venue {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public static ArrayList<String> genStringArray(ArrayList<Venue> venues) {
        ArrayList<String> v = new ArrayList<>();
        for (Venue ven : venues ) {
            v.add(ven.getName());
        }
        return v;
    }
}
