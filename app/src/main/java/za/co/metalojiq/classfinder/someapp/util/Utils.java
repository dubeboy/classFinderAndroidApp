package za.co.metalojiq.classfinder.someapp.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.ArrayRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.HashMap;

import gun0912.tedbottompicker.TedBottomPicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.LoginActivity;
import za.co.metalojiq.classfinder.someapp.activity.NewAccommodation;
import za.co.metalojiq.classfinder.someapp.activity.fragment.BookSearchFaculty;
import za.co.metalojiq.classfinder.someapp.model.NetworksCategory;
import za.co.metalojiq.classfinder.someapp.model.StatusRespose;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;

import static android.content.Context.MODE_PRIVATE;
import static za.co.metalojiq.classfinder.someapp.activity.LoginActivity.LOGIN_IS_RUNNER;
import static za.co.metalojiq.classfinder.someapp.activity.LoginActivity.LOGIN_PREF_USER_ID;

/**
 * Created by divine on 1/28/17.
 */

//todo: should change this to a kotlin object class
public class Utils               {

    public static final String[] LOCATIONS = {"Auckland Park", "Braamfontein", "Doornfontein", "Soweto"};
    public static final String[] INSTITUTIONS = {"Wits", "UJ-Auckland Park", "UJ-DFC", "UJ-Soweto", "Other"};
    public static final String AUCK_AREA_PREFIX = "Auckland Park, ";

    //cannot instantiate this class
    private Utils() {}

