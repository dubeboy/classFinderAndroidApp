package za.co.metalojiq.classfinder.someapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import okhttp3.RequestBody;
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

import static za.co.metalojiq.classfinder.someapp.util.Utils.*;

public class NewAccommodation extends AppCompatActivity {

    private static final String TAG = NewAccommodation.class.getSimpleName();
    private Spinner locationSpinner;
    private Spinner roomTypeSpinner;
    private Spinner auckAreaSpinner;
    private Bitmap[] bitmaps;
    private String[] imageUris;
    private EditText etPrice;
    private EditText etDescription;
    private LinearLayout imagesContainer;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_accommodation);
        TextView tvAuck = (TextView) findViewById(R.id.newAuckAreas);
        locationSpinner = setupSpinner(this, R.id.newSpinnerLocation, R.array.locations_array);
        roomTypeSpinner = setupSpinner(this, R.id.newSpinnerRoomType, R.array.room_type);
        auckAreaSpinner = setupSpinner(this, R.id.newSpinnerAuckAreas, R.array.auck_areas);
        imagesContainer = (LinearLayout) findViewById(R.id.newImagesHorizontalScroll);

        int houseId = getIntent().getIntExtra(HouseActivityFragment.Companion.getHOUSE_ID(), -1);

        locationSpinner.setOnItemSelectedListener(new Utils.LocationItemListener(tvAuck, auckAreaSpinner));
        etPrice = (EditText) findViewById(R.id.newEtPrice);
        etDescription = (EditText) findViewById(R.id.newBooksDesc);

        Button btnPickImages = (Button) findViewById(R.id.newBtnAddImages);
        btnPickImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast("Starting image Picker", NewAccommodation.this);
                launchImagesPicker();
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

    private void uploadData(int houseId) {
        String loc = (String) locationSpinner.getSelectedItem();
        String roomT= (String) roomTypeSpinner.getSelectedItem();
        String rawArea = (String) auckAreaSpinner.getSelectedItem();
        int prc =  Integer.valueOf((etPrice.getText().toString()).equals("") ? "0" : etPrice.getText().toString());
        String desc = etDescription.getText().toString();


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
            String aA = AUCK_AREA_PREFIX + rawArea;
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"),((Integer) uId).toString() );
            RequestBody location = RequestBody.create(MediaType.parse("text/plain"), loc);
            RequestBody roomType = RequestBody.create(MediaType.parse("text/plain"), roomT);
            RequestBody auckArea = RequestBody.create(MediaType.parse("text/plain"), aA);
            RequestBody price = RequestBody.create(MediaType.parse("text/plain"), ((Integer) prc).toString() );
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), desc);
            MultipartBody requestBody = builderNew.build();
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);


            Call<AccommodationResponse> call = apiService.postAccommodation(userId, houseId,  location, roomType, auckArea,
                    price, description, requestBody.parts());
            call.enqueue(new Callback<AccommodationResponse>() {
                @Override
                public void onResponse(Call<AccommodationResponse> call, Response<AccommodationResponse> response) {
                    if (response.body().isStatus()) {
                        makeToast("uploaded", NewAccommodation.this);
                        dialog.dismiss();
                        finish();
                    } else {
                        makeToast("Sorry please try again something went wrong double check your submission", NewAccommodation.this);
                    }
                }
                @Override
                public void onFailure(Call<AccommodationResponse> call, Throwable t) {
                    Log.e(TAG, t.toString());
                    makeToast("Please connect to the internet  ", NewAccommodation.this);
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


    private  void launchImagesPicker() {
        requestCameraPermissions();
    }

    private void createImagesBottomPicker() {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(this)
                .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {

                    @Override
                    public void onImagesSelected(ArrayList<Uri> uriList) {
                        int numImages = uriList.size();
                        bitmaps  = new Bitmap[numImages];
                        if (numImages > 0) {
                            imagesContainer.removeAllViews();
                            imageUris = new String[numImages];
                            ImageView previewImages[] = new ImageView[numImages];
                            HorizontalScrollView imagesHoriScrollView = (HorizontalScrollView) findViewById(R.id.newImagesScrollView);
                            imagesHoriScrollView.setVisibility(View.VISIBLE);

                            for (int i = 0; i < numImages; i++) {
                                bitmaps[i] = BitmapFactory.decodeFile(uriList.get(i).getPath());
                                imageUris[i] = uriList.get(i).getPath();
                                previewImages[i] = new ImageView(NewAccommodation.this);
                                previewImages[i].setAdjustViewBounds(true);
                                previewImages[i].setLayoutParams(new ViewGroup.LayoutParams(240, 240));
                                previewImages[i].setPadding(5, 0, 5, 0);
                                previewImages[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
                                previewImages[i].setImageBitmap(bitmaps[i]);
                                imagesContainer.addView(previewImages[i]);
                            }
                        }
                    }
                })
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Upload")
                .setEmptySelectionText("No image selected for upload")
                .create();

        bottomSheetDialogFragment.show(getSupportFragmentManager());
    }
    private void requestCameraPermissions() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(NewAccommodation.this, "Please select images ypu want to upload.", Toast.LENGTH_SHORT).show();
                createImagesBottomPicker();
            }
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(NewAccommodation.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        new TedPermission(NewAccommodation.this)
                .setPermissionListener(permissionlistener)
                .setGotoSettingButton(true)
                .setDeniedMessage("If you reject permission,you can not upload Images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }
}
