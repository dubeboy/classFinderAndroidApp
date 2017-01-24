package za.co.metalojiq.classfinder.someapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import za.co.metalojiq.classfinder.someapp.model.Picture;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;

/**
 * Created by divine on 1/11/17.
 */

public class AccomImageAdapter extends PagerAdapter{
    Context mContext;
    ArrayList<String> picturesUrls;
    public AccomImageAdapter(Context context, ArrayList<String> picturesUrls ) {
        this.mContext = context;
        this.picturesUrls = picturesUrls;
    }

    @Override
    public int getCount() {
        return picturesUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);

    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {

        ImageView mImageView = new ImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Picasso.with(mContext) //// TODO: move this to a util class
                .load(ApiClient.DEV_HOST + picturesUrls.get(i)).into(mImageView);
        ((ViewPager) container).addView(mImageView, 0);
        return mImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((ImageView) obj);
    }
}
