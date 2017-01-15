package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
* Created by divine on 1/9/17.
*/
 public class Picture implements Serializable{

   @SerializedName("image_id")
   private Integer imageId;
   @SerializedName("image_name")
   private String imageName;
   @SerializedName("image_size")
   private Integer imageSize;
   @SerializedName("image_url")
   private String imageUrl;

   public Integer getImageId() {
       return imageId;
   }

   public void setImageId(Integer imageId) {
       this.imageId = imageId;
   }

   public String getImageName() {
       return imageName;
   }

   public void setImageName(String imageName) {
       this.imageName = imageName;
   }

   public Integer getImageSize() {
       return imageSize;
   }

   public void setImageSize(Integer imageSize) {
       this.imageSize = imageSize;
   }

   public String getImageUrl() {
       return imageUrl;
   }

   public void setImageUrl(String imageUrl) {
       this.imageUrl = imageUrl;
   }

}
