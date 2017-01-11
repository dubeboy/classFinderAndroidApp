package za.co.metalojiq.classfinder.someapp.rest;

import retrofit2.Call;

/**
 * Created by divine on 1/9/17.
 */


import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import za.co.metalojiq.classfinder.someapp.model.AccommodationResponse;


public interface ApiInterface {
    @GET("/accommodations.json")
    Call<AccommodationResponse> getAllAccommodations();

    @GET("/accommodations/{id}.json")
    Call<AccommodationResponse> getAccommodationDetails(@Path("id") int id);
}