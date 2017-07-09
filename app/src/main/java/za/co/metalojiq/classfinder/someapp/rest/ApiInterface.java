package za.co.metalojiq.classfinder.someapp.rest;

import android.support.annotation.NonNull;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;
import za.co.metalojiq.classfinder.someapp.model.*;
import za.co.metalojiq.classfinder.someapp.model.network.NetworkPostModel;
import za.co.metalojiq.classfinder.someapp.model.network.NetworkResponse;

import java.util.List;

/**
 * Created by divine on 1/9/17.
 */

/**
 * this is a class containing all my api class, sweet! is It...?? the /api/v1 can be eliminated
 */
public interface ApiInterface {
    @GET("/api/v1/accommodations.json")
    Call<AccommodationResponse> getAllAccommodations(@Query("page") int page);

    @Headers({"Accept: application/json"})
    @GET("/api/v1/accommodations/search")
    Call<AccommodationResponse> searchAccommodations(@Query(value = "name", encoded = true) String location,
                                                     @Query("room_type") String roomType,
//                                                     @Query("auck_location") String auckLocation,
                                                     @Query("price_from") Integer priceFrom,
                                                     @Query("price_to") Integer priceTo,
                                                     @Query("nsfas") boolean nsfas);
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
    Call<UserResponse> signIn(@Field("email") String email,
                              @Field("password") String password, @Field("fcm_token") String fcmToken);

    @Headers({"Accept: application/json"}) //Todo: should remove this -> redundant
    @GET("/api/v1/users/get_user")
    Call<UserResponse> getUser(@Query("user_id") String userId);

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
    Call<AccommodationResponse> postAccommodation(@Part("user_id") RequestBody userId,
                                                  @Part("house_id") RequestBody houseId,
//                                                  @Part("location") RequestBody location,
                                                  @Part("room_type") RequestBody roomType,
//                                                  @Part("institution") RequestBody auckArea,
                                                  @Part("price") RequestBody price,
                                                  @Part("deposit") RequestBody deposit,
                                                  @Part("description") RequestBody description,
                                                  @Part List<MultipartBody.Part> images);


    @Headers({"Accept: application/json"})
    @POST("/api/v1/accommodations/{id}/deposit")
    Call<StatusRespose> postStripeToken(@Path("id") Integer accommdationId,
                                        @Query("stripeToken") String token,
                                        @Query("email") String email,
                                        @Query("deposit") String deposit);

