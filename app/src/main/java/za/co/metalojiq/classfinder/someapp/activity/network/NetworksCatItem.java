package za.co.metalojiq.classfinder.someapp.activity.network;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.LoginActivity;
import za.co.metalojiq.classfinder.someapp.activity.fragment.NetworkTopicFragment;
import za.co.metalojiq.classfinder.someapp.activity.fragment.NewNetworkTopic;

import static za.co.metalojiq.classfinder.someapp.util.Utils.getUserSharedPreferences;


// class makes  a request to get all the
public class NetworksCatItem extends AppCompatActivity {

    private int networkCatId;
    private NETWORK_TYPE networkType;
    private SearchView searchView;
    private static final String TAG = "NetworksCatItem";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_networks_item, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_network_search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_questions:
                SharedPreferences sharedPreferences = getUserSharedPreferences(this);
                int id = sharedPreferences.getInt(LoginActivity.LOGIN_PREF_USER_ID, 0);
                showAddNewNetworkDialogFragment(id, networkCatId + 1 );
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networks_cat_item);
        Intent intent = getIntent();
        // 0 based but server is 1 based
        networkCatId = intent.getIntExtra(Networks.INTENT_EXTRA_CAT_POS, -1);
       final  String netWorksName = intent.getStringExtra(Networks.INTENT_EXTRA_CAT_NAME);
        // Initial NetworkType load load the posts!
        networkType = NETWORK_TYPE.POST;
        setTitle(netWorksName + " " + networkType.toString().toLowerCase() + "s");

        //I have to load the fragment here giving it the id of the network + 1
        //and it will handle the fetching of the data depending on the position
        final NetworkTopicFragment networkTopicFragment = NetworkTopicFragment.newInstance(networkCatId, netWorksName, networkType);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_cf_networks:
                        networkType = NETWORK_TYPE.POST;
                        selectedFragment = networkTopicFragment;
                        break;
                    case R.id.action_cf_question:
                        networkType = NETWORK_TYPE.QUESTION;
                        selectedFragment = NetworkTopicFragment.newInstance(networkCatId, netWorksName, networkType);
                        break;
                }
                setTitle(netWorksName + " " + networkType.toString().toLowerCase() + "s");
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, networkTopicFragment);
        transaction.commit();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "onCreate: Search result's" + query);

        } else {
           // getInitBooks( null, null);
        }

    }


    private void showAddNewNetworkDialogFragment(int userId, int networkCategoryId) {
        FragmentManager fm = getSupportFragmentManager();
        NewNetworkTopic editNameDialogFragment = NewNetworkTopic.newInstance(userId, networkCategoryId, networkType);
        editNameDialogFragment.show(fm, "fragment_new_network_topic");
    }
}
