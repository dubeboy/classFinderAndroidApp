package za.co.metalojiq.classfinder.someapp.activity.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import gun0912.tedbottompicker.TedBottomPicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.AccomImageSlider;
import za.co.metalojiq.classfinder.someapp.activity.MainActivity;
import za.co.metalojiq.classfinder.someapp.adapter.AccomAdapter;
import za.co.metalojiq.classfinder.someapp.adapter.EndlessRecyclerViewScrollListener;
import za.co.metalojiq.classfinder.someapp.model.Accommodation;
import za.co.metalojiq.classfinder.someapp.model.AccommodationResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;

/**
 * This displays all the available accommodations
 */
public class AccomList extends Fragment {
    private static final String TAG = AccomList.class.getSimpleName();

    public static final String PICTURES_ARRAY_EXTRA = MainActivity.TAG + ".PICTURES_ARRAY_LIST";
    public static final String DOUBLE_PRICE_EXTRA = MainActivity.TAG + ".DOUBLE_PRICE";
    public static final String STRING_ROOM_TYPE_EXTRA = MainActivity.TAG + ".STRING_ROOM_TYPE";
    public static final String STRING_ROOM_DESC = MainActivity.TAG + ".STRING_ROOM_DESC";
    public static final String STRING_ROOM_LOCATION_EXTRA = MainActivity.TAG + ".STRING_ROOM_LOCATION";
    //Post strings for securing room----------------------------------------------------------------------
    public static final String POST_INT_HOST_ID = MainActivity.TAG + ".POST_STRING_SECURING_ROOM";
    public static final String ACCOM_BUNDLE_KEY = TAG + ".ACCOM_KEY";
    public static final String POST_ADVERT_ID = TAG + "POST_INT_ADVERT_ID";


    private EndlessRecyclerViewScrollListener scrollListener;
    private View linearLayout;
    private ProgressBar progressBar;
    private AccomAdapter accomAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean mCamPermissionGranted;

    public AccomList() {
        // Required empty public constructor
    }

