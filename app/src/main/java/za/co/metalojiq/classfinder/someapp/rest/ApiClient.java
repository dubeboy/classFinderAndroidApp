package za.co.metalojiq.classfinder.someapp.rest;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by divine on 1/9/17.
 */

public class ApiClient {

    //todo make this a gradle task
    //todo:  this should be calling the api domain /api/v1/...
    public static final String HOST_URL = "http://192.168.42.140:3000";
//    public static final String BASE_URL = HOST_URL + "/accommodations" ; not required man retrofit is cool
    private static Retrofit retrofit = null;



    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder().baseUrl(HOST_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private  static OkHttpClient  interc() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.networkInterceptors().add(
                new Interceptor() {
            @Override
            //todo should be com.squareup.okhttp.Response
            public Response intercept(Chain chain) throws IOException {
                Request.Builder requestBuilder = chain.request().newBuilder();
                requestBuilder.header("Content-Type", "application/json");
                return chain.proceed(requestBuilder.build());
            }
        });
        return httpClient;
    }
}
