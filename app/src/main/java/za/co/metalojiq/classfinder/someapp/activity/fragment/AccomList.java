package za.co.metalojiq.classfinder.someapp.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.AccomImageSlider;
import za.co.metalojiq.classfinder.someapp.activity.MainActivity;
import za.co.metalojiq.classfinder.someapp.activity.NewAccommodation;
import za.co.metalojiq.classfinder.someapp.adapter.AccomAdapter;
import za.co.metalojiq.classfinder.someapp.adapter.EndlessRecyclerViewScrollListener;
import za.co.metalojiq.classfinder.someapp.model.Accommodation;
import za.co.metalojiq.classfinder.someapp.model.AccommodationResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;

import java.util.ArrayList;
/**
 * This displays all the available accommodations
 */
public class AccomList extends Fragment implements AccomAdapter.OnItemClickListener {
    private static final String TAG = AccomList.class.getSimpleName();


    //todo: too long TAGS
    public static final String PICTURES_ARRAY_EXTRA = MainActivity.TAG + ".PICTURES_ARRAY_LIST";
    public static final String DOUBLE_PRICE_EXTRA = MainActivity.TAG + ".DOUBLE_PRICE";
    public static final String STRING_ROOM_TYPE_EXTRA = MainActivity.TAG + ".STRING_ROOM_TYPE";
    public static final String STRING_ROOM_DESC = MainActivity.TAG + ".STRING_ROOM_DESC";
    public static final String STRING_ROOM_LOCATION_EXTRA = MainActivity.TAG + ".STRING_ROOM_LOCATION";
    public static final String STRING_ROOM_ADDRESS_EXTRA = MainActivity.TAG + ".STRING_ROOM_ADDRESS";
    public static final String STRING_ROOM_CITY_EXTRA = MainActivity.TAG + ".STRING_ROOM_CITY";
    //Post strings for securing room----------------------------------------------------------------------
    public static final String POST_INT_HOST_ID = MainActivity.TAG + ".POST_STRING_SECURING_ROOM";
    public static final String ACCOM_BUNDLE_KEY = TAG + ".ACCOM_KEY";
    public static final String POST_ADVERT_ID = TAG + "POST_INT_ADVERT_ID";
    public static final String DOUBLE_ROOM_DEPOSIT_EXTRA = "DOUBLE_DEPOSIT";
    public static final String OBJECT_ROOM_HOUSE = "houseObject";
    private Button btnShare;


    private EndlessRecyclerViewScrollListener scrollListener;
    private View linearLayout;
    private ProgressBar progressBar;
    private AccomAdapter accomAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean mCamPermissionGranted;

    public AccomList() {
        // Required empty public constructor
    }

    // this is really good because I would like show diffrent accom based on who is calling!!
    public static AccomList newInstance(ArrayList<Accommodation> accommodations, int houseId) {
        Log.d(TAG, "Inherererereererere");
        Bundle args = new Bundle();
        args.putSerializable(ACCOM_BUNDLE_KEY, accommodations);
        args.putInt(HouseActivityFragment.Companion.getHOUSE_ID(), houseId);
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
            public void onLoadMore(int page, int totalItemsCount, RecyclerView recyclerView1) {
                Log.d(TAG, "Called");
                progressBar.setVisibility(View.VISIBLE);
                    fetchAccomData(page);
                swipeRefreshLayout.setRefreshing(false);
            }
        };


        recyclerView.addOnScrollListener(scrollListener); //TODO test if position matters

        swipeRefreshLayout = (SwipeRefreshLayout) linearLayout.findViewById(R.id.swipeContainer);
        //TODO not working man on emulator
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scrollListener.resetState();
                if(accomAdapter != null)  accomAdapter.clear();
                Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
                fetchAccomData(0);
            }
        });

        recyclerView.setLayoutManager(gridLayoutManager);

        //// TODO: 1/13/17  make it better please move this the onCreate Hook
        ArrayList<Accommodation> accommodations =
                (ArrayList<Accommodation>) getArguments().getSerializable(ACCOM_BUNDLE_KEY);
        final int houseId = getArguments().getInt(HouseActivityFragment.Companion.getHOUSE_ID(), -1);

        if (accommodations != null) {  //I think its redundant.
            Log.d(TAG, "Number of elemets =" + accommodations.size());
            // I need to load the recycler view only if there are items to load!!!
         //   if (accommodations.size() > 0) {
                accomAdapter = new AccomAdapter(accommodations,
                        R.layout.list_item_accom, getActivity().getApplicationContext(), this);
                recyclerView.setAdapter(accomAdapter);

       //     }
// else {
//                //Todo(FRAGMENT COMMIT ERROR) Should be an If statement here to Id which fragment
//                //so that we can change the text to match wheather a person is just loading all accommodation
//                textViewError.setVisibility(View.VISIBLE);
//            }
        }

        final FloatingActionButton fab = (FloatingActionButton) linearLayout.findViewById(R.id.fab);
        if (houseId < 1) fab.setVisibility(View.GONE);  //hide fab if house id =< 0
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccomList.this.getActivity(), NewAccommodation.class);
                intent.putExtra(HouseActivityFragment.Companion.getHOUSE_ID(), houseId);
                startActivity(intent);
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
            call = apiService.getAllAccommodations(page + 1);
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
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<AccommodationResponse> call, Throwable t) {
                Log.d(TAG, t.toString());
                Snackbar.make(linearLayout, "Oops... failing to load more items. Please try connecting to the internet.",
                        Snackbar.LENGTH_SHORT).show();
                //todo: hise
            }
        });
    }

    private void startAcomDetailsActivity(@NonNull Accommodation accommodation) {
        Log.d(TAG, "startAcomDetailsActivity: its has been clicked dude ");
        Intent intent = new Intent(getActivity().getApplicationContext(), AccomImageSlider.class);
        intent.putStringArrayListExtra(PICTURES_ARRAY_EXTRA, accommodation.getImagesUrls()); // TODO: 1/11/17 google how to add an arraylist to a put extra
        intent.putExtra(DOUBLE_PRICE_EXTRA, accommodation.getPrice());
        intent.putExtra(STRING_ROOM_TYPE_EXTRA, accommodation.getRoomType());
        intent.putExtra(STRING_ROOM_LOCATION_EXTRA, accommodation.getHouse().getLocation());
        intent.putExtra(STRING_ROOM_ADDRESS_EXTRA, accommodation.getHouse().getAddress());
        intent.putExtra(STRING_ROOM_CITY_EXTRA, accommodation.getHouse().getCity());
        String desc = "Description: " + accommodation.getDescription();
        intent.putExtra(STRING_ROOM_DESC,desc);
        intent.putExtra(OBJECT_ROOM_HOUSE, accommodation.getHouse());
        intent.putExtra(POST_INT_HOST_ID, accommodation.getHostId());
        intent.putExtra(DOUBLE_ROOM_DEPOSIT_EXTRA,String.valueOf(accommodation.getDeposit()));

        Log.d(TAG, "Id of host is ################################### " + accommodation.getHostId());
        intent.putExtra(POST_ADVERT_ID, accommodation.getId());
        startActivity(intent);
    }


    @Override
    public void onItemClick(Accommodation accommodation) {
        Log.d(TAG, "onItemClick: its clicked man ");
        startAcomDetailsActivity(accommodation);


    }
}
