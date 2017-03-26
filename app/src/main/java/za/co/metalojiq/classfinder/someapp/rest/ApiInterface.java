package za.co.metalojiq.classfinder.someapp.rest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;
import za.co.metalojiq.classfinder.someapp.model.*;
import za.co.metalojiq.classfinder.someapp.model.network.NetworkResponse;

import java.util.List;

/**
 * Created by divine on 1/9/17.
 */

/**
 * this is a class containing all my api class, sweet! is It...??
 */
public interface ApiInterface {
    @GET("/api/v1/accommodations.json")
    Call<AccommodationResponse> getAllAccommodations(@Query("page") int page);

    @Headers({"Accept: application/json"})
    @GET("/api/v1/accommodations/search")
    Call<AccommodationResponse> searchAccommodations(@Query(value = "name", encoded = true) String location,
                                                     @Query("room_type") String roomType,
                                                     @Query("auck_location") String auckLocation,
                                                     @Query("price_from") Integer priceFrom, @Query("price_to") Integer priceTo);

    @GET("/api/v1/accommodations/{id}.json")
    Call<AccommodationResponse> getAccommodationDetails(@Path("id") int id);

    @GET("/api/v1/books")
    @Headers({"Accept: application/json"})
    Call<BooksResponse> getBooks(@Query("page") Integer page);

    @GET("/api/v1/books/search")
    @Headers({"Accept: application/json"})
    Call<BooksResponse> searchBooks(@Query("search") String search, @Query("category") int category_id);

    @POST("/api/v1/accommodations/{advert_id}/secure_room")
    @Headers({"Accept: application/json"})
    Call<AccommodationResponse> secureRoom(@Path("advert_id") int advertId,
                                           @Query("host_id") int hostId, @Query("std_id") int studentId,
                                           @Query("booking_type") int bookingType,
                                           //month in the format mm/dd/yy time -> xx:xx
                                           @Query("month") String month, @Query("time") String time);
    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("/api/v1/sessions")
    Call<UserResponse> signIn(@Field("email") String email, @Field("password") String password);

    @Headers({"Accept: application/json"}) //Todo: should remove this -> redundant
    @GET("/api/v1/users/{id}")
    Call<TransactionResponse> getRunner(@Path("id") int id, @Query("run") int run);

    @Headers({"Accept: application/json"})
    @GET("/api/v1/users/{id}/panel")
    Call<TransactionResponse> getBookings(@Path("id") int id);

    @Headers({"Accept: application/json"})
    @GET("/api/v1/venue_finder/index")
    Call<VenuesRespose> getVenues(@Query("time") String time);

    @Multipart
    @Headers({"Accept: application/json"})
    @POST("/api/v1/accommodations")
    Call<AccommodationResponse> postAccommodation(@Part("user_id") RequestBody userId, @Part("location") RequestBody location,
                                                  @Part("room_type") RequestBody roomType, @Part("institution") RequestBody auckArea,
                                                  @Part("price") RequestBody price, @Part("description") RequestBody description,
                                                  @Part List<MultipartBody.Part> images);

    @Multipart
    @Headers({"Accept: application/json"})
    @POST("/api/v1/books")
    Call<Book> postBook(@Part("user_id") RequestBody id,
                        @Part("book_title") RequestBody bookTitle,
                        @Part("author") RequestBody authr,
                        @Part("category_id") RequestBody bkFaculty,
                        @Part("institution_id") RequestBody inst, // this is an ID
                        @Part("price") RequestBody price,
                        @Part("description") RequestBody description,
                        @Part List<MultipartBody.Part> images); // should default to images

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("/api/v1/users")
    Call<UserResponse> signUp(@Field("email") String email, @Field("name") String name,
                              @Field("phone") String phone, @Field("password") String password,
                              @Field("is_runner") boolean selected,
                              @Field("time_slots_ids") byte[] times, @Field("run_location") String runLocation);

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("/api/v1/users")
    Call<UserResponse> googleUserSignUp(@Field("email") String email, @Field("name") String name,
                                        @Field("phone") String phone, @Field("token") String token,
                                        @Field("is_runner") boolean selected,
                                        @Field("time_slots_ids") byte[] times, @Field("run_location") String runLocation);

    @Headers({"Accept: application/json"})
    @GET("/api/v1/users/user_exits")
    Call<UserResponse> doesUserExit(@Query("email") String email);


    //-------------------------Networks API REST --------------------------------




    //this get all the Topics
    @Headers({"Accept: application/json"})
    @GET("/api/v1/networks")
    Call<NetworkResponse> getAllNetworks(@Query("page") int page, @Query("network_id") int catId);


    // we need on to create a new post
    @Headers({"Accept: application/json"})
    @POST("/api/v1/networks")
    Call<NetworkResponse> postNewNetwork(@Query("user_id") int userId,
                                         @Query("net_cat") int catId,
                                         @Query("network_name") String networkName,
                                         @Query("desc") String description);

    @Headers({"Accept: application/json"})
    @GET("/api/v1/networks_posts")
    Call<NetworkPostsResponse> getAllNetworksPost(@Query("page") int page, @Query("network_id") int catId);

    @Multipart
    @Headers({"Accept: application/json"})
    @POST("/api/v1/networks/{network_id}/network_posts")
    Call<NetworkPostModel> postNetworkPost(@Path("network_id") Integer catId,
                        @Part("desc") RequestBody networkPostDesc,
                        @Part("user_id") RequestBody userId,
                        @Part List<MultipartBody.Part> images); // The network Images yoh

}