    public static AccomList newInstance(ArrayList<Accommodation> accommodations) {
        Log.d(TAG, "Inherererereererere");
        Bundle args = new Bundle();
        args.putSerializable(ACCOM_BUNDLE_KEY, accommodations);
        AccomList fragment = new AccomList();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        linearLayout = inflater.inflate(R.layout.fragment_accom_list, container, false);

        final RecyclerView recyclerView = (RecyclerView) linearLayout.findViewById(R.id.recycler_view);
        final TextView textViewError = (TextView) linearLayout.findViewById(R.id.accomListTvError);
        progressBar = (ProgressBar) linearLayout.findViewById(R.id.accomLoad);



        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "Called");
                progressBar.setVisibility(View.VISIBLE);
                fetchAccomData(page);
            }
        };
        recyclerView.addOnScrollListener(scrollListener); //TODO test if position matters

        swipeRefreshLayout = (SwipeRefreshLayout) linearLayout.findViewById(R.id.swipeContainer);
        //TODO not working man
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchAccomData(0);
                Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(gridLayoutManager);

        //// TODO: 1/13/17  make it better please move this the onCreate Hook
        ArrayList<Accommodation> accommodations =
                (ArrayList<Accommodation>) getArguments().getSerializable(ACCOM_BUNDLE_KEY);

        if (accommodations != null) {  //I think its redundant.
            Log.d(TAG, "Number of elemets =" + accommodations.size());
            // I need to load the recycler view only if there are items to load!!!
            if (accommodations.size() > 0) {
                accomAdapter = new AccomAdapter(accommodations, R.layout.list_item_accom, getActivity().getApplicationContext(),
                        new AccomAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Accommodation accommodation) {
                                Intent intent = new Intent(getActivity().getApplicationContext(), AccomImageSlider.class);
                                intent.putStringArrayListExtra(PICTURES_ARRAY_EXTRA, accommodation.getImagesUrls()); // TODO: 1/11/17 google how to add an arraylist to a put extra
                                intent.putExtra(DOUBLE_PRICE_EXTRA, accommodation.getPrice());
                                intent.putExtra(STRING_ROOM_TYPE_EXTRA, accommodation.getRoomType());
                                intent.putExtra(STRING_ROOM_LOCATION_EXTRA, accommodation.getLocation());
                                intent.putExtra(STRING_ROOM_DESC, accommodation.getDescription());
                                intent.putExtra(POST_INT_HOST_ID, accommodation.getHostId());
                                Log.d(TAG, "Id of host is 3################################### " + accommodation.getHostId());
                                intent.putExtra(POST_ADVERT_ID, accommodation.getId());
                                startActivity(intent);
                            }
                        });
                recyclerView.setAdapter(accomAdapter);

            } else {
                //Todo Should be an If statement here to Id which fragment
                textViewError.setVisibility(View.VISIBLE);
            }
        }
        FloatingActionButton fab = (FloatingActionButton) linearLayout.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View view) {
                requestCameraPermissions();
                if (mCamPermissionGranted) {
                    Snackbar.make(view, "Please pick the images you would like to upload", Snackbar.LENGTH_SHORT).show();
                    createImagesBottomPicker();
                } else {

                }


            }
        });
        return linearLayout;

    }


    //get data from the server given the page
    private void fetchAccomData(final int page) {
        Log.d(TAG, "you scrolling to page: " + page);
//        if (page == 1) {
//            return;
//        }  // dont do anything if page is equal to one

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AccommodationResponse> call;
        //
        if (page == 0) {
            Log.d(TAG, "The page is here " + page);
            call = apiService.getAllAccommodations(1);
        } else {
            Log.d(TAG, "The page is here > 0 " + page);
            call = apiService.getAllAccommodations(page);
        }
        //FIXME I am not sure if there are new item this top item will show thos new items
        call.enqueue(new Callback<AccommodationResponse>() {
            @Override
            public void onResponse(Call<AccommodationResponse> call, Response<AccommodationResponse> response) {
                if (response.body().getResults().size() != 0) {
                    ArrayList<Accommodation> accommodations;
                    ArrayList<Accommodation> accommodationResults = response.body().getResults();

                    if (page == 0) {
                        Log.d(TAG, "Page is here reload");
                        accomAdapter.clear();
                        accomAdapter.addAll(accommodationResults);
                        scrollListener.resetState();
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        Log.d(TAG, "Page is here loadMore");
                        accommodations = (ArrayList<Accommodation>) getArguments().getSerializable(ACCOM_BUNDLE_KEY);
                        Log.d(TAG, "onResponse: Accommodations" + accommodations.size());
                        //TODO sould use addALL
                        for (Accommodation newAccom : accommodationResults) {
                            accommodations.add(newAccom);
                        }
                        accomAdapter.notifyItemRangeInserted(accomAdapter.getItemCount() + 1, response.body().getResults().size());
                    }
                    progressBar.setVisibility(View.GONE);
                } else {
                    Snackbar.make(linearLayout, "No more accommodationds to load. ", Snackbar.LENGTH_SHORT).show();
                    call.cancel();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<AccommodationResponse> call, Throwable t) {
                Log.d(TAG, t.toString());
                Snackbar.make(linearLayout, "Oops... failing to load more items. Please try connecting to the internet.",
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    //Todo should be moved to Utils the books might also need it
    private void createImagesBottomPicker() {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(getActivity())
                .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {

                    @Override
                    public void onImagesSelected(ArrayList<Uri> uriList) {
                        Bitmap[] bitmaps = new Bitmap[uriList.size()];
                        for (int i = 0; i < uriList.size(); i++) {
                            bitmaps[i] = BitmapFactory.decodeFile(uriList.get(i).getPath());

                        }
                    }
                })
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .create();

        bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager());
    }

    private void requestCameraPermissions() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                mCamPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                mCamPermissionGranted = false;
            }
        };

        new TedPermission(getActivity())
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }
}
