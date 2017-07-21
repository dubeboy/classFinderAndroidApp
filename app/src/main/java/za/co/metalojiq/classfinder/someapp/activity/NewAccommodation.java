package za.co.metalojiq.classfinder.someapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import gun0912.tedbottompicker.TedBottomPicker;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.fragment.HouseActivityFragment;
import za.co.metalojiq.classfinder.someapp.model.AccommodationResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;
import za.co.metalojiq.classfinder.someapp.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static za.co.metalojiq.classfinder.someapp.util.Utils.*;

public class NewAccommodation extends AppCompatActivity implements Utils.OnImagesSelected {

    private static final String TAG = NewAccommodation.class.getSimpleName();
    //    private Spinner auckAreaSpinner;
    //    private Spinner locationSpinner;
    private Bitmap[] bitmaps;
    private Spinner roomTypeSpinner;
    private String[] imageUris;
    private EditText etPrice;
    private EditText etDescription;
    private LinearLayout imagesContainer;
    private ProgressDialog dialog;
    private EditText etDeposit;
//    private OkHttpClient kk;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_accommodation);
       // TextView tvAuck = (TextView) findViewById(R.id.newAuckAreas);
        //locationSpinner = setupSpinner(this, R.id.newSpinnerLocation, R.array.locations_array);
        roomTypeSpinner = setupSpinner(this, R.id.newSpinnerRoomType, R.array.room_type);
       // auckAreaSpinner = setupSpinner(this, R.id.newSpinnerAuckAreas, R.array.auck_areas);
        imagesContainer = (LinearLayout) findViewById(R.id.newImagesHorizontalScroll);

        setTitle("Add a room");

        final int houseId = getIntent()
                            .getIntExtra(HouseActivityFragment.Companion.getHOUSE_ID(), -1);
        Log.d(TAG, "onCreate: the passed in house id is:  " + houseId);

       // locationSpinner.setOnItemSelectedListener(new Utils.LocationItemListener(tvAuck, auckAreaSpinner));
        etPrice = (EditText) findViewById(R.id.newEtPrice);
        etDescription = (EditText) findViewById(R.id.newBooksDesc);
        etDeposit = (EditText) findViewById(R.id.newEtDeposit);

        Button btnPickImages = (Button) findViewById(R.id.newBtnAddImages);
        btnPickImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast("Starting image Picker", NewAccommodation.this);
                Utils.launchImagesPicker(NewAccommodation.this,
                                                getSupportFragmentManager(),
                                                imagesContainer,
                                                NewAccommodation.this); //todo:  arguments can be better
            }
        });

        Button btnSave = (Button) findViewById(R.id.newBtnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmaps != null) {
                    //TODO should be a notification
                     dialog = ProgressDialog.show(NewAccommodation.this, "",
                            "Uploading images, please wait...", true);
                    uploadData(houseId);
                } else {
                    Toast.makeText(NewAccommodation.this,
                            "you have to also include images.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void uploadData(final int houseId) {
//        String loc = (String) locationSpinner.getSelectedItem();
//        String rawArea = (String) auckAreaSpinner.getSelectedItem();
        Log.d(TAG, "uploadData: the house id is: " + houseId);
        String roomT= (String) roomTypeSpinner.getSelectedItem();
        double deposit = Double.valueOf(etDeposit.getText().toString());
        int prc =  Integer.valueOf((etPrice.getText().toString()).equals("") ? "0" : etPrice.getText().toString());
        String desc = etDescription.getText().toString(); // todo: must do validatons here please

        // we want to upload only if there are images
        if (!(bitmaps.length == 0 && prc <= 0 && TextUtils.isEmpty(desc))) {
            //upload!!
            MultipartBody.Builder builderNew = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (String imageUri : imageUris) {
                File file = new File(imageUri);
                String mime = getMimeType(imageUri);
                Log.d("MIME", "the mime is " + mime);
                RequestBody reqFile = RequestBody.create(MediaType.parse(mime), file);
//                 MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), reqFile);
                builderNew.addFormDataPart("images[]", file.getName(), reqFile);
            }
            int uId = getUserSharedPreferences(this).getInt(LoginActivity.LOGIN_PREF_USER_ID, 0);
//            String aA = AUCK_AREA_PREFIX + rawArea;
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"),((Integer) uId).toString() );
//            RequestBody location = RequestBody.create(MediaType.parse("text/plain"), loc);
            RequestBody roomType = RequestBody.create(MediaType.parse("text/plain"), roomT);
//            RequestBody auckArea = RequestBody.create(MediaType.parse("text/plain"), aA);
            RequestBody price = RequestBody.create(MediaType.parse("text/plain"), ((Integer) prc).toString() );
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), desc);
            RequestBody hId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(houseId));
            RequestBody reqDeposit = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(deposit));
            MultipartBody requestBody = builderNew.build();
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);


            Call<AccommodationResponse> call = apiService.postAccommodation(userId, hId, roomType,
                                                                             price, reqDeposit,  description, requestBody.parts());
            call.enqueue(new Callback<AccommodationResponse>() {
                @Override
                public void onResponse(Call<AccommodationResponse> call, Response<AccommodationResponse> response) {
                    if (response.body() != null && response.body().isStatus()) {
                        makeToast("uploaded, swipe down to refresh.", NewAccommodation.this);
                        dialog.dismiss(); //todo: this is redundant
                        finish();
                    } else {
                        makeToast("Sorry please try again something went wrong double check your submission", NewAccommodation.this);
                        dialog.dismiss();  //todo: this is redundant
                    }
                }
                @Override
                public void onFailure(Call<AccommodationResponse> call, Throwable t) {
                    Log.e(TAG, t.toString());
                    makeToast("Please connect to the internet", NewAccommodation.this);
                    dialog.dismiss();
                }
            });
        } else {
            if (TextUtils.isEmpty(desc))  {
                Toast.makeText(this, "Description has to have something", Toast.LENGTH_SHORT).show();
            }
            if (prc == 0)  {
                Toast.makeText(this, "Price has to be more than 0", Toast.LENGTH_SHORT).show();
            }
            if (bitmaps.length == 0)  {
                Toast.makeText(this, "you have to also include images.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onImagesSelected(Bitmap[] bitmaps, String[] imagesUrls) {
        this.bitmaps = bitmaps;
        this.imageUris = imagesUrls;
    }
}
