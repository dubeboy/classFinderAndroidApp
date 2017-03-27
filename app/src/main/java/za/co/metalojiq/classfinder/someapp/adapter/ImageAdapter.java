package za.co.metalojiq.classfinder.someapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.model.NetworksCategory;
import za.co.metalojiq.classfinder.someapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by divine on 3/19/17.
 */
public class ImageAdapter extends ArrayAdapter<NetworksCategory> implements View.OnClickListener {

    private static final String TAG = ImageAdapter.class.getSimpleName();

    private Context mContext;
    private static  List<NetworksCategory> networksCategories = new ArrayList<>();
    private OnCatClick onCatClick;

    static {
        //all of these need descs
        networksCategories.add(new NetworksCategory("Science"));
        networksCategories.add(new NetworksCategory("Education"));
        networksCategories.add(new NetworksCategory("Engineering"));
        networksCategories.add(new NetworksCategory("Law"));
        networksCategories.add(new NetworksCategory("Architecture And Design"));
        networksCategories.add(new NetworksCategory("Humanities"));
        networksCategories.add(new NetworksCategory("Commerce"));
        networksCategories.add(new NetworksCategory("Business"));
        networksCategories.add(new NetworksCategory("Music"));
        networksCategories.add(new NetworksCategory("Religion"));
        networksCategories.add(new NetworksCategory("Entertainment"));
    }
    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        ImageView info;
    }

    public ImageAdapter(Context context) {
        super(context, R.layout.categories_row, networksCategories);
        this.mContext=context;
    }



    //can be implemented bu the caller!! please do!!!

    public void setOnCatClick(OnCatClick onCatClick) {
        this.onCatClick = onCatClick;
    }

    public interface OnCatClick {
        void onCatClick(int position, NetworksCategory networksCategory);
    }


    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag(10);
        NetworksCategory networksCategory =  getItem(position);
        Log.d(TAG, "onClick: " + position + "< pos | > name: > " + ((NetworksCategory) networksCategories).getName());
        onCatClick.onCatClick(position, networksCategory);
    }

  //  private int lastPosition = -1;

    @Override
    public long getItemId(int position) {
        return networksCategories.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        NetworksCategory dataModel =  getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.categories_row, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.category_name);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.cat_image_view);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
//        lastPosition = position;


        TextDrawable drawable = Utils.getTextDrawable(dataModel);

        ImageView image = viewHolder.info;
        image.setImageDrawable(drawable);
        viewHolder.txtName.setText(dataModel.getName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworksCategory networksCategory =  getItem(position);
                Log.d(TAG, "onClick: " + position + "< pos | > name: > " + networksCategory.getName());
                onCatClick.onCatClick(position, networksCategory);
            }
        });
//        convertView.setTag position);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }


}
