package za.co.metalojiq.classfinder.someapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by divine on 1/9/17.
 */


//this class is useful when recieving extra data with the json
public class AccommodationResponse {

//    @SerializedName("page")
//    private int page;
    @SerializedName("results")
    private ArrayList<Accommodation> results;

//    public int getPage() {
//        return page;
//    }
//
//    public void setPage(int page) {
//        this.page = page;
//    }

    public ArrayList<Accommodation> getResults() {
        return results;
    }

    public void setResults(ArrayList<Accommodation> results) {
        this.results = results;
    }

//    public int getTotalResults() {
//        return totalResults;
//    }
//
//    public void setTotalResults(int totalResults) {
//        this.totalResults = totalResults;
//    }
//
//    public int getTotalPages() {
//        return totalPages;
//    }
//
//    public void setTotalPages(int totalPages) {
//        this.totalPages = totalPages;
//    }
}
