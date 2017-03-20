package za.co.metalojiq.classfinder.someapp.activity.Network;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.fragment.NetworkPost;

public class NetworksCatItem extends AppCompatActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_networks_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_questions:
//                Intent intent = new Intent(this, Search.class);
//                startActivity(intent);
                return true;
        }
        // the fas
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networks_cat_item);

        Intent intent = getIntent();
        // 0 based but server is 1 based
        int cat = intent.getIntExtra(Networks.INTENT_EXTRA_CAT_POS, -1);
        String netWorksName = intent.getStringExtra(Networks.INTENT_EXTRA_CAT_NAME);
        setTitle(netWorksName);

        //I have to load the fragment here giving it the id of the network + 1
        //and it will handle the fetching of the data depending on the position

        NetworkPost networkPost = NetworkPost.newInstance(cat, netWorksName );


        //TODO should be put in tye utils class
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activity_networks_cat_item, networkPost, "NETWORK_POST_FRAGMENT");
        fragmentTransaction.commit(); //this is causing problem #Fixme
    }
}
