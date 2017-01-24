package za.co.metalojiq.classfinder.someapp.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.adapter.UserAdapter;
import za.co.metalojiq.classfinder.someapp.model.Transaction;
import za.co.metalojiq.classfinder.someapp.model.TransactionResponse;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface;


//this should be a fragment loaded by the user class
public class Runner extends AppCompatActivity {

    private static final String TAG = Runner.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ViewPager mViewPager;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runner);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.LOGIN_PREF_FILENAME, MODE_PRIVATE);
        int id = sharedPreferences.getInt(LoginActivity.LOGIN_PREF_USER_ID, 0);

        boolean runner = sharedPreferences.getBoolean(LoginActivity.LOGIN_IS_RUNNER, false);
        Log.d(TAG,"User Email ma nikkka " +  sharedPreferences.getString(LoginActivity.LOGIN_PREF_EMAIL, "NO email oops"));


        if (id == 0) {
            Toast.makeText(this, "Please make sure that you signed in First", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        if (!runner) {
            Toast.makeText(this, "You are not a runner", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }


        mProgressBar = (ProgressBar) findViewById(R.id.runnerTransLoad);
        //should set the loading thing to gon here

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), id);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);



        mViewPager.setAdapter(mSectionsPagerAdapter);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.runnerTransLoad);
        progressBar.setVisibility(View.INVISIBLE);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_runner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements Callback<TransactionResponse>{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_POSITION = "transactions";
        private static final String ARG_RUNNER_ID = "runnerID";

        RecyclerView recyclerView;
        TextView tvError;
        ProgressBar mProgressBar;
        UserAdapter userAdapter;


        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int position, int runnerId) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
                args.putInt(ARG_POSITION, position);
                args.putInt(ARG_RUNNER_ID, runnerId);
                fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_runner, container, false);

            recyclerView = (RecyclerView) rootView.findViewById(R.id.runner_list);
            mProgressBar = (ProgressBar) rootView.findViewById(R.id.runnerTransLoad); // Todo should rename
            tvError = (TextView) rootView.findViewById(R.id.runnerErrorMsg);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//            if (!isNull) {
//                Log.d(TAG, "Transactions is here wait.... baam!!" + transactions);
//                transactions = (ArrayList<Transaction>) getArguments().getSerializable(ARG_POSITION);
//
//                if (transactions == null) {
//                    textView.setVisibility(View.VISIBLE);
//                } else if (transactions.size() == 0) {
//                    textView.setText("You have no current jobs.");
//                    textView.setVisibility(View.VISIBLE);
//                } else {
//                    recyclerView.setAdapter(new UserAdapter(transactions, R.layout.list_item_runner, getActivity()));
//
//                }
//            } else {
//                textView.setVisibility(View.VISIBLE);
//            }
            load(getArguments().getInt(ARG_POSITION), getArguments().getInt(ARG_RUNNER_ID));
            return rootView;
        }


            public void load(final int position, final int runnerId) {
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                switch (position) {
                    case 0:
                        Call<TransactionResponse> call = apiService.getRunner(runnerId, 1);
                        call.enqueue(this);
                        break;
                    case 1:
                        Call<TransactionResponse> callPaid = apiService.getRunner(runnerId, 2);
                        callPaid.enqueue(this);
                        break;
                }
            }

            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {  //TOdo can produce NullPointerException
                ArrayList<Transaction> transactions = response.body().getTransactions();
                userAdapter = new UserAdapter(transactions, R.layout.list_item_runner, getActivity());
                recyclerView.setAdapter(userAdapter);
//                recyclerView.postInvalidate();
                showProgress(false);
                if (transactions.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Good job you have no leads yet! contact Admin if you cant wait to get lead and get your cash at" +
                            "089 blah blah ");
                } else {
                   Log.d(TAG, "Number of element " + userAdapter.getItemCount());
                }
            }
            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                //BAD!!!
                Log.d(TAG, t.toString());
                recyclerView.setVisibility(View.GONE);
                showProgress(false);
                tvError.setVisibility(View.VISIBLE);

            }


        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        private void showProgress(final boolean show) {
            // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
            // for very easy animations. If available, use these APIs to fade-in
            // the progress spinner.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
                recyclerView.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

                mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                mProgressBar.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                mProgressBar.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        int runnerId;

        public SectionsPagerAdapter(FragmentManager fm, int runnerId) {
            super(fm);
            this.runnerId = runnerId;

        }
        Fragment fragment;
        @Override
        public Fragment getItem(int position) {
           return PlaceholderFragment.newInstance(position, runnerId);
        }

//        private Fragment getFragment(int position, int runnerId) {
//            LoadTransactions loadTransactions = new LoadTransactions(); // TODO(NOT REQUIRED) i Don't think tht its required to have this!!!!!!
//            loadTransactions.load(position, runnerId);
//            if (fragment == null) {
//                return  PlaceholderFragment.newInstance(null);
//            } else {
//                return fragment;
//            }
//        }




        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "View Requests";
                case 1:
                    return "Paid by Students";
            }
            return null;
        }
    }



}