    //----------------------------------BOOKS-------------------------------------------------

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
                              @Field("time_slots_ids") byte[] times,
                              @Field("run_location") String runLocation,
                              @Field("fcm_token") String fcmToken);

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("/api/v1/users")
    Call<UserResponse> googleUserSignUp(@Field("email") String email, @Field("name") String name,
                                        @Field("phone") String phone, @Field("token") String token,
                                        @Field("is_runner") boolean selected,
                                        @Field("time_slots_ids") byte[] times,
                                        @Field("run_location") String runLocation,
                                        @Field("fcm_token") String fcmToken);

    @Headers({"Accept: application/json"})
    @GET("/api/v1/users/user_exits")
    Call<UserResponse> doesUserExit(@Query("email") String email);


    //-------------------------Networks API REST --------------------------------


    //this get all the Topics
    @Headers({"Accept: application/json"})
    @GET("/api/v1/networks")
    Call<NetworkResponse> getAllNetworkTopics(@Query("page") int page,
                                              @Query("network_id") int catId,
                                              @Query("network_type") int topicType,
                                              @Query("user_id") int userId);


    // we need on to create a new post
    @Headers({"Accept: application/json"})
    @POST("/api/v1/networks")
    Call<NetworkResponse> postNewNetwork(@Query("user_id") int userId,
                                         @Query("net_cat") int catId,
                                         @Query("network_name") String networkName,
                                         @Query("desc") String description,
                                         @Query("network_type") int networkType);

    @Headers({"Accept: application/json"})
    @GET("/api/v1/networks/{network_id}/network_posts")
    Call<NetworkPostsResponse> getAllNetworksPost(@Path("network_id") int catId,
                                                  @Query("topic_id") int topicId,
                                                  @Query("page") int page,
                                                  @Query("user_id") int userId);

    @Multipart
    @Headers({"Accept: application/json"})
    @POST("/api/v1/networks/{network_id}/network_posts")
    Call<NetworkPostModel> postNetworkPost(@Path("network_id") Integer catId,
                                           @Part("topic_id") RequestBody topicId,
                                           @Part("desc") RequestBody networkPostDesc,
                                           @Part("user_id") RequestBody userId,
                                           @Part List<MultipartBody.Part> images); // The network Images yoh


    // we need on to create a new post
    @Headers({"Accept: application/json"})
    //"/api/v1/networks/:network_id/network_posts/:network_post_id/like"
    @POST("/api/v1/networks/{network_id}/network_posts/{network_post_id}/like")
    Call<NetworkPostsResponse> likeNetworkPost(@Path("network_id") int networkId,
                                               @Path("network_post_id") int postId,
                                               @Query("user_id") int userId);

    @Headers({"Accept: application/json"})
    @POST("/api/v1/networks/{network_id}/network_posts/{network_post_id}/comments")
        ///api/v1/networks/:network_id/network_posts/:network_post_id/comments
    Call<PostResponse> postComment(@Path("network_id") int networkId,
                                   @Path("network_post_id") int postId,
                                   @Query("user_id") int userId,
                                   @Query("com") String comment);

    @Headers({"Accept: application/json"})
    @GET("/api/v1/networks/{network_id}/network_posts/{network_post_id}/comments")
    Call<NetworkPostModel> getAllComments(@Path("network_id") int networkId,
                                          @Path("network_post_id") int postId,
                                          @Query("user_id") int userId);

    @Headers({"Accept: application/json"})
    @POST("/api/v1/networks/{network_id}/subscribe")
    Call<PostResponse> subscribeToNetwork(@Path("network_id") int networkId,
                                          @Query("user_id") int userId);


    // House routes! // ================HOUSE ROUTES =====================
    @Headers({"Accept: application/json"})
    @GET("/api/v1/users/{user_id}/house")
    Call<HousesResponse> getHousesForUser(@Path("user_id") int userId,
                                          @Query("page") int page);


    @Headers({"Accept: application/json"})
    @POST("/api/v1/users/{user_id}/house")
    Call<House> postHouse(@Path("user_id") int userId,
                          @NonNull @Query("address") String address,
                          @NonNull @Query("location") String location,
                          @NonNull @Query("city") String city,
                          @NonNull @Query("common") String common,
                          @Query("nsfas") boolean nsfas,
                          @Query("wifi") boolean wifi,
                          @Query("prepaid_elec") boolean prepaid,
                          @Part List<MultipartBody.Part> images);

    @Headers({"Accept: application/json"})
    @GET("/api/v1/house/{id}/get_accoms")
    Call<AccommodationResponse> getAccommodationsForHouse(@Path("id") int id);

    //----------notifications---------------refrencies------- payments
    @Headers({"Accept: application/json"})
    @GET("/api/v1/accommodations/refs")
    Call<StatusRespose> shareRef(@Query("tk") String token, @Query("accom_id") int accommodationId);

    @Headers({"Accept: application/json"})
    @GET("/api/v1/users/save_fcm_token")
    Call<StatusRespose> saveFcmToken(@Query("fcm_token") String token, @Query("email") String email);

    @Headers({"Accept: application/json"})
    @GET("/api/v1/users/send_sms")
    Call<StatusRespose> sendSms(@Query("phone_number") String number,
                                @Query("user_id") int userId);


    @Headers({"Accept: application/json"})
    @GET("/api/v1/users/notify_host")
    Call<StatusRespose> notifyHost(@Query("room_id") String roomId,
                                @Query("user_id") int userId);




}