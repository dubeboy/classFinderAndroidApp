package za.co.metalojiq.classfinder.someapp.activity.fragment

import android.content.Intent
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import za.co.metalojiq.classfinder.someapp.R
import za.co.metalojiq.classfinder.someapp.activity.AddHouseActivity
import za.co.metalojiq.classfinder.someapp.activity.MainActivity
import za.co.metalojiq.classfinder.someapp.activity.NewAccommodation
import za.co.metalojiq.classfinder.someapp.adapter.EndlessRecyclerViewScrollListener
import za.co.metalojiq.classfinder.someapp.adapter.HouseListAdapter
import za.co.metalojiq.classfinder.someapp.model.House
import za.co.metalojiq.classfinder.someapp.model.HousesResponse
import za.co.metalojiq.classfinder.someapp.rest.ApiClient
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface
import za.co.metalojiq.classfinder.someapp.util.Utils
import za.co.metalojiq.classfinder.someapp.util.Utils.isLoggedIn
import za.co.metalojiq.classfinder.someapp.util.Utils.makeToast
import kotlin.collections.ArrayList

/**
 * A placeholder fragment containing a simple view.
 */
class HouseActivityFragment : Fragment() {
        private lateinit var scrollListener: EndlessRecyclerViewScrollListener
        private lateinit var progressBar: ProgressBar
        private lateinit var houseAdapter: HouseListAdapter
        private lateinit var swipeRefreshLayout: SwipeRefreshLayout
        private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater?,
                                  container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            linearLayout = inflater!!.inflate(R.layout.fragment_accom_list, container, false)
            recyclerView = linearLayout.findViewById(R.id.recycler_view) as RecyclerView
            val gridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            //val userId : Int = arguments.getInt(MainActivity.USER_ID, -1)

            val userId = Utils.getUserId(activity)
            Log.d(TAG, "fetching initial house data for user : $userId")

            fetchHousesData(0, userId)

            progressBar = linearLayout.findViewById(R.id.accomLoad) as ProgressBar
            // val textViewError = linearLayout.findViewById(R.id.accomListTvError) as TextView



            scrollListener = object : EndlessRecyclerViewScrollListener(gridLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    Log.d(TAG, "Called")
                    progressBar.visibility = View.VISIBLE
                    fetchHousesData(page , userId)
                }
            }
            recyclerView.addOnScrollListener(scrollListener) //TODO test if position matters

            swipeRefreshLayout = linearLayout.findViewById(R.id.swipeContainer) as SwipeRefreshLayout
            //TODO not working man on emulator
            swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                                                                        android.R.color.holo_green_light,
                                                                        android.R.color.holo_orange_light,
                                                                        android.R.color.holo_red_light)
            swipeRefreshLayout.setOnRefreshListener({
                              fetchHousesData(0, userId)
                              Toast.makeText(activity, "Refresh", Toast.LENGTH_SHORT).show()
            })

            recyclerView.layoutManager = gridLayoutManager

            //// TODO: 1/13/17  make it better please move this the onCreate Hook

