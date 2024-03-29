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

import static za.co.metalojiq.classfinder.someapp.rest.ApiClient.DEV_HOST;

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
        return houseList.size();
    }

    static class HouseViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "__HouseViewHolder__";
        private final ImageView accomImageThumb;
        private final TextView tvLocation;
        private final TextView tvTitle;
        private final TextView tvRoomType;

        HouseViewHolder(View itemView) {
            super(itemView);
            accomImageThumb = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_location);
            tvRoomType = (TextView) itemView.findViewById(R.id.tv_room_type);
        }

        void bind(final House house, final HouseListAdapter.OnItemClickListener listener) {
            String wifi = house.getWifi().trim().equals("true") ? "wifi: Available": "wifi: Not available";
            String nsfas = house.isNsfas() ? "NSFAS: Yes" : "NSFAS: No";
            String prePaidElectricity = house.isPrepaidElectricity() ? "Prepaid elctricity: Yes" : "Prepaid elctricity: No";

            tvLocation.setText(wifi + "\n" + nsfas + "\n" + prePaidElectricity + "\n" + house.getCommon().trim());
            String address = house.getAddress();
            tvTitle.setText(address);
            tvRoomType.setText(""); //Todo this should have the tot num people

            if (house.getPictures()!= null && house.getPictures().size() > 0) {  // we want to this if the house has more than item
                Log.d(TAG,"the image URL is this: "  +  house.getPictures().get(0).getImageUrl());
                Picasso.with(itemView.getContext())
                        .load(DEV_HOST + house
                                .getPictures()
                                .get(0)
                                .getImageUrl()
                        ).into(accomImageThumb);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(house);
                }
            });
        }
    }

    public void clear() {
        if (houseList.size() > 0) {
            houseList.clear();
            notifyDataSetChanged();
        }
    }

    public void addAll(ArrayList<House> houses) {
        houseList.addAll(houses);
        notifyDataSetChanged();
    }
}
