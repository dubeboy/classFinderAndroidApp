package za.co.metalojiq.classfinder.someapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.adapter.AccomAdapter;
import za.co.metalojiq.classfinder.someapp.model.Accommodation;
import za.co.metalojiq.classfinder.someapp.model.AccommodationResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;


//// TODO: 1/11/17   this class should use fragments to display the activity based on the which callvback
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public  static final String PICTURES_ARRAY_EXTRA = TAG + ".PICTURES_ARRAY_LIST";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_slider:
                Intent intent = new Intent(this, AccomImageSlider.class);
                startActivity(intent);
                return true;
        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AccommodationResponse> call = apiService.getAllAccommodations();
        call.enqueue(new Callback<AccommodationResponse>() {
            @Override
            public void onResponse(Call<AccommodationResponse> call, Response<AccommodationResponse> response) {
                List<Accommodation> accommodations = response.body().getResults();
                Log.d(TAG, "Number of elemets =" +accommodations.size());
                recyclerView.setAdapter(new AccomAdapter(accommodations,
                        R.layout.list_item_accom, getApplicationContext(), new AccomAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Accommodation accommodation) {
                        Intent intent = new Intent(getApplicationContext(), AccomImageSlider.class);
                        intent.putStringArrayListExtra(PICTURES_ARRAY_EXTRA, accommodation.getImagesUrls()); // TODO: 1/11/17 google how to add an arraylist to a put extra
                        startActivity(intent);
                    }
                }));
            }

            @Override
            public void onFailure(Call<AccommodationResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }
}
