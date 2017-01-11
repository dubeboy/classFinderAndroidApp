package za.co.metalojiq.classfinder.someapp.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by divine on 1/9/17.
 */

public class ApiClient {

    //todo make this a gradle task
    public static final String HOST_URL = "http://192.168.56.1:3000";
//    public static final String BASE_URL = HOST_URL + "/accommodations" ; not required man retrofit is cool
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(HOST_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