//            if (houses != null) {
//                Log.d(TAG, "Number of elemets =" + houses.size)
//                // I need to load the recycler view only if there are items to load!!!
//                if (houses!!.size > 0) {
//                    houseAdapter = AccomAdapter(houses, R.layout.list_item_accom, activity.applicationContext,
//                            AccomAdapter.OnItemClickListener { accommodation ->
//                                val intent = Intent(activity.applicationContext, AccomImageSlider::class.java)
//                                intent.putStringArrayListExtra(PICTURES_ARRAY_EXTRA, accommodation.imagesUrls) // TODO: 1/11/17 google how to add an arraylist to a put extra
//                                intent.putExtra(DOUBLE_PRICE_EXTRA, accommodation.price)
//                                intent.putExtra(STRING_ROOM_TYPE_EXTRA, accommodation.roomType)
//                                intent.putExtra(STRING_ROOM_LOCATION_EXTRA, accommodation.location)
//                                intent.putExtra(STRING_ROOM_DESC, accommodation.description)
//                                intent.putExtra(POST_INT_HOST_ID, accommodation.hostId)
//                                Log.d(TAG, "Id of host is ################################### " + accommodation.hostId!!)
//                                intent.putExtra(POST_ADVERT_ID, accommodation.id)
//                                startActivity(intent)
//                            })
//                    recyclerView.adapter = houseAdapter
//
//                } else {
//                    //Todo(FRAGMENT COMMIT ERROR) Should be an If statement here to Id which fragment
//                    //so that we can change the text to match wheather a person is just loading all accommodation
//                    textViewError.visibility = View.VISIBLE
//                }
//            } else {
//                Snackbar.make(linearLayout, "No houses found ")
//            }

            val fab = linearLayout.findViewById(R.id.fab) as FloatingActionButton
            fab.setOnClickListener {
                if (isLoggedIn(activity)) {
                    val intent = Intent(this@HouseActivityFragment.activity, AddHouseActivity::class.java)
                    startActivity(intent)
                } else {
                    fab.visibility = View.GONE
                    makeToast("Please sign in to access this action", context)
                }
            }
            return linearLayout
        }


        //get data from the server given the page
        private fun fetchHousesData(page: Int, userId: Int) {
            Log.d(TAG, "you scrolling to page: " + page)
            //        if (page == 1) {
            //            return;
            //        }  // dont do anything if page is equal to one

            val apiService = ApiClient.getClient().create<ApiInterface>(ApiInterface::class.java)
            val call: Call<HousesResponse>
            if (page == 0) {
                Log.d(TAG, "The page is here " + page)
                call = apiService.getHousesForUser(userId, 1 )
            } else {
                Log.d(TAG, "The page is here > 0 " + page)
                call = apiService.getHousesForUser(userId, page)
            }
            //FIXME I am not sure if there are new item this top item will show those new items
            call.enqueue(object : Callback<HousesResponse> {
                override fun onResponse(call: Call<HousesResponse>,
                                        response: Response<HousesResponse>) {
                    if(response.body() == null)
                        Snackbar.make(linearLayout, "An error happened please try again", Snackbar.LENGTH_SHORT)
                    if (response.body()?.houses?.size != 0) {
                        val houses: ArrayList<House> = ArrayList(response.body()!!.houses.size)
                        val housesResults = response.body()!!.houses

                        if (page == 0) {
                            Log.d(TAG, "Page is here reload")
                            houseAdapter = HouseListAdapter(houses, R.layout.list_item_accom, activity.applicationContext,
                                                                HouseListAdapter.OnItemClickListener { accommodation ->
//                                val intent = Intent(activity.applicationContext, AccomImageSlider::class.java)
//                                intent.putStringArrayListExtra(PICTURES_ARRAY_EXTRA, accommodation.imagesUrls) // TODO: 1/11/17 google how to add an arraylist to a put extra
//                                intent.putExtra(DOUBLE_PRICE_EXTRA, accommodation.price)
//                                intent.putExtra(STRING_ROOM_TYPE_EXTRA, accommodation.roomType)
//                                intent.putExtra(STRING_ROOM_LOCATION_EXTRA, accommodation.location)
//                                intent.putExtra(STRING_ROOM_DESC, accommodation.description)
//                                intent.putExtra(POST_INT_HOST_ID, accommodation.hostId)
//                                Log.d(TAG, "Id of host is ################################### " + accommodation.hostId!!)
//                                intent.putExtra(POST_ADVERT_ID, accommodation.id)
//                                startActivity(intent)
                                Snackbar.make(linearLayout, "You have clicked ", Snackbar.LENGTH_SHORT)
                            })
                            recyclerView.adapter = houseAdapter
                           // houseAdapter.clear()
//                            scrollListener.resetState()
                            swipeRefreshLayout.isRefreshing = false
                        } else {
                            Log.d(TAG, "Page is here loadMore")
                            Log.d(TAG, "onResponse: Houses" + houses.size)
                            //TODO sould use addALL
                            houses += housesResults  // this is awesome man
                            houseAdapter.notifyItemRangeInserted(houseAdapter.itemCount + 1, response.body()!!.houses.size)
                        }
                        progressBar.visibility = View.GONE
                    } else {
                        Snackbar.make(linearLayout, "No more accommodationds to load. ", Snackbar.LENGTH_SHORT).show()
                        call.cancel()
                    }
                    progressBar.visibility = View.GONE
                }

                override fun onFailure(call: Call<HousesResponse>, t: Throwable) {
                    Log.d(TAG, t.toString())
                    Snackbar.make(linearLayout, "Oops... failing to load more items. Please try connecting to the internet.",
                            Snackbar.LENGTH_SHORT).show()
                }
            })
        }

        companion object {
            private val TAG = HouseActivityFragment::class.simpleName

            val PICTURES_ARRAY_EXTRA = MainActivity.TAG + ".PICTURES_ARRAY_LIST"
            val DOUBLE_PRICE_EXTRA = MainActivity.TAG + ".DOUBLE_PRICE"
            val STRING_ROOM_TYPE_EXTRA = MainActivity.TAG + ".STRING_ROOM_TYPE"
            val STRING_ROOM_DESC = MainActivity.TAG + ".STRING_ROOM_DESC"
            val STRING_ROOM_LOCATION_EXTRA = MainActivity.TAG + ".STRING_ROOM_LOCATION"
            //Post strings for securing room----------------------------------------------------------------------
            val POST_INT_HOST_ID = MainActivity.TAG + ".POST_STRING_SECURING_ROOM"
            val ACCOM_BUNDLE_KEY = TAG + ".ACCOM_KEY"
            val POST_ADVERT_ID = TAG + "POST_INT_ADVERT_ID"

            private lateinit var linearLayout: View

            fun newInstance(usrId: Int): HouseActivityFragment {
                Log.d(TAG, "House fragment hello")
                val args = Bundle()  //might use this some day
                val fragment = HouseActivityFragment()
                fragment.arguments = args
                return fragment
            }
        }

}
