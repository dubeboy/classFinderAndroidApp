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
import za.co.metalojiq.classfinder.someapp.model.Accommodation;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;


/**
 * Created by divine on 1/10/17.
 */

//view holder is a inner class which actually has instancies of our xml elements
public class AccomAdapter extends RecyclerView.Adapter<AccomAdapter.AccomViewHolder> {
    private static final String TAG = AccomAdapter.class.getSimpleName();
    List<Accommodation> accommodations;
    private int rowLayout;
    private Context context;
    private final OnItemClickListener listener;

    public AccomAdapter(List<Accommodation> accommodations, int rowLayout, Context context, OnItemClickListener listener) {
        this.accommodations = accommodations;
        this.rowLayout = rowLayout;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public AccomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        inflate it first so that the variable become available in R
        View view = LayoutInflater.from(parent.getContext())
                .inflate(rowLayout, parent, false);
        return new AccomViewHolder(view);
    }


    //the holder object represents the current item that we working at
    @Override
    public void onBindViewHolder(AccomViewHolder holder, final int position) {
        holder.bind(accommodations.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return accommodations.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Accommodation accommodation);
    }

    static class AccomViewHolder extends RecyclerView.ViewHolder {

        ImageView accomImageThumb;
        TextView tvTitle;
        TextView tvLocation;
        TextView tvRoomType;
        AccomViewHolder(View itemView) {
            super(itemView);
            accomImageThumb = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_location);
            tvRoomType = (TextView) itemView.findViewById(R.id.tv_room_type);
        }

//        this function will do all the binding to the each item list todo: a bit inefficient
        void bind(final Accommodation accommodation, final OnItemClickListener listener) {
            tvLocation.setText(accommodation.getLocation());
            String price = "R " + accommodation.getPrice();
            tvTitle.setText(price);
            tvRoomType.setText(accommodation.getRoomType());
            Log.d(TAG,ApiClient.DEV_HOST + accommodation.getPicture(0).getImageUrl() + "^END%");
            Picasso.with(itemView.getContext())
                    .load(ApiClient.DEV_HOST + accommodation.getPicture(0)
                            .getImageUrl()).into(accomImageThumb);
//            built in onClick listener for a view.. nice!!
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(accommodation);
                }
            });
        }
    }

    public void clear() {
        accommodations.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Accommodation> accoms) {
        accommodations.addAll(accoms);
        notifyDataSetChanged();
    }
}
