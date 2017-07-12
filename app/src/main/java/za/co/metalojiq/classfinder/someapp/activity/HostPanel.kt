package za.co.metalojiq.classfinder.someapp.activity

import android.support.design.widget.TabLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
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
import android.view.Menu
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
import kotlin.reflect.jvm.internal.impl.incremental.UtilsKt

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

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_host_panel, menu)
        return true
    }

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
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_host_panel, container, false)
            return rootView
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val swipeRefreshLayout = view.findViewById(R.id.swipeContainer) as SwipeRefreshLayout
            val recyclerView = view.findViewById(R.id.host_content_list) as RecyclerView
            swipeRefreshLayout.isRefreshing = true
            var transactionAdapter: TransactionAdapter? = null
            getTransactions(1) {
                swipeRefreshLayout.isRefreshing = false
                transactionAdapter = TransactionAdapter(it, R.layout.list_item_runner, activity)
                recyclerView.adapter = transactionAdapter
            }
            val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(activity)
            val scrollListener: EndlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    Log.d(TAG, "the page is $page")
                    if (transactionAdapter != null && page != 0) {
                        getTransactions(page + 1) {
                            transactionAdapter!!.addAll(it)
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


        private fun getTransactions(page: Int, onResponse: (v: ArrayList<Transaction>) -> Unit) {
            ApiClient.getClient().create(ApiInterface::class.java)
                    .getHostTrans(Utils.getUserId(activity), page)
                    .enqueue(object : Callback<TransactionResponse?> {

                        override fun onFailure(call: Call<TransactionResponse?>, t: Throwable?) {
                            Snackbar.make(view!!, "Please connect to the internet and then swipe down to reload", Snackbar.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<TransactionResponse?>?, response: Response<TransactionResponse?>) {
                            val transactions = response.body()?.transactions
                            if (transactions != null) {
                                if (transactions.isNotEmpty()) {
                                    onResponse(transactions) //dangerous name ayeye!! but ohh well...
                                } else {
                                    activity.runOnUiThread {
                                        Snackbar.make(view!!, "You have no booking yet!, Share links of your accommodations on social media to get attraction.", Snackbar.LENGTH_LONG).show()
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
            const val TAG = "__HOST_PANEL__"

            /**
             * Returns a new instance of this fragment for the given section
             * number. its not  0 indexed so no need to add one
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
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
            return rootView
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val swipeRefreshLayout = view.findViewById(R.id.swipeContainer) as SwipeRefreshLayout
            val recyclerView = view.findViewById(R.id.host_content_list) as RecyclerView
            swipeRefreshLayout.isRefreshing = true

            val hostUser = Utils.getUserId(activity)
            KtUtils.displayChatMessages("cf_$hostUser", recyclerView, activity, hostUser, 1, object: KtUtils.OnItemClickListener {
                override fun onItemClicked(hostId: Int, studentId: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private val ARG_SECTION_NUMBER = "section_number"
        const val TAG = "__HOST_PANEL__"

        /**
         * Returns a new instance of this fragment for the given section
         * number. its not  0 indexed so no need to add one
         */
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            val fragment = PlaceholderFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1)
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
