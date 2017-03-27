package za.co.metalojiq.classfinder.someapp.activity.network;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.fragment.NetworkPost;
import za.co.metalojiq.classfinder.someapp.model.NetworkPostModel;

import java.util.ArrayList;

import static za.co.metalojiq.classfinder.someapp.activity.fragment.NetworkTopicFragment.NETWORK_CAT_ID;
import static za.co.metalojiq.classfinder.someapp.activity.fragment.NetworkTopicFragment.NETWORK_NAME;

public class NetworkPostActivity extends AppCompatActivity {

    private static final String TAG_FRAGMENT = "tag_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_post);

        Intent intent = getIntent();
        int mNetworkCatId = intent.getIntExtra(NETWORK_CAT_ID, 0);
        String mNetworkName = intent.getStringExtra(NETWORK_NAME);

        NetworkPost networkPost = NetworkPost.newInstance(mNetworkCatId, mNetworkName, new ArrayList<NetworkPostModel>());
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activity_network_post, networkPost, TAG_FRAGMENT)
                .commit();
    }
}
