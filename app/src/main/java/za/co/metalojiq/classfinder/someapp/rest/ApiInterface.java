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
import za.co.metalojiq.classfinder.someapp.model.*;


//this is a class containing all my api class, sweet
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

    @GET("/api/v1/accommodations/{id}.json")
    Call<AccommodationResponse> getAccommodationDetails(@Path("id") int id);

    @POST("/api/v1/accommodations/{advert_id}/secure_room")
    @Headers({ "Accept: application/json"})
    Call<AccommodationResponse> secureRoom(@Path("advert_id") int advertId,
                                           @Query("host_id") int hostId, @Query("std_id") int studentId,
                                           @Query("booking_type") int bookingType,
                                           //month in the format mm/dd/yy time -> xx:xx
                                           @Query("month") String month, @Query("time") String time);

    @Headers({ "Accept: application/json"})
    @FormUrlEncoded
    @POST("/api/v1/sessions")
    Call<UserResponse> signIn(@Field("email") String email, @Field("password") String  password);

    @Headers({ "Accept: application/json"}) //Todo: should remove this -> redundant
    @GET("/api/v1/users/{id}")
    Call<TransactionResponse> getRunner(@Path("id") int id, @Query("run") int run);

    @Headers({ "Accept: application/json"})
    @GET("/api/v1/users/{id}/panel")
    Call<TransactionResponse> getBookings(@Path("id") int id);

    Call<Result>
}