    /**
     *
     * this method setup a general spinner
     *
     * @param context - the activity which is going to render this spinner
     * @param resId - the spinner that we dealing with :)
     * @param arrResId - the xml res ID which as an array of elements to populate the array with
     * @return - the spinner which has been created, so that we can make any required modification :)
     */
    public static Spinner setupSpinner(AppCompatActivity context, @IdRes int resId, @ArrayRes int arrResId) {
        Spinner spinner = (Spinner) context.findViewById(resId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(context, arrResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        return spinner;
    }

    //These 2 can be combined
    public static int genIDForSelectedFaculty(String faculty) {
        for (int i = 0; i < BookSearchFaculty.FACULTIES.length; i++) {
            if(BookSearchFaculty.FACULTIES[i].equals(faculty))
                return i+2; // the server is 1 based an the first element is accommodation
        }
        return -1;
    }

    public static int genIDForSelectedInstitution(String institution) {
        for (int i = 0; i < INSTITUTIONS.length; i++) {
            if(INSTITUTIONS[i].equals(institution))
                return i+1;
        }
        return -1;
    }


    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
//        Log.d("Utils", password);
        return password.length() >= 4;
    }

    @NonNull
    public static GoogleApiClient getGoogleSignUp(Context context,
                                                  GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("928443959691-bvf8ptbuphctke0sfcifocms8ejogchi.apps.googleusercontent.com")
                .requestEmail()
                .build();

        return new GoogleApiClient.Builder(context)
                .enableAutoManage((FragmentActivity) context /* FragmentActivity */, onConnectionFailedListener /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    //fixme please really bad!!!
    public static boolean isEmailValid(String email) {
        //TODO: Replace regex NB!!!!!!!!
        return email.contains("@");
    }

    public static SharedPreferences getUserSharedPreferences(Context context) {
        return context.getSharedPreferences(LoginActivity.LOGIN_PREF_FILENAME, MODE_PRIVATE);
    }

    public static boolean isLoggedIn(Context context) {
        return (getUserSharedPreferences(context).getInt(LOGIN_PREF_USER_ID, 0) > 0) ;
    }


    public static boolean isRunner(Context context) {
        return getUserSharedPreferences(context).getBoolean(LOGIN_IS_RUNNER, false);
    }

    public static void makeToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public static void logIt(String id, String msg) {
        Log.d(id, msg);
    }

    public static String getMimeType(String image) {
        int indexOf = image.lastIndexOf(".") + 1;
        String s = image.substring(indexOf);
        if (s.equals("jpg")) {
            s = "jpeg";
        }
        return "image/" + s;
    }

    public static int getUserId(Context context ) {
        SharedPreferences userSharedPreferences = getUserSharedPreferences(context);
        return userSharedPreferences.getInt(LoginActivity.LOGIN_PREF_USER_ID, 0);
    }

    //TODO should be implemented
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.activity_networks_cat_item, networkPost, "NETWORK_POST_FRAGMENT_2");
//        fragmentTransaction.commit();

    //        //NetworkPost networkPost = NetworkPost.newInstance(networkCatId, netWorksName, new ArrayList<NetworkPostModel>());
//        NetworkTopicFragment networkTopicFragment = NetworkTopicFragment.newInstance(networkCatId);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.activity_networks_cat_item, networkTopicFragment, "NETWORK_POST_FRAGMENT_2");
//        fragmentTransaction.commit();
    public static class LocationItemListener implements AdapterView.OnItemSelectedListener {

        //TextView tvAuck;
        Spinner auckAreaSpinner;

        public LocationItemListener(Spinner auckAreaSpinner) {
           // this.tvAuck = tvAuck;
            this.auckAreaSpinner = auckAreaSpinner;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selected = (String) parent.getItemAtPosition(position);
            if (selected.equals(LOCATIONS[0])) {
              //  tvAuck.setVisibility(View.VISIBLE);
                auckAreaSpinner.setVisibility(View.VISIBLE);
                auckAreaSpinner.setSelection(-1);
            } else {
                //tvAuck.setVisibility(View.GONE);
                auckAreaSpinner.setVisibility(View.GONE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    //For generating alphabets of the users profile
    public static TextDrawable getTextDrawable(NetworksCategory dataModel) {
        return TextDrawable.builder()
                .buildRound(dataModel.getName().charAt(0) + "", genColor(dataModel.getName()));
    }

    public static TextDrawable getTextDrawable(String theWord) {
        return TextDrawable.builder()
                .buildRound(theWord.charAt(0) + "", genColor(theWord));
    }


    public static int genColor(String key) {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        return generator.getColor(key);

    }
    @Nullable
    public static Intent shareButtonIntent(final int accommodationId, Context activity) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        String userToken = Utils.getUserSharedPreferences(activity)
                .getString(LoginActivity
                        .USER_LOGIN_TOKEN, "");
        if (!userToken.equals("")) {
            String url = ApiClient.DEV_HOST + "/api/v1/refs?token=" + userToken + "&accom_id=" + accommodationId;
            intent.putExtra(Intent.EXTRA_TEXT, url);
            intent.setType("text/plain");
            // this is where we send data to the internet
            ApiClient.getClient().create(ApiInterface.class)
                    .shareRef(userToken, accommodationId)
                    .enqueue(new Callback<StatusRespose>() {
                        @Override
                        public void onResponse(Call<StatusRespose> call, Response<StatusRespose> response) {
                            //todo: add some logic
                        }

                        @Override
                        public void onFailure(Call<StatusRespose> call, Throwable t) {

                        }
                    });
            return intent;
        } else {
            Toast.makeText(activity, "Please Sign in before you can share accommodation and start getting some money", Toast.LENGTH_LONG).show();
            return null;
        }
    }


    // image upload stuff

    enum KEYS {BITMAP_ARRAY, IMAGES_URL_ARRAY}




    public interface OnImagesSelected {
        void onImagesSelected(Bitmap[] bitmaps, String[] imagesUrls);
    }

    private static void createImagesBottomPicker(final Context context,
                                                 FragmentManager fragmentManager,
                                                 final LinearLayout imagesContainer,
                                                 final OnImagesSelected onImagesSelected) {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(context)
                .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(ArrayList<Uri> uriList) {
                        int numImages = uriList.size();
                        Bitmap[] bitmaps = new Bitmap[numImages];
                        if (numImages > 0) {
                            imagesContainer.removeAllViews();
                            String[] imageUris = new String[numImages];
                            ImageView previewImages[] = new ImageView[numImages];
                            HorizontalScrollView imagesHoriScrollView = (HorizontalScrollView) ((AppCompatActivity) context).findViewById(R.id.newImagesScrollView);
                            imagesHoriScrollView.setVisibility(View.VISIBLE);

                            for (int i = 0; i < numImages; i++) {
                                bitmaps[i] = BitmapFactory.decodeFile(uriList.get(i).getPath());
                                imageUris[i] = uriList.get(i).getPath();
                                previewImages[i] = new ImageView(context);
                                previewImages[i].setAdjustViewBounds(true);
                                previewImages[i].setLayoutParams(new ViewGroup.LayoutParams(240, 240));
                                previewImages[i].setPadding(5, 0, 5, 0);
                                previewImages[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
                                previewImages[i].setImageBitmap(bitmaps[i]);
                                imagesContainer.addView(previewImages[i]);
                            }
                            onImagesSelected.onImagesSelected(bitmaps, imageUris);
                        } else {
                            Toast.makeText(context, "The please select atleast on image", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Upload")
                .setEmptySelectionText("No image selected for upload")
                .create();
        bottomSheetDialogFragment.show(fragmentManager);
    }

    private static void requestCameraPermissions(final Context context,
                                                 final FragmentManager fragmentManager,
                                                 final LinearLayout imagesContainer,
                                                 final OnImagesSelected onImagesSelected) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(context, "Please select images ypu want to upload.", Toast.LENGTH_SHORT).show();
                createImagesBottomPicker(context, fragmentManager, imagesContainer, onImagesSelected);
            }
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        new TedPermission(context)
                .setPermissionListener(permissionlistener)
                .setGotoSettingButton(true)
                .setDeniedMessage("If you reject permission,you can not upload Images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    public static void launchImagesPicker(final Context context,
                                           FragmentManager fragmentManager,
                                           final LinearLayout imagesContainer,
                                           final OnImagesSelected onImagesSelected) {

        requestCameraPermissions(context, fragmentManager, imagesContainer, onImagesSelected);
    }

}
