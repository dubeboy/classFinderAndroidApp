package za.co.metalojiq.classfinder.someapp.activity

import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import za.co.metalojiq.classfinder.someapp.R
import za.co.metalojiq.classfinder.someapp.adapter.EndlessRecyclerViewScrollListener
import za.co.metalojiq.classfinder.someapp.adapter.TransactionAdapter
import za.co.metalojiq.classfinder.someapp.model.Transaction
import za.co.metalojiq.classfinder.someapp.model.TransactionResponse
import za.co.metalojiq.classfinder.someapp.rest.ApiClient
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface
import za.co.metalojiq.classfinder.someapp.util.KtUtils
import za.co.metalojiq.classfinder.someapp.util.Utils

class HostPanel : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_panel)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container) as ViewPager
        mViewPager!!.adapter = mSectionsPagerAdapter

        val tabLayout = findViewById(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)

    }


//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_host_panel, menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class HostBookings : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_host_panel, container, false)
            Log.d(TAG, "HostBookings: is created")
            return rootView
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val swipeRefreshLayout = view.findViewById(R.id.swipeContainer) as SwipeRefreshLayout
            val recyclerView = view.findViewById(R.id.host_content_list) as RecyclerView
            swipeRefreshLayout.isRefreshing = true
            var transactionAdapter: TransactionAdapter? = null
            getTransactions(view, swipeRefreshLayout,  1) {
                swipeRefreshLayout.isRefreshing = false
                transactionAdapter = TransactionAdapter(it, R.layout.list_item_runner, activity,  { transaction ->
                    Log.d(TAG, "The transaction row was clicked")
                    val intentForChatActivity = KtUtils.setIntentForChatActivity(
                            context,
                            transaction.hostId,
                            transaction.studentId,
                            transaction.roomAddress,
                            true,
                            transaction.studentEmail,
                            transaction.price,
                            transaction.roomType )
                    startActivity(intentForChatActivity)
                })
                recyclerView.adapter = transactionAdapter
            }
            val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(activity)
            val scrollListener: EndlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, recyclerView1: RecyclerView) {
                    Log.d(TAG, "the page is $page")
                    if (transactionAdapter != null && page != 0) {
                        getTransactions(recyclerView1, swipeRefreshLayout,  page + 1) {
                            transactionAdapter!!.addAll(it)
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }
            }
            swipeRefreshLayout.setOnRefreshListener({
                swipeRefreshLayout.isRefreshing = true
                scrollListener.resetState()
            })

            recyclerView.layoutManager = linearLayoutManager
            recyclerView.addOnScrollListener(scrollListener)
        }

        private fun getTransactions(view: View, swipeRefreshLayout: SwipeRefreshLayout,  page: Int, onResponse: (v: ArrayList<Transaction>) -> Unit) {
            ApiClient.getClient().create(ApiInterface::class.java)
                    .getHostTrans(Utils.getUserId(activity), page)
                    .enqueue(object : Callback<TransactionResponse?> {

                        override fun onFailure(call: Call<TransactionResponse?>, t: Throwable?) {
                            Utils.makeToast("Please connect to the internet and then swipe down to reload", context)
                            Log.d(TAG, "failed to connect " )
                            swipeRefreshLayout.isRefreshing = false
                        }

                        override fun onResponse(call: Call<TransactionResponse?>?, response: Response<TransactionResponse?>) {
                            val transactions = response.body()?.transactions
                            swipeRefreshLayout.isRefreshing = false
                            if (transactions != null) {
                                if (transactions.isNotEmpty()) {
                                    onResponse(transactions) //dangerous name ayeye!! but ohh well...
                                    Log.d(TAG, "got em " )
                                } else {
                                    activity.runOnUiThread {
                                        Utils.makeToast("You have no booking yet!, Share links of your accommodations on social media to get attraction", context)
                                    }
                                }
                            } else {
                                Log.d(TAG, "Oops server error, this should not happen sir...")
//
                            }
                        }
                    })
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"
            const val TAG = "__HOST_BOOKINGS__"

            /**
             * Returns a new instance of this fragment for the given section
             * number. its not  0 indexed so no need to add one
             */
            fun newInstance(sectionNumber: Int): HostBookings {
                val fragment = HostBookings()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }


    /*todo: these classes are too similar*/
    class HostChatsList : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_host_panel, container, false)
            Log.d(TAG, "On Fragment 2")
            return rootView
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val swipeRefreshLayout = view.findViewById(R.id.swipeContainer) as SwipeRefreshLayout
            val recyclerView = view.findViewById(R.id.host_content_list) as RecyclerView
            swipeRefreshLayout.isRefreshing = true

            Log.d(TAG, "View is created")
            val hostUser = Utils.getUserId(activity)
            KtUtils.displayChatMessages("cf_$hostUser", recyclerView, activity, hostUser, swipeRefreshLayout)

            swipeRefreshLayout.setOnRefreshListener({
                swipeRefreshLayout.isRefreshing = false  //because this windows auto upadtes there is no need th
            })

        }

        companion object {
            private val ARG_SECTION_NUMBER = "section_number"
            const val TAG = "__HOST_CHAT_LISTS__"

            /**
             * Returns a new instance of this fragment for the given section
             * number. its not  0 indexed so no need to add one
             */
            fun newInstance(sectionNumber: Int): HostChatsList {
                val fragment = HostChatsList()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }




    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
          return when(position) {
                0 -> {
                    Log.d("__SECTION_PAGER__", "paging to page!!! $position" )

                    HostBookings.newInstance(position + 1)
                }
                1 -> {
                    Log.d("__SECTION_PAGER__", "paging to page!!! $position" )
                    HostChatsList.newInstance(position + 1)
                }
                else -> null
            }
        }

        override fun getCount(): Int = 2 // Show 2 total pages.

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "BOOKINGS"
                1 -> return "CHATS"
            }
            return null
        }
    }
}
