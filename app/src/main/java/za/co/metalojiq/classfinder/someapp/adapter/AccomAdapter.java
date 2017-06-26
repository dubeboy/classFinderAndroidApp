package za.co.metalojiq.classfinder.someapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.activity.LoginActivity;
import za.co.metalojiq.classfinder.someapp.model.Accommodation;
import za.co.metalojiq.classfinder.someapp.rest.ApiClient;
import za.co.metalojiq.classfinder.someapp.util.Utils;

import java.util.ArrayList;
import java.util.List;


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

    public AccomAdapter(List<Accommodation> accommodations, int rowLayout, Context context,
                                                                                    OnItemClickListener listener) {
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
        holder.setActivity(context);
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

        private final ImageButton btnShare;
        private final ImageView accomImageThumb;
        private final TextView tvTitle;
        private final TextView tvLocation;
        private final TextView tvRoomType;
        private Context activity = null;

        AccomViewHolder(View itemView) {
            super(itemView);
            accomImageThumb = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_location);
            tvRoomType = (TextView) itemView.findViewById(R.id.tv_room_type);
            btnShare = (ImageButton) itemView.findViewById(R.id.btn_share);

        }



//        this function will do all the binding to the each item list todo: a bit inefficient
        void bind(final Accommodation accommodation, final OnItemClickListener listener) {
            tvLocation.setText(accommodation.getLocation());
            String price = "R " + accommodation.getPrice();
            tvTitle.setText(price);
            tvRoomType.setText(accommodation.getRoomType());
            Log.d(TAG,accommodation.getPicture(0).getImageUrl() + "^END%");
            Picasso.with(itemView.getContext())
                    .load( accommodation.getPicture(0)
                            .getImageUrl()).into(accomImageThumb);
//            built in onClick listener for a view.. nice!!
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(accommodation);
                }
            });

            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    String userToken = Utils.getUserSharedPreferences(activity)
                            .getString(LoginActivity
                                    .USER_LOGIN_TOKEN, "");
                    String url = ApiClient.DEV_HOST + "/api/v1/refs?token=" + userToken + "&accom_id=" + accommodation.getId();
                    intent.putExtra(Intent.EXTRA_TEXT, url);
                    intent.setType("text/plain");
                    activity.startActivity(intent);
                }
            });
        }

        public void setActivity(Context activity) {
            this.activity = activity;
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
