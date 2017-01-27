package za.co.metalojiq.classfinder.someapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Accommodation implements Serializable {

    @SerializedName("id=")
    private Integer id;
    @SerializedName("host_id")
    private Integer hostId;
    @SerializedName("location")
    private String location;
    @SerializedName("room_type")
    private String roomType;
    @SerializedName("price")
    private String price;
    @SerializedName("description")
    private String description;
    @SerializedName("pictures")
    private List<Picture> pictures = null;

    public Accommodation(Integer id, Integer hostId, String location, String roomType, String price, String description, List<Picture> pictures) {
        this.id = id;
        this.hostId = hostId;
        this.location = location;
        this.roomType = roomType;
        this.price = price;
        this.description = description;
        this.pictures = pictures;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public ArrayList<String> getImagesUrls() {
        ArrayList<String> urls = new ArrayList<>();
        for (Picture p : pictures) {
            urls.add(p.getImageUrl());
        }
        return urls;
    }

    public Picture getPicture(int position) {
        return pictures.get(position);
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }
}

