package za.co.metalojiq.classfinder.someapp.activity.Network;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.adapter.ImageAdapter;
import za.co.metalojiq.classfinder.someapp.model.NetworksCategory;

public class Networks extends AppCompatActivity implements ImageAdapter.OnCatClick {

    public static final String INTENT_EXTRA_CAT_POS = "cat_pos";
    public static final String INTENT_EXTRA_CAT_NAME = "cat_name";

    private ListView lvCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networks);

        lvCat = (ListView) findViewById(R.id.lvCategories);
        ImageAdapter imageAdapter = new ImageAdapter(this);
        imageAdapter.setOnCatClick(this);
        lvCat.setAdapter(imageAdapter);
    }

    @Override
    public void onCatClick(int position, NetworksCategory networksCategory) {
        Intent intent = new Intent(this, NetworksCatItem.class);
        intent.putExtra(INTENT_EXTRA_CAT_POS, position);
        intent.putExtra(INTENT_EXTRA_CAT_NAME, networksCategory.getName());
        startActivity(intent);



    }
}
