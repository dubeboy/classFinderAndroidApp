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
import za.co.metalojiq.classfinder.someapp.activity.HouseAccomsActivity
import za.co.metalojiq.classfinder.someapp.activity.MainActivity
import za.co.metalojiq.classfinder.someapp.adapter.EndlessRecyclerViewScrollListener
import za.co.metalojiq.classfinder.someapp.adapter.HouseListAdapter
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
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    private var progressBar: ProgressBar? = null
    private var houseAdapter: HouseListAdapter? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var linearLayout: View

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        linearLayout = inflater!!.inflate(R.layout.fragment_accom_list, container, false)
        recyclerView = linearLayout.findViewById(R.id.recycler_view) as RecyclerView
        swipeRefreshLayout = linearLayout.findViewById(R.id.swipeContainer) as SwipeRefreshLayout
        val fab = linearLayout.findViewById(R.id.fab) as FloatingActionButton
        val gridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        activity.title = "Your houses"
        val userId = Utils.getUserId(activity)
        progressBar = linearLayout.findViewById(R.id.accomLoad) as ProgressBar
        progressBar!!.setVisibility(View.GONE) // set the progressbar to be gone
        scrollListener = object : EndlessRecyclerViewScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                Log.d(TAG, "Called with page: $page")
                swipeRefreshLayout!!.isRefreshing = true
                fetchMoreHousesData(page, userId)
            }
        }
        recyclerView!!.addOnScrollListener(scrollListener) //TODO test if position matters
        swipeRefreshLayout!!.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
        swipeRefreshLayout!!.setOnRefreshListener({
            fetchMoreHousesData(0, userId)
            Toast.makeText(activity, "Refresh", Toast.LENGTH_SHORT).show()
        })
        recyclerView!!.layoutManager = gridLayoutManager
        fab.setOnClickListener {
            if (isLoggedIn(activity)) {
                val intent = Intent(this@HouseActivityFragment.activity, AddHouseActivity::class.java)
                startActivity(intent)
            } else {
                fab.visibility = View.GONE
                makeToast("Please sign in to access this action", context)
            }
        }
        fetchAndInitialiseFirstData(userId) // at last to this!
        return linearLayout
    }


    fun fetchAndInitialiseFirstData(userId: Int) {
        Log.d(TAG, "fetching initial house data for user : $userId")
        val apiService = ApiClient.getClient().create<ApiInterface>(ApiInterface::class.java)
        val call: Call<HousesResponse> = apiService.getHousesForUser(userId, 1)
        call.enqueue(object : Callback<HousesResponse?> {
            override fun onResponse(call: Call<HousesResponse?>?, response: Response<HousesResponse?>) {
                if (response.body() != null) {
                    val houses = response.body()!!.houses
                    if (houses!!.size == 0) {
                        Snackbar.make(linearLayout, "Sorry you have not add your houses to classFinder, press + button to add ",
                                Snackbar.LENGTH_LONG).show()
                    }
                    houseAdapter = HouseListAdapter(houses,
                            R.layout.list_item_accom, activity.applicationContext,
                            HouseListAdapter.OnItemClickListener {
                                house ->
                                val intent = Intent(activity.applicationContext, HouseAccomsActivity::class.java)
                                intent.putExtra(HOUSE_ID, house.id)
                                startActivity(intent)
                            })
                    recyclerView!!.adapter = houseAdapter
                    scrollListener!!.resetState()
                }
            }

            override fun onFailure(call: Call<HousesResponse?>?, t: Throwable?) {
                Snackbar.make(linearLayout, "Sorry classFinder error, we will be back shortly.",
                        Snackbar.LENGTH_LONG).show()
            }
        })
    }

    //get data from the server given the page
    private fun fetchMoreHousesData(page: Int, userId: Int) {
        Log.d(TAG, "you scrolling to page: " + page)
        //        if (page == 1) {
        //            return;
        //        }  // dont do anything if page is equal to one

        val apiService = ApiClient.getClient().create<ApiInterface>(ApiInterface::class.java)
        val call: Call<HousesResponse>
        if (page == 0) {
            Log.d(TAG, "The page is here " + page)
            call = apiService.getHousesForUser(userId, 1)
        } else {
            Log.d(TAG, "The page is here > 0 " + page)
            call = apiService.getHousesForUser(userId, page)
        }
        //FIXME I am not sure if there are new item this top item will show those new items
        call.enqueue(object : Callback<HousesResponse> {
            override fun onResponse(call: Call<HousesResponse>,
                                    response: Response<HousesResponse>) {
                if (response.body() == null)
                    Snackbar.make(linearLayout, "An error happened please try again", Snackbar.LENGTH_SHORT)
                if (response.body()!!.houses!!.size != 0) {
                    val houses = response.body()!!.houses

                    if (page == 0) {
                        Log.d(TAG, "Page is here reload")
                        Log.d(TAG, "the houses is: $houses")
                        houseAdapter!!.clear()
                        houseAdapter!!.addAll(ArrayList(houses))
                        scrollListener!!.resetState()
                    } else {
                        Log.d(TAG, "Page is here loadMore")
                        Log.d(TAG, "onResponse: Houses" + houses.size)
                        //TODO sould use addALL
                        houseAdapter!!.addAll(ArrayList(houses))
                        houseAdapter!!.notifyItemRangeInserted(houseAdapter!!.itemCount + 1, response.body()!!.houses.size)
                    }
                    swipeRefreshLayout!!.isRefreshing = false
                } else {
                    Snackbar.make(linearLayout, "No more accommodationds to load. ", Snackbar.LENGTH_SHORT).show()
                    call.cancel()
                }
                swipeRefreshLayout!!.isRefreshing = false
            }

            override fun onFailure(call: Call<HousesResponse>, t: Throwable) {
                Log.d(TAG, t.toString())
                Snackbar.make(linearLayout, "Oops... failing to load more items. " +
                        "Please try connecting to the internet.",
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
        val POST_INT_HOST_ID = MainActivity.TAG + ".POST_STRING_SECURING_ROOM"
        val ACCOM_BUNDLE_KEY = TAG + ".ACCOM_KEY"
        val POST_ADVERT_ID = TAG + "POST_INT_ADVERT_ID"
        val HOUSE_ID = TAG + "HOUSE_ID"

        fun newInstance(usrId: Int): HouseActivityFragment {
            Log.d(TAG, "House fragment hello")
            val args = Bundle()  //might use this some day
            val fragment = HouseActivityFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
