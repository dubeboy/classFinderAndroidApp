package za.co.metalojiq.classfinder.someapp.model.network;

/**
 * Created by divine on 3/20/17.
 */


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * this class is model for network topics ma nikka!!!
 */
public class Network implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("desc")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
