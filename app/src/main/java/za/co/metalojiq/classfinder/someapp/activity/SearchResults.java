package za.co.metalojiq.classfinder.someapp.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.fragment.AccomList;
import za.co.metalojiq.classfinder.someapp.activity.fragment.GetAccomFailed;
import za.co.metalojiq.classfinder.someapp.model.Accommodation;

import static za.co.metalojiq.classfinder.someapp.activity.Search.INTENT_RESPONSE_EXTRA;


/**
 * this class handles all search that is done on the app and launches the required fragment
 */
public class SearchResults extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();

        final ArrayList<Accommodation> accommodations = (ArrayList<Accommodation>) intent.getSerializableExtra(AccomList.ACCOM_BUNDLE_KEY);


        //get fragement manO
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();

        Search.INTENT_RESPONSE intentResponse = (Search.INTENT_RESPONSE) intent.getSerializableExtra(Companion.getINTENT_RESPONSE_EXTRA());
        if (intentResponse == Search.INTENT_RESPONSE.SUCCESS) {
            //todo unchecked cast
            AccomList accomList = AccomList.newInstance(accommodations, -1);
            transaction.add(R.id.activity_search_results, accomList, "ACCOM_LIST_FRAGMENT");
        } else {
            GetAccomFailed getAccomFailed = new GetAccomFailed();
            setTitle(R.string.app_name);
            transaction.add(R.id.activity_search_results, getAccomFailed, "ACCOM_FAIL_FRAGMENT");
        }

        transaction.commit();
    }

}
