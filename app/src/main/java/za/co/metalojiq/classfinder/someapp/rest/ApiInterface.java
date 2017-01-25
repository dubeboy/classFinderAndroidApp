package za.co.metalojiq.classfinder.someapp.rest;

import retrofit2.Call;

/**
 * Created by divine on 1/9/17.
 */


import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import za.co.metalojiq.classfinder.someapp.model.AccommodationResponse;
import za.co.metalojiq.classfinder.someapp.model.Transaction;
import za.co.metalojiq.classfinder.someapp.model.TransactionResponse;
import za.co.metalojiq.classfinder.someapp.model.User;
import za.co.metalojiq.classfinder.someapp.model.UserResponse;

public interface ApiInterface {
    @GET("/api/v1/accommodations.json")
    Call<AccommodationResponse> getAllAccommodations(@Query("page") int page);

    @Headers({"Accept: application/json"})
    @GET("/api/v1/accommodations/search") //TODO Should change to api/v1/accommodation
    Call<AccommodationResponse> searchAccommodations(@Query(value ="name", encoded=true) String location,
                                                     @Query("room_type" )String roomType,
                                                     @Query("auck_location")  String auckLocation,
                                                     @Query("price_from") int priceFrom,
                                                     @Query("price_to") int priceTo );

    @Deprecated
    @GET("/api/v1/accommodations/{id}.json")
    Call<AccommodationResponse> getAccommodationDetails(@Path("id") int id);

    @GET("/users/{id}.json")
    Call<User> getUser(@Path("id") int id);

    @Headers({ "Accept: application/json"})
    @FormUrlEncoded
    @POST("/api/v1/sessions")
    Call<UserResponse> signIn(@Field("email") String email, @Field("password") String  password);

    @Headers({ "Accept: application/json"}) //Todo: should remove this -> redundant
    @GET("/api/v1/users/{id}")
    Call<TransactionResponse> getRunner(@Path("id") int id, @Query("run") int run);
}