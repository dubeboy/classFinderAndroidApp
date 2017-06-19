package za.co.metalojiq.classfinder.someapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.model.House;

/**
 * Created by divine on 2017/06/16.
 */

public class HouseListAdapter extends RecyclerView.Adapter<HouseListAdapter.HouseViewHolder> {

    private final List<House> houseList;
    private final int rowLayout;
    private final HouseListAdapter.OnItemClickListener listener;
    private final Context context;

    public interface OnItemClickListener {
        void onItemClick(House h);
    }

    public HouseListAdapter(List<House> houseList, int rowLayout, Context context,
                            HouseListAdapter.OnItemClickListener listener) {
        this.houseList = houseList;
        this.rowLayout = rowLayout;
        this.listener = listener;
        this.context = context;

    }

    @Override
    public HouseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(rowLayout, viewGroup, false);

        return new HouseViewHolder(view
        );
    }

    @Override
    public void onBindViewHolder(HouseViewHolder houseViewHolder, int position) {
        houseViewHolder.bind(houseList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class HouseViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "HouseViewHolder";
        private final ImageView accomImageThumb;
        private final TextView tvLocation;
        private final TextView tvTitle;
        private final TextView tvRoomType;

        public HouseViewHolder(View itemView) {
            super(itemView);
            accomImageThumb = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_location);
            tvRoomType = (TextView) itemView.findViewById(R.id.tv_room_type);
        }

        void bind(final House house, final HouseListAdapter.OnItemClickListener listener) {
            tvLocation.setText(house.getWifi() + "\n" + house.isNsfas() + "\n" + house.isPrepaidElectricity() + "\n" + house.getCommon());
            String price = house.getAddress();
            tvTitle.setText(price);
            tvRoomType.setText(""); //Todo this should have the tot num people
            Log.d(TAG, house.getResults().get(0).getPicture(0).getImageUrl() + "^END%");
            if(house.getResults().size() > 0) {  // we want to this if the house has more than item
                Picasso.with(itemView.getContext())
                        .load(house
                                .getResults()
                                .get(0)
                                .getPicture(0)
                                .getImageUrl()
                        ).into(accomImageThumb);
            } else {

                //TODO: Should add place holder image
                accomImageThumb.setVisibility(View.GONE);
            }
//            built in onClick listener for a view.. nice!!
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(house);
                }
            });
        }
    }

    public void clear() {
        houseList.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<House> houses) {
        houseList.addAll(houses);
        notifyDataSetChanged();
    }
}
