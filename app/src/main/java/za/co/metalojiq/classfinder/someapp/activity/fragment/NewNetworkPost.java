package za.co.metalojiq.classfinder.someapp.activity.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import za.co.metalojiq.classfinder.someapp.activity.LoginActivity;
import za.co.metalojiq.classfinder.someapp.model.network.NetworkPostModel;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;

import java.io.File;
import java.util.ArrayList;

import static za.co.metalojiq.classfinder.someapp.util.Utils.*;

/**
 * Created by divine on 3/20/17.
 */
public class NewNetworkPost extends BottomSheetDialogFragment {

    private static final String ARGS_CAT_ID = "cat_id";
    private static final String TAG = NewNetworkPost.class.getSimpleName();
    private static final String ARGS_CAT_NAME = "cat_name";
    private static final String ARGS_TOPIC_ID = "topic_id";
    private EditText mShareNote;
    private Button btnUploadImages;
    private TextView tvCat;
    private LinearLayout imagesContainer;
    private Button btnPost;
    private Bitmap[] bitmaps;
    private String[] imageUris;
    private ProgressDialog dialog;

    public static NewNetworkPost newInstance(int catId, int topicId, String catName) {
        Bundle args = new Bundle();
        args.putInt(ARGS_CAT_ID, catId);
        args.putInt(ARGS_TOPIC_ID, topicId);
        args.putString(ARGS_CAT_NAME, catName);
        NewNetworkPost newNetworkPost = new NewNetworkPost();
        newNetworkPost.setArguments(args);
        return newNetworkPost;
    }

    public NewNetworkPost() {
        // Required empty public constructor
    }

    // the container is the parent layout
    @Override @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_new_network_post, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mShareNote = (EditText) view.findViewById(R.id.etShareNote);
        mShareNote.setHint("Write some thing in " + getArguments().getString(ARGS_CAT_NAME));
        tvCat = (TextView) view.findViewById(R.id.tvCat);
        imagesContainer = (LinearLayout) view.findViewById(R.id.imgContainer);
        btnPost = (Button) view.findViewById(R.id.btnPost);
        btnUploadImages = (Button) view.findViewById(R.id.btnAddImages);
        String title = getArguments().getString(ARGS_CAT_NAME, "");
        tvCat.setText("in " + title);
        getDialog().setTitle("Share your Thoughts in the " + title + " CF Network");
        mShareNote.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        btnUploadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchImagesPicker();
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {
                    dialog = ProgressDialog.show(getContext(), "",
                            "Please wait sharing your thoughts in " + getArguments().getString(ARGS_CAT_NAME), true);
                    uploadData();
                } else {
                    makeToast("Please fix your form.", getContext());
                }
            }
        });
    }

    private boolean isFormValid() {

         if (TextUtils.isEmpty(mShareNote.getText().toString())) {
             mShareNote.setError("Please Write something about what you are sharing");
            return false;
        }
        return true;
    }


    // this upload the data to the server
    private void uploadData() {
        String networkDesc = mShareNote.getText().toString();
        int catId = getArguments().getInt(ARGS_CAT_ID);
        int topicId = getArguments().getInt(ARGS_TOPIC_ID);
        MultipartBody.Builder builderNew = new MultipartBody.Builder().setType(MultipartBody.FORM);
        MultipartBody requestBody = null;
        if (imageUris != null ) {   //MMMH
            for (String imageUri : imageUris) {
                File file = new File(imageUri);
                String mime = getMimeType(imageUri);
                Log.d(TAG, "the mime is " + mime);
                RequestBody reqFile = RequestBody.create(MediaType.parse(mime), file);
                //                 MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), reqFile);
                builderNew.addFormDataPart("images[]", file.getName(), reqFile);
                requestBody = builderNew.build();
            }
        }

        // the uId should never be 0;
           int uId = getUserSharedPreferences(getContext()).getInt(LoginActivity.LOGIN_PREF_USER_ID, 0);
            RequestBody netDesc = RequestBody.create(MediaType.parse("text/plain"), networkDesc);
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), Integer.valueOf(uId).toString());
            RequestBody tpcId = RequestBody.create(MediaType.parse("text/plain"), Integer.valueOf(topicId).toString());
          //  RequestBody categoryId = RequestBody.create(MediaType.parse("text/plain"), Integer.valueOf(catId).toString()); // networks id

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

           //catId + 1 becuase the server is one based fam!
            Call<NetworkPostModel> call = apiService.postNetworkPost(catId + 1, tpcId,  netDesc, userId, requestBody == null ? null : requestBody.parts());
            call.enqueue(new Callback<NetworkPostModel>() {
                @Override
                public void onResponse(Call<NetworkPostModel> call, Response<NetworkPostModel> response) {
                    Log.d(TAG, "onResponse: the status is:" + response.body().isStatus());
                    if (response.body().isStatus()) {
                        makeToast("uploaded, pull down to see your post", getContext());
                        dialog.dismiss(); // blocking IO no!!!
                        dismiss();
                    } else {
                        makeToast("Sorry please try again something went wrong double check your submission", getContext());
                        dialog.dismiss();
                        dismiss();
                    }
                }
                @Override
                public void onFailure(Call<NetworkPostModel> call, Throwable t) {
                    Log.e(TAG, t.toString());
                    dialog.dismiss();
                    makeToast("Please connect to the internet", getContext());
                }
            });
        }


    private void launchImagesPicker() {
        requestCameraPermissions();
    }

    private void createImagesBottomPicker() {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(getContext())
                .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {

                    @Override
                    public void onImagesSelected(ArrayList<Uri> uriList) {
                        int numImages = uriList.size();
                        bitmaps = new Bitmap[numImages];
                        if (numImages > 0) {
                            imagesContainer.removeAllViews();
                            imageUris = new String[numImages];
                            ImageView previewImages[] = new ImageView[numImages];
                            HorizontalScrollView imagesHoriScrollView = (HorizontalScrollView) getView().findViewById(R.id.newImagesScrollView);
                            imagesHoriScrollView.setVisibility(View.VISIBLE);

                            for (int i = 0; i < numImages; i++) {
                                bitmaps[i] = BitmapFactory.decodeFile(uriList.get(i).getPath());
                                imageUris[i] = uriList.get(i).getPath();
                                previewImages[i] = new ImageView(getContext());
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

        bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager());
    }

    private void requestCameraPermissions() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getContext(), "Please select images you want to upload.", Toast.LENGTH_SHORT).show();
                createImagesBottomPicker();
            }
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        new TedPermission(getContext())
                .setPermissionListener(permissionlistener)
                .setGotoSettingButton(true)
                .setDeniedMessage("If you reject permission,you can not upload Images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }
}
