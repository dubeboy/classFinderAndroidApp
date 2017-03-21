package za.co.metalojiq.classfinder.someapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import za.co.metalojiq.classfinder.someapp.activity.fragment.BooksPricesDialog;
import za.co.metalojiq.classfinder.someapp.model.Book;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;

import java.io.File;
import java.util.ArrayList;

import static za.co.metalojiq.classfinder.someapp.util.Utils.*;


public class NewBooks extends AppCompatActivity {

    private Bitmap[] bitmaps;
    private LinearLayout imagesContainer;
    private String[] imageUris;
    private ProgressDialog dialog;
    private Spinner facultySpinner;
    private Spinner newBookSpinnerInstitution;
    private EditText etPrice;
    private EditText etDescription;
    private String TAG = NewBooks.class.getSimpleName();
    private EditText etBookTitle;
    private EditText etAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_books);

        if (!isLoggedIn(this)) {
            makeToast("You need to be logged in to upload books", this);
        }
        imagesContainer = (LinearLayout) findViewById(R.id.newImagesHorizontalScroll);
        facultySpinner = setupSpinner(this, R.id.newBookSpinnerFaculty, R.array.books_faculty);
        etBookTitle = ((EditText) findViewById(R.id.newBookTitle));
        etAuthor = ((EditText) findViewById(R.id.newBookEtAuthor));
        newBookSpinnerInstitution = setupSpinner(this, R.id.newBookSpinnerInstitution, R.array.institution);

        etPrice = (EditText) findViewById(R.id.newEtPrice);
        etDescription = (EditText) findViewById(R.id.newBooksDesc);
        Button pricesBtn = (Button) findViewById(R.id.books_btn_prices);

        pricesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new BooksPricesDialog();
                dialogFragment.show(getSupportFragmentManager(), "Prices");
                Log.d(TAG, "onClick: prices btn clecked");

            }
        });

        Button btnPickImages = (Button) findViewById(R.id.newBtnAddImages);
        btnPickImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast("Starting image Picker", NewBooks.this);
                launchImagesPicker();
            }
        });

        Button btnSave = (Button) findViewById(R.id.newBtnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLoggedIn(NewBooks.this)) {
                    makeToast("Please login before continuing", NewBooks.this);
                    Intent intent = new Intent(NewBooks.this, LoginActivity.class);
                    startActivity(intent);
                    return;
                }
                if (isFormValid()) {
                    //TODO should be a notification
                    dialog = ProgressDialog.show(NewBooks.this, "",
                            "Uploading book images, please wait...", true);
                    uploadData();
                }
            }
        });
    }

    private boolean isFormValid() {

        if (bitmaps == null) {
            Toast.makeText(getApplicationContext(),
                    "Please attach an image with the book", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(etPrice.getText().toString())) {
            etPrice.setError("Please input price");
            return false;
        } else if (TextUtils.isEmpty(etDescription.getText().toString())) {
            etDescription.setError("Please input the description");
            return false;
        } else if (TextUtils.isEmpty(etBookTitle.getText().toString())) {
            etBookTitle.setError("Please input book title");
            return false;
        } else if (TextUtils.isEmpty(etAuthor.getText().toString())) {
            etBookTitle.setError("Please input book author");
            return false;
        }

        return true;
    }

    private void uploadData() {
        String bookTitle = etBookTitle.getText().toString();
        String author = etAuthor.getText().toString();
        String bookFaculty = (String) facultySpinner.getSelectedItem();
        String institution = (String) newBookSpinnerInstitution.getSelectedItem();
        double prc = Double.valueOf((etPrice.getText().toString()).equals("") ? "0" : etPrice.getText().toString());
        String desc = etDescription.getText().toString();


        // we want to upload only if there are images
        if (!(bitmaps.length == 0)) {
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
            RequestBody bookTte = RequestBody.create(MediaType.parse("text/plain"), bookTitle);
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), Integer.valueOf(uId).toString());
            RequestBody authr = RequestBody.create(MediaType.parse("text/plain"), author);
            RequestBody bkFaculty = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(genIDForSelectedFaculty(bookFaculty)));
            RequestBody inst = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(genIDForSelectedInstitution(institution)));
            RequestBody price = RequestBody.create(MediaType.parse("text/plain"), Double.valueOf(prc).toString());
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), desc);
            MultipartBody requestBody = builderNew.build();
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);


            Call<Book> call = apiService.postBook(userId, bookTte, authr, bkFaculty, inst,
                    price, description, requestBody.parts());
            call.enqueue(new Callback<Book>() {
                @Override
                public void onResponse(Call<Book> call, Response<Book> response) {
                    Log.d(TAG, "onResponse: the satus is:" + response.body().isStatus());
                    if (response.body().isStatus()) {

                        makeToast("uploaded, pull down to see your book!", getApplicationContext());
                        dialog.dismiss();
                        finish();
                    } else {
                        makeToast("Sorry please try again something went wrong double check your submission", getApplicationContext());
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<Book> call, Throwable t) {
                    Log.e(TAG, t.toString());
                    dialog.dismiss();
                    makeToast("Please connect to the internet", getApplicationContext());
                }
            });
        }
    }


    private void launchImagesPicker() {
        requestCameraPermissions();
    }

    private void createImagesBottomPicker() {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(this)
                .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {

                    @Override
                    public void onImagesSelected(ArrayList<Uri> uriList) {
                        int numImages = uriList.size();
                        bitmaps = new Bitmap[numImages];
                        if (numImages > 0) {
                            imagesContainer.removeAllViews();
                            imageUris = new String[numImages];
                            ImageView previewImages[] = new ImageView[numImages];
                            HorizontalScrollView imagesHoriScrollView = (HorizontalScrollView) findViewById(R.id.newImagesScrollView);
                            imagesHoriScrollView.setVisibility(View.VISIBLE);

                            for (int i = 0; i < numImages; i++) {
                                bitmaps[i] = BitmapFactory.decodeFile(uriList.get(i).getPath());
                                imageUris[i] = uriList.get(i).getPath();
                                previewImages[i] = new ImageView(getApplicationContext());
                                previewImages[i].setAdjustViewBounds(true);
                                previewImages[i].setLayoutParams(new ViewGroup.LayoutParams(240, 240));
                                previewImages[i].setPadding(5, 0, 5, 0);
                                previewImages[i].setScaleType(ImageView.ScaleType.FIT_XY);
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
                Toast.makeText(getApplicationContext(), "Please select images you want to upload.", Toast.LENGTH_SHORT).show();
                createImagesBottomPicker();
            }

            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        new TedPermission(getApplicationContext())
                .setPermissionListener(permissionlistener)
                .setGotoSettingButton(true)
                .setDeniedMessage("If you reject permission,you can not upload Images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }
}
