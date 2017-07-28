package za.co.metalojiq.classfinder.someapp.rest;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by divine on 1/9/17.
 */

public class ApiClient {
    //todo make this a gradle task

    //shuold not include the ending forward slash ;)
    //todo:  this should be calling the api domain /api/v1/...
    public static final String PROD_HOST = "https://ancient-journey-18261.herokuapp.com";
    public static final String DEV_HOST = "http://10.254.109.20:3000";
    //    public static final String BASE_URL = HOST_URL + "/accommodations" ; not required man retrofit is cool
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient okHttp = new OkHttpClient.Builder()
//                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                //.connectTimeout(30, TimeUnit.SECONDS)
                .build();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(DEV_HOST)
                    .client(okHttp)